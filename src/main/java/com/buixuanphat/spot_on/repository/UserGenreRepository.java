package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.Event;
import com.buixuanphat.spot_on.entity.UserGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.cdi.JpaRepositoryExtension;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGenreRepository extends JpaRepository<UserGenre, Integer> {
    Optional<UserGenre> findByUser_IdAndGenre_Id(int userId, int genreId);


    @Query(
            value =
"""
        WITH top_genre AS (
            SELECT genre_id, value
            FROM user_genre
            WHERE user_id = :userId
            ORDER BY value DESC
            LIMIT 5
        ),
        ranked_event AS (
            SELECT\s
                e.*,
                tg.value AS genre_value,
                ROW_NUMBER() OVER (
                    PARTITION BY e.genre_id
                    ORDER BY e.id DESC
                ) AS rn
            FROM event e
            JOIN top_genre tg ON e.genre_id = tg.genre_id
        )
        SELECT *
        FROM ranked_event
        WHERE rn <= 4
        ORDER BY genre_value DESC, genre_id, id DESC;
        
""",
            nativeQuery = true
    )
    List<Event> getRecomment(@Param("userId") Integer userId);
}
