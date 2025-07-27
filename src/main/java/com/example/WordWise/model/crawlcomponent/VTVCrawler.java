package com.example.WordWise.model.crawlcomponent;

import com.example.WordWise.entity.CrawledWord;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
public class VTVCrawler implements CrawlerStrategy{
    private String path = "crawl-config/JsonConfig.json";
    private String site = "VTV";

    @Override
    public List<List<String>> crawl() throws Exception { // TODO: NEED TO OPTIMIZE THIS ALGORITHM (USING RECURSIVE OR BFS DFS)
        List<CrawlSiteConfig> configs = loadJsonConfig(path);
        List<List<String>> results = new ArrayList<>();

        for (CrawlSiteConfig config : configs) {
            if (!config.getSite().equals(site)) {
                continue;
            }
            Document doc = Jsoup.connect(config.getUrl()).get();

            CrawlComponent mainComponent = config.getComponent();

            Elements elements = doc.select(mainComponent.getQuery());

            for (Element el : elements) {
                List<String> list = new ArrayList<>();
                for (CrawlComponent child : mainComponent.getChildComponents()) {
                    CrawlComponent c = child;
                    Elements targets = el.select(c.getQuery());

                    if (targets.isEmpty()) continue;
                    Element target = targets.first();

                    if (Boolean.TRUE.equals(c.getIsGetText())) {
                        list.add(target.text());
                    }

                    if (c.getAttributes() != null) {
                        for (String attr : c.getAttributes()) {
                            list.add(target.attr(attr));
                        }
                    }
                }
                results.add(list);
            }
        }

        return results;
    }

    @Override
    public List<CrawlSiteConfig> loadJsonConfig(String path) throws Exception{
        // Load config
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("crawl-config/JsonConfig.json");

        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: crawl-config/JsonConfig.json");
        }

        List<CrawlSiteConfig> configs;
        try {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            configs = mapper.readValue(content, new TypeReference<List<CrawlSiteConfig>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse config", e);
        }

        return configs;
    }
}
