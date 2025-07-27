package com.example.WordWise.service;

import com.example.WordWise.entity.CrawledWord;
import com.example.WordWise.entity.TrendingNewFeed;
import com.example.WordWise.enums.PromptEnum;
import com.example.WordWise.model.crawlcomponent.CrawlerFactory;
import com.example.WordWise.model.crawlcomponent.CrawlerStrategy;
import com.example.WordWise.model.crawlcomponent.VTVCrawler;
import com.example.WordWise.repository.CrawledWordRepository;
import com.example.WordWise.repository.TrendingNewFeedRepository;
import com.example.WordWise.utils.APIUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CrawlerService {
    @Autowired
    private CrawlerFactory crawlerFactory;

    @Autowired
    private APIUtils apiUtils;

    @Value("${external.gemini-api-json}")
    private String geminiAPIJsonPath;

    @Value("${external.gemini-url}")
    private String geminiURL;

    @Autowired
    private TrendingNewFeedRepository trendingNewFeedRepository;

    @Autowired
    private CrawledWordRepository crawledWordRepository;

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional(rollbackOn = Exception.class)
    public void crawl() {
        CrawlerStrategy crawlerStrategy = this.crawlerFactory.createCrawlerInstance(VTVCrawler.class);
        StringBuilder prompt = new StringBuilder(PromptEnum.GEN_NEW_FEED.getValue());
        List<String> subjects = new ArrayList<>();
        try {
            List<List<String>> list = crawlerStrategy.crawl();
            prompt.append("\n 1.").append(list.get(0).get(2)).append("\n");
            prompt.append("\n 2.").append(list.get(1).get(2)).append("\n");
            prompt.append("\n 3.").append(list.get(2).get(2)).append("\n");

            subjects.addAll(Arrays.asList(list.get(0).get(2), list.get(1).get(2), list.get(2).get(2)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String escapedPrompt = this.apiUtils.escape(prompt.toString());
        String jsonContent = "";
        try {
            jsonContent = this.apiUtils.loadJsonConfig(geminiAPIJsonPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        jsonContent = this.apiUtils.replaceValue(Arrays.asList("${replaced}"), Arrays.asList(escapedPrompt), jsonContent);

        ResponseEntity<String> response = this.apiUtils.callApi(
                new HashMap<>(), jsonContent, geminiURL, HttpMethod.POST
        );

        String bodyResponse = response.getBody();
        if (bodyResponse == null) {
            throw new RuntimeException("API response body is null");
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(bodyResponse);

            String text = root
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            text = text.replace("*", "");

            Map<String, String> vocabularyMap = new LinkedHashMap<>();

            String[] lines = text.split("\\r?\\n");
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;

                int colonIndex = line.indexOf(":");
                if (colonIndex == -1) continue;

                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();

                vocabularyMap.put(key, value);
            }

            // create subject
            List<TrendingNewFeed> trendingNewFeeds = new ArrayList<>();
            subjects.stream().forEach(subject -> {
                TrendingNewFeed trendingNewFeed = TrendingNewFeed.builder()
                        .link("VTV")
                        .title(subject)
                        .createdDate(LocalDateTime.now())
                        .build();

                trendingNewFeed = this.trendingNewFeedRepository.save(trendingNewFeed);
                trendingNewFeeds.add(trendingNewFeed);
            });

            // create words
            for (int i = 0; i < trendingNewFeeds.size(); i++) {
                String topic = "topic" + (i + 1); // topic1, topic2, topic3...
                TrendingNewFeed feed = trendingNewFeeds.get(i);

                for (int w = 1; w <= 2; w++) { // word1, word2
                    String prefix = topic + "-word" + w;

                    CrawledWord crawledWord = CrawledWord.builder()
                            .english(vocabularyMap.getOrDefault(prefix + "-eng", ""))
                            .vietnamese(vocabularyMap.getOrDefault(prefix + "-vn", ""))
                            .engUsage(vocabularyMap.getOrDefault(topic + "-usage" + w + "-eng", ""))
                            .vnUsage(vocabularyMap.getOrDefault(topic + "-usage" + w + "-vn", ""))
                            .engSentence(vocabularyMap.getOrDefault(topic + "-sentence" + w + "-eng", ""))
                            .vnSentence(vocabularyMap.getOrDefault(topic + "-sentence" + w + "-vn", ""))
                            .link("VTV") // assuming TrendingNewFeed has getLink()
                            .trendingNewFeedId(feed.getId())
                            .createdDate(LocalDateTime.now())
                            .modifiedDate(LocalDateTime.now())
                            .build();

                    this.crawledWordRepository.save(crawledWord);
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse API response JSON", e);
        }
    }


}
