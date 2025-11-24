package com.buixuanphat.spot_on.repository;

import com.buixuanphat.spot_on.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
}
