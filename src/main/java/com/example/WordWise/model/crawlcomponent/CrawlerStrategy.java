package com.example.WordWise.model.crawlcomponent;

import com.example.WordWise.entity.CrawledWord;

import java.io.InputStream;
import java.util.List;

public interface CrawlerStrategy {
    public List<List<String>> crawl() throws Exception;
    public List<CrawlSiteConfig> loadJsonConfig(String path) throws Exception;
}
