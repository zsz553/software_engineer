package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubanMovieSpider {

    public List<DoubanMovie> fetchTop100() {
        List<DoubanMovie> movies = new ArrayList<>();

        try {
            for (int start = 0; start < 100; start += 25) {
                String url = "https://movie.douban.com/top250?start=" + start;

                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .timeout(10000)
                        .get();

                Elements items = doc.select(".item");

                for (Element item : items) {
                    if (movies.size() >= 100) {
                        break;
                    }

                    int rankNo = Integer.parseInt(item.select(".pic em").text());
                    String title = item.select(".title").first().text();
                    String detailUrl = item.select(".hd a").attr("href");

                    double rating = Double.parseDouble(
                            item.select(".rating_num").text()
                    );

                    String starText = item.select(".star").text();
                    int ratingCount = parseRatingCount(item);

                    Element infoElement = item.select(".bd p").first();

                    int releaseYear = 0;
                    String region = "";
                    String genre = "";

                    if (infoElement != null) {
                        String infoHtml = infoElement.html();
                        String[] lines = infoHtml.split("<br>");

                        if (lines.length >= 2) {
                            String metaLine = Jsoup.parse(lines[1]).text().trim();

                            String[] parts = metaLine.split("\\s*/\\s*");

                            if (parts.length >= 1) {
                                releaseYear = parseYear(parts[0]);
                            }

                            if (parts.length >= 2) {
                                region = parts[1].trim();
                            }

                            if (parts.length >= 3) {
                                genre = parts[2].trim();
                            }
                        }
                    }
                    DoubanMovie movie = new DoubanMovie(
                            rankNo,
                            title,
                            rating,
                            ratingCount,
                            releaseYear,
                            region,
                            genre,
                            detailUrl
                    );

                    movies.add(movie);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("爬取豆瓣电影失败：" + e.getMessage(), e);
        }

        return movies;
    }

    private int parseRatingCount(Element item) {
        String text = item.select(".star span").text();
        Pattern pattern = Pattern.compile("(\\d+)人评价");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return 0;
    }

    private int parseYear(String text) {
        Pattern pattern = Pattern.compile("\\d{4}");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }

        return 0;
    }
    private String[] fetchDetailInfo(String detailUrl) {
        try {
            Document doc = Jsoup.connect(detailUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            String infoText = doc.select("#info").text();

            String region = "";
            String genre = "";

            Pattern regionPattern = Pattern.compile("制片国家/地区: (.*?) 语言:");
            Matcher regionMatcher = regionPattern.matcher(infoText);
            if (regionMatcher.find()) {
                region = regionMatcher.group(1).trim();
            }

            Pattern genrePattern = Pattern.compile("类型: (.*?) 制片国家/地区:");
            Matcher genreMatcher = genrePattern.matcher(infoText);
            if (genreMatcher.find()) {
                genre = genreMatcher.group(1).trim();
            }

            return new String[]{region, genre};

        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }
}