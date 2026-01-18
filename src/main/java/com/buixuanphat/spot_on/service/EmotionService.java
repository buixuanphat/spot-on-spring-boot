package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.entity.Emotion;
import com.buixuanphat.spot_on.entity.Post;
import com.buixuanphat.spot_on.entity.User;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.repository.EmotionRepository;
import com.buixuanphat.spot_on.repository.PostRepository;
import com.buixuanphat.spot_on.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE,makeFinal = true)
public class EmotionService {

    EmotionRepository emotionRepository;

    UserRepository userRepository;

    PostRepository postRepository;

    public Boolean create(int postId, int userId)
    {
        Emotion emotion = emotionRepository.findByPost_IdAndUser_Id(postId, userId);
        if(emotion==null)
        {
            emotion = new Emotion();
            User user = userRepository.findById(userId).orElseThrow(()-> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng"));
            Post post = postRepository.findById(postId).orElseThrow(()-> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy bài viết"));
            emotion.setPost(post);
            emotion.setUser(user);
            emotionRepository.save(emotion);
            return true;
        }
        else
        {
            emotionRepository.delete(emotion);
            return false;
        }
    }


}
