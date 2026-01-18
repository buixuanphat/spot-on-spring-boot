package com.buixuanphat.spot_on.service;


import com.buixuanphat.spot_on.dto.event.EventResponseDTO;
import com.buixuanphat.spot_on.dto.merchandise.MerchandiseResponseDTO;
import com.buixuanphat.spot_on.dto.post.CreateImageDTO;
import com.buixuanphat.spot_on.dto.post.CreatePostDTO;
import com.buixuanphat.spot_on.dto.post.PostResponseDTO;
import com.buixuanphat.spot_on.dto.user.UserPublicInfoResponseDTO;
import com.buixuanphat.spot_on.entity.*;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.mapper.EventMapper;
import com.buixuanphat.spot_on.repository.*;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {

    PostRepository postRepository;

    UserRepository userRepository;
    UserService userService;

    EventRepository eventRepository;

    CloudinaryService cloudinaryService;

    ImageRepository imageRepository;

    EventMapper eventMapper;

    EmotionRepository emotionRepository;

    CommentRepository commentRepository;

    @NonFinal
    Boolean isLiked = false;

    @Transactional
    public PostResponseDTO create(CreatePostDTO request, List<MultipartFile> imagesRequest) {
        System.err.println(request.toString());
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng"));

        Event event = new Event();
        if (request.getEventId() != null) {
            event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy sự kiện"));
        }

        Post post = Post.builder()
                .user(user)
                .caption(request.getCaption())
                .createdDate(Instant.now())
                .build();

        if (request.getEventId() != null) {
            post.setEvent(event);
        }

        Post savedPost = postRepository.save(post);


        List<Image> images = new ArrayList<>();
        if (imagesRequest != null) {
            imagesRequest.forEach(i -> {
                String url = cloudinaryService.uploadImage(i).get("url");
                Image image = new Image();
                image.setImage(url);
                image.setPost(savedPost);
                images.add(imageRepository.save(image));
            });
        }


        return convert(savedPost, request.getUserId());
    }


    public Page<PostResponseDTO> getPosts(String kw, @Nullable Integer userId, int page, int size) {
        Specification<Post> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (kw != null && !kw.trim().isEmpty()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("caption")),
                                "%" + kw.toLowerCase() + "%"
                        )
                );
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Post> posts = postRepository.findAll(specification, pageable);

        return posts.map(p ->
        {
            return convert(p, userId);
        });

    }

    public PostResponseDTO getPost(int id)
    {
        Post post = postRepository.findById(id).orElseThrow(()->new AppException(HttpStatus.NO_CONTENT.value(), "Không tìm thấy bài viết"));

        return convert(post, null);

    }



    public String deletePost(int id)
    {
        Post post = postRepository.findById(id).orElseThrow(()->new AppException(HttpStatus.NO_CONTENT.value(), "Không tìm thấy bài viết"));

        postRepository.delete(post);

        return "Đã xóa thành công";

    }



    PostResponseDTO convert(Post post, Integer userId) {
        PostResponseDTO response = new PostResponseDTO();
        response.setId(post.getId());
        response.setCaption(post.getCaption());
        response.setCreatedDate(DateUtils.instantToString(post.getCreatedDate()));
        response.setUser(userService.convertToUserDTO(post.getUser().getId()));
        response.setEvent(eventMapper.toEventResponseDTO(post.getEvent()));

        List<Image> images = imageRepository.findAllByPost_Id(post.getId());
        List<String> imagesUrl = new ArrayList<>();
        images.forEach(image -> {
            imagesUrl.add(image.getImage());
        });
        response.setImages(imagesUrl);

        List<Emotion> emotions = emotionRepository.findAllByPost_Id(post.getId());
        response.setEmotions(emotions.size());

        if (userId != null) {
            emotions.forEach(e -> {
                if (e.getUser().getId().equals(userId)) isLiked = true;
            });
        }


        List<Comment> comments = commentRepository.findAllByPost_Id(post.getId());
        response.setComments(comments.size());
        response.setIsLiked(isLiked);
        isLiked = false;

        return response;
    }
}
