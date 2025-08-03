package com.example.WordWise.dto.response;

import com.example.WordWise.entity.CrawledWord;
import com.example.WordWise.entity.TrendingNewFeed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TrendingNewFeedResponse {
    private TrendingNewFeed trendingNewFeed;
    private List<CrawledWord> crawledWordList;
}
