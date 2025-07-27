package com.example.WordWise.model.crawlcomponent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrawlComponent {
    private String query;
    private String elementType;
    private Boolean isGetText;
    private List<String> attributes;
    private List<CrawlComponent> childComponents;
}