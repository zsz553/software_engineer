package org.example.backend.repository;

import org.example.backend.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Integer> {

    List<Movie> findTop10ByOrderByRatingDesc();

    List<Movie> findAllByOrderByRankNoAsc();
}