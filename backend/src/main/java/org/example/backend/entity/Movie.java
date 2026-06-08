package org.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tmdb_movie_top100")
@Data
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tmdb_id")
    private Integer tmdbId;

    @Column(name = "rank_no")
    private Integer rankNo;

    private String title;

    @Column(name = "original_title")
    private String originalTitle;

    private Double rating;

    @Column(name = "rating_count")
    private Integer ratingCount;

    @Column(name = "release_year")
    private Integer releaseYear;

    private String language;

    private String genre;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(name = "poster_path")
    private String posterPath;
}