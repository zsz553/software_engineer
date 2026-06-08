package org.example;

public class HotSearch {

    private int rankNo;
    private String title;
    private String hotIndex;
    private String url;

    public HotSearch(int rankNo, String title, String hotIndex, String url) {
        this.rankNo = rankNo;
        this.title = title;
        this.hotIndex = hotIndex;
        this.url = url;
    }

    public int getRankNo() {
        return rankNo;
    }

    public String getTitle() {
        return title;
    }

    public String getHotIndex() {
        return hotIndex;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "HotSearch{" +
                "rankNo=" + rankNo +
                ", title='" + title + '\'' +
                ", hotIndex='" + hotIndex + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}