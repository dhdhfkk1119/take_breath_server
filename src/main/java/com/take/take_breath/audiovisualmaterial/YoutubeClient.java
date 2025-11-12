package com.take.take_breath.audiovisualmaterial;

import com.take.take_breath.audiovisualmaterial.dto.YoutubeVideoInfo;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class YoutubeClient {

    private final WebClient webClient = WebClient.builder().build();

    @Value("${youtube.api-key}")
    private String apiKey;

    @Value("${youtube.base-url}")
    private String baseUrl;

    public String extractVideoId(String url) {
        if (url == null) return null;
        String[] regexes = {
                "(?:v=)([A-Za-z0-9_-]{11})",
                "youtu\\.be/([A-Za-z0-9_-]{11})",
                "embed/([A-Za-z0-9_-]{11})",
                "shorts/([A-Za-z0-9_-]{11})"
        };
        for (String r : regexes) {
            Matcher m = Pattern.compile(r).matcher(url);
            if (m.find()) return m.group(1);
        }
        return null;
    }

    public YoutubeVideoInfo fetchVideoInfo(String youtubeUrl) {
        String videoId = extractVideoId(youtubeUrl);
        if (videoId == null) return null;

        String uri = baseUrl + "/videos?part=snippet,contentDetails&id=" + videoId + "&key=" + apiKey;

        String response = webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody ->
                                        Mono.<Throwable>error(new RuntimeException("YouTube API error: " + errorBody))
                                )
                )
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> {
                    System.err.println("⚠️ YouTube API 요청 실패: " + e.getMessage());
                    return Mono.empty();
                })
                .block();

        if (response == null) return null;
        return parseVideoInfo(response);
    }

    private YoutubeVideoInfo parseVideoInfo(String json) {
        JSONObject root = new JSONObject(json);
        if (root.getJSONArray("items").isEmpty()) return null;

        JSONObject item = root.getJSONArray("items").getJSONObject(0);
        JSONObject snippet = item.getJSONObject("snippet");
        JSONObject details = item.getJSONObject("contentDetails");

        String title = snippet.optString("title", "제목 없음");
        String description = snippet.optString("description", "");
        String isoDuration = details.optString("duration");

        String thumbnailUrl = "";
        if (snippet.has("thumbnails")) {
            JSONObject thumbs = snippet.getJSONObject("thumbnails");
            if (thumbs.has("maxres")) {
                thumbnailUrl = thumbs.getJSONObject("maxres").optString("url", "");
            } else if (thumbs.has("high")) {
                thumbnailUrl = thumbs.getJSONObject("high").optString("url", "");
            } else if (thumbs.has("default")) {
                thumbnailUrl = thumbs.getJSONObject("default").optString("url", "");
            }
        }

        return new YoutubeVideoInfo(title, description, formatDuration(isoDuration), thumbnailUrl);
    }

    private String formatDuration(String iso) {
        try {
            java.time.Duration d = java.time.Duration.parse(iso);
            long total = d.getSeconds();
            long h = total / 3600;
            long m = (total % 3600) / 60;
            long s = total % 60;
            if (h > 0) return String.format("%d:%02d:%02d", h, m, s);
            return String.format("%d:%02d", m, s);
        } catch (Exception e) {
            return null;
        }
    }
}