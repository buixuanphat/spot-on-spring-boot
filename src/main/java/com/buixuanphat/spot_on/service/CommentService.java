package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.comment.CommentResponseDTO;
import com.buixuanphat.spot_on.dto.comment.CreateCommentDTO;
import com.buixuanphat.spot_on.entity.Comment;
import com.buixuanphat.spot_on.entity.Post;
import com.buixuanphat.spot_on.entity.User;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.repository.CommentRepository;
import com.buixuanphat.spot_on.repository.PostRepository;
import com.buixuanphat.spot_on.repository.UserRepository;
import com.buixuanphat.spot_on.utils.DateUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {

    CommentRepository commentRepository;

    PostRepository postRepository;

    UserRepository userRepository;
    UserService userService;

    public CommentResponseDTO create (CreateCommentDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng"));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy bài viết"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(request.getContent());
        comment.setCreatedDate(Instant.now());

        if(request.getParentId()!=null)
        {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(()->new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy bài viết"));
            comment.setParent(parent);
        }
        Comment savedComment = commentRepository.save(comment);

        return convert(savedComment);
    }


    public Page<CommentResponseDTO> getParentComments(int postId, int page, int size)
    {
        Specification<Comment> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("post").get("id"), postId));

            predicates.add(cb.isNull(root.get("parent")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Comment> comments = commentRepository.findAll(specification ,pageable);

        return comments.map(this::convert);
    }



    public Page<CommentResponseDTO> getChildComments(int parentId, int page, int size)
    {
        Specification<Comment> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("parent").get("id"), parentId));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Comment> comments = commentRepository.findAll(specification ,pageable);

        return comments.map(this::convert);
    }


    public Integer getNumberOfComment(int postId)
    {
        return commentRepository.countByPost_Id(postId);
    }


    CommentResponseDTO convert (Comment comment)
    {
        CommentResponseDTO response = new  CommentResponseDTO();
        response.setId(comment.getId());
        response.setUser(userService.convertToUserDTO(comment.getUser().getId()));
        response.setPostId(comment.getPost().getId());
        response.setContent(comment.getContent());
        response.setCreatedDate(DateUtils.instantToString(comment.getCreatedDate()));
        if(comment.getParent()!=null)
        {
            response.setParentId(comment.getParent().getId());
        }
        List<Comment> children = commentRepository.findByParent_Id(comment.getId());
        response.setChildrens(children.size());
        return response;
    }
}
