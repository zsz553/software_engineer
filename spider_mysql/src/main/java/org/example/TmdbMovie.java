package org.example;

public class TmdbMovie {
    private int tmdbId;
    private int rankNo;
    private String title;
    private String originalTitle;
    private double rating;
    private int ratingCount;
    private int releaseYear;
    private String language;
    private String genre;
    private String overview;
    private String posterPath;

    public TmdbMovie(int tmdbId, int rankNo, String title, String originalTitle,
                     double rating, int ratingCount, int releaseYear,
                     String language, String genre, String overview, String posterPath) {
        this.tmdbId = tmdbId;
        this.rankNo = rankNo;
        this.title = title;
        this.originalTitle = originalTitle;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.releaseYear = releaseYear;
        this.language = language;
        this.genre = genre;
        this.overview = overview;
        this.posterPath = posterPath;
    }

    public int getTmdbId() { return tmdbId; }
    public int getRankNo() { return rankNo; }
    public String getTitle() { return title; }
    public String getOriginalTitle() { return originalTitle; }
    public double getRating() { return rating; }
    public int getRatingCount() { return ratingCount; }
    public int getReleaseYear() { return releaseYear; }
    public String getLanguage() { return language; }
    public String getGenre() { return genre; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
}