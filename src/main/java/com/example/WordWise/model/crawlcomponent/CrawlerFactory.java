package com.example.WordWise.model.crawlcomponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CrawlerFactory {
    @Autowired
    private VTVCrawler vtvCrawler;

    public CrawlerStrategy createCrawlerInstance(Class<? extends CrawlerStrategy> crawlerClass) {
        if (crawlerClass == VTVCrawler.class) {
            return vtvCrawler;
        }
        return null;
    }
}
