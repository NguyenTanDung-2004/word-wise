package com.example.WordWise.model.crawlcomponent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrawlSiteConfig {
    private String site;
    private String url;
    private CrawlComponent component;
}
