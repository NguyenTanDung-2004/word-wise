package com.example.WordWise.utils;
import com.example.WordWise.model.CrawlComponentConfig.CrawlComponent;
import com.example.WordWise.model.CrawlComponentConfig.CrawlSiteConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CrawlUtils {
    public static void main(String[] args) {
        CrawlUtils crawlUtils =  new CrawlUtils();
        crawlUtils.crawlWeb();
    }

    public void crawlWeb() {
        // Load config
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("crawl-config/JsonConfig.json");

        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: crawl-config/JsonConfig.json");
        }

        List<CrawlSiteConfig> configs;

        try {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("🔍 JSON Config Content:\n" + content);
            ObjectMapper mapper = new ObjectMapper();
            configs = mapper.readValue(content, new TypeReference<List<CrawlSiteConfig>>() {});

        } catch (Exception e) {
            throw new RuntimeException("Cannot parse config", e);
        }

        for (CrawlSiteConfig config : configs) {
            try {
                System.out.println("📡 Crawling site: " + config.getSite());
                Document doc = Jsoup.connect(config.getUrl()).get();

                CrawlComponent mainComponent = config.getComponent();

                Elements elements = doc.select(mainComponent.getQuery());

                for (Element el : elements) {
                    System.out.println("🧱 Block found:");

                    for (CrawlComponent child : mainComponent.getChildComponents()) {
                        CrawlComponent c = child;
                        Elements targets = el.select(c.getQuery());

                        if (targets.isEmpty()) continue;

                        Element target = targets.first();

                        if (Boolean.TRUE.equals(c.getIsGetText())) {
                            System.out.println("  📄 Text: " + target.text());
                        }

                        if (c.getAttributes() != null) {
                            for (String attr : c.getAttributes()) {
                                System.out.println("  🔗 " + attr + ": " + target.attr(attr));
                            }
                        }
                    }

                    System.out.println("---------------------------");
                }

            } catch (Exception e) {
                System.err.println("❌ Failed to crawl " + config.getSite() + ": " + e.getMessage());
            }
        }
    }
}
