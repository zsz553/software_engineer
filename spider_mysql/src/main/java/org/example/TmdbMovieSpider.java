package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class TmdbMovieSpider {

    private static final String API_KEY = "c45aa5ccccb8fab05d9a762d2a0be323";

    public List<TmdbMovie> fetchTop100() {
        List<TmdbMovie> movies = new ArrayList<>();

        try {
            for (int page = 1; page <= 5; page++) {
                String url = "https://api.themoviedb.org/3/movie/top_rated"
                        + "?api_key=" + API_KEY
                        + "&language=zh-CN"
                        + "&page=" + page;

                JSONObject jsonObject = requestJson(url);
                JSONArray results = jsonObject.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    if (movies.size() >= 100) {
                        break;
                    }

                    JSONObject item = results.getJSONObject(i);

                    int tmdbId = item.getInt("id");
                    int rankNo = movies.size() + 1;
                    String title = item.optString("title");
                    String originalTitle = item.optString("original_title");
                    double rating = item.optDouble("vote_average");
                    int ratingCount = item.optInt("vote_count");
                    int releaseYear = parseYear(item.optString("release_date"));
                    String language = item.optString("original_language");
                    String overview = item.optString("overview");
                    String posterPath = item.optString("poster_path");

                    String genre =
                            convertGenre(item.getJSONArray("genre_ids"));

                    movies.add(new TmdbMovie(
                            tmdbId,
                            rankNo,
                            title,
                            originalTitle,
                            rating,
                            ratingCount,
                            releaseYear,
                            language,
                            genre,
                            overview,
                            posterPath
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("获取 TMDB 电影数据失败：" + e.getMessage(), e);
        }

        return movies;
    }

    private JSONObject requestJson(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        return new JSONObject(response.toString());
    }

    private int parseYear(String releaseDate) {
        if (releaseDate == null || releaseDate.length() < 4) {
            return 0;
        }

        return Integer.parseInt(releaseDate.substring(0, 4));
    }
    private String convertGenre(JSONArray genreIds) {

        Map<Integer, String> genreMap = new HashMap<>();

        genreMap.put(28, "Action");
        genreMap.put(12, "Adventure");
        genreMap.put(16, "Animation");
        genreMap.put(18, "Drama");
        genreMap.put(35, "Comedy");
        genreMap.put(80, "Crime");
        genreMap.put(99, "Documentary");
        genreMap.put(10749, "Romance");
        genreMap.put(878, "Science Fiction");
        genreMap.put(53, "Thriller");
        genreMap.put(14, "Fantasy");
        genreMap.put(36, "History");
        genreMap.put(10752, "War");
        genreMap.put(9648, "Mystery");

        List<String> genres = new ArrayList<>();

        for (int i = 0; i < genreIds.length(); i++) {
            int id = genreIds.getInt(i);

            if (genreMap.containsKey(id)) {
                genres.add(genreMap.get(id));
            }
        }

        return String.join(", ", genres);
    }
}