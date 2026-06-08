package org.example;

public class DoubanMovie {
    private int rankNo;
    private String title;
    private double rating;
    private int ratingCount;
    private int releaseYear;
    private String region;
    private String genre;
    private String detailUrl;

    public DoubanMovie(int rankNo, String title, double rating, int ratingCount,
                       int releaseYear, String region, String genre, String detailUrl) {
        this.rankNo = rankNo;
        this.title = title;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.releaseYear = releaseYear;
        this.region = region;
        this.genre = genre;
        this.detailUrl = detailUrl;
    }

    public int getRankNo() { return rankNo; }
    public String getTitle() { return title; }
    public double getRating() { return rating; }
    public int getRatingCount() { return ratingCount; }
    public int getReleaseYear() { return releaseYear; }
    public String getRegion() { return region; }
    public String getGenre() { return genre; }
    public String getDetailUrl() { return detailUrl; }

    @Override
    public String toString() {
        return rankNo + " " + title + " " + rating + " " + releaseYear;
    }
}