package com.example.WordWise.model.CrawlComponentConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrawlSiteConfig {
    private String site;
    private String url;
    private CrawlComponent component;
}
