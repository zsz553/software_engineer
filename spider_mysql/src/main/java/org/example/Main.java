package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        MySqlHelper db = new MySqlHelper();

        String createTableSql = """
                CREATE TABLE IF NOT EXISTS tmdb_movie_top100 (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    tmdb_id INT,
                    rank_no INT,
                    title VARCHAR(255),
                    original_title VARCHAR(255),
                    rating DECIMAL(3,1),
                    rating_count INT,
                    release_year INT,
                    language VARCHAR(50),
                    genre VARCHAR(255),
                    overview TEXT,
                    poster_path TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        db.executeUpdate(createTableSql);

        db.executeUpdate("TRUNCATE TABLE tmdb_movie_top100");

        TmdbMovieSpider spider = new TmdbMovieSpider();
        List<TmdbMovie> movies = spider.fetchTop100();

        String insertSql = """
                INSERT INTO tmdb_movie_top100
                (tmdb_id, rank_no, title, original_title, rating, rating_count,
                 release_year, language, genre, overview, poster_path)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        for (TmdbMovie movie : movies) {
            db.executeUpdate(
                    insertSql,
                    movie.getTmdbId(),
                    movie.getRankNo(),
                    movie.getTitle(),
                    movie.getOriginalTitle(),
                    movie.getRating(),
                    movie.getRatingCount(),
                    movie.getReleaseYear(),
                    movie.getLanguage(),
                    movie.getGenre(),
                    movie.getOverview(),
                    movie.getPosterPath()
            );
        }

        System.out.println("TMDB 电影 Top100 已成功存入数据库");
    }
}