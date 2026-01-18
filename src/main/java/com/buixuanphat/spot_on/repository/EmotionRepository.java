package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmotionRepository extends JpaRepository<Emotion,Integer> {
    List<Emotion> findAllByPost_Id(Integer id);

    Emotion findByPost_IdAndUser_Id(int postId, int userId);
}
