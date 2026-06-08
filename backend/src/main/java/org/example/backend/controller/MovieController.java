package org.example.backend.controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.example.backend.entity.Movie;
import org.example.backend.repository.MovieRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // 查询所有电影
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepository.findAllByOrderByRankNoAsc();
    }

    // 评分最高 Top10
    @GetMapping("/top10")
    public List<Movie> getTop10Movies() {
        return movieRepository.findTop10ByOrderByRatingDesc();
    }

    // 年份分布
    @GetMapping("/year-stats")
    public Map<Integer, Long> getYearStats() {
        List<Movie> movies = movieRepository.findAll();

        Map<Integer, Long> result = new TreeMap<>();

        for (Movie movie : movies) {
            Integer year = movie.getReleaseYear();

            if (year != null && year != 0) {
                result.put(year, result.getOrDefault(year, 0L) + 1);
            }
        }

        return result;
    }

    // 语言分布
    @GetMapping("/language-stats")
    public Map<String, Long> getLanguageStats() {
        List<Movie> movies = movieRepository.findAll();

        Map<String, Long> result = new HashMap<>();

        for (Movie movie : movies) {
            String language = movie.getLanguage();

            if (language != null && !language.isEmpty()) {
                result.put(language, result.getOrDefault(language, 0L) + 1);
            }
        }

        return result;
    }

    // 类型分布
    @GetMapping("/genre-stats")
    public Map<String, Long> getGenreStats() {
        List<Movie> movies = movieRepository.findAll();

        Map<String, Long> result = new HashMap<>();

        for (Movie movie : movies) {
            String genre = movie.getGenre();

            if (genre != null && !genre.isEmpty()) {
                String[] genres = genre.split(",");

                for (String g : genres) {
                    String name = g.trim();
                    result.put(name, result.getOrDefault(name, 0L) + 1);
                }
            }
        }

        return result;
    }
}