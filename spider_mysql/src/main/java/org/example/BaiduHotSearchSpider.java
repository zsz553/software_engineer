package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BaiduHotSearchSpider {

    private static final String API_URL =
            "https://top.baidu.com/api/board?platform=wise&tab=realtime";

    public List<HotSearch> fetchTop10() {
        List<HotSearch> result = new ArrayList<>();
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0"
            );
            connection.setRequestProperty(
                    "Referer",
                    "https://top.baidu.com/board?tab=realtime"
            );
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray contentArray = jsonObject
                    .getJSONObject("data")
                    .getJSONArray("cards")
                    .getJSONObject(0)
                    .getJSONArray("content")
                    .getJSONObject(0)
                    .getJSONArray("content");
            for (int i = 0; i < contentArray.length() && result.size() < 10; i++) {
                JSONObject item = contentArray.getJSONObject(i);

                if (item.optBoolean("isTop", false)) {
                    continue;
                }

                int rankNo = item.optInt("index");
                String title = item.optString("word");
                String hotIndex = item.optString("hotTag");
                String itemUrl = item.optString("url");

                HotSearch hotSearch = new HotSearch(
                        rankNo,
                        title,
                        hotIndex,
                        itemUrl
                );
                result.add(hotSearch);
            }
        } catch (Exception e) {
            throw new RuntimeException("爬取百度热搜失败：" + e.getMessage(), e);
        }

        return result;
    }
}