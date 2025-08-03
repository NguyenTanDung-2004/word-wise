package com.example.WordWise.service;

import com.example.WordWise.dto.response.TrendingNewFeedResponse;
import com.example.WordWise.entity.CrawledWord;
import com.example.WordWise.entity.TrendingNewFeed;
import com.example.WordWise.repository.CrawledWordRepository;
import com.example.WordWise.repository.TrendingNewFeedRepository;
import com.example.WordWise.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrendingNewFeedService {
    @Autowired
    private TrendingNewFeedRepository trendingNewFeedRepository;

    @Autowired
    private CrawledWordRepository crawledWordRepository;

    public List<TrendingNewFeedResponse> getTrendingNewFeedResponseList() {
        // get list new feed on that date
        List<TrendingNewFeed> trendingNewFeedList = this.trendingNewFeedRepository.getNewFeed(LocalDate.now());

        List<String> ids = (List<String>) Utils.getFieldsFromList(trendingNewFeedList, Arrays.asList("id"), String.class);
        ids = ids.stream()
                .map(id -> id.replaceAll("id=", "").replaceAll("\\{", "").replaceAll("}", ""))
                .collect(Collectors.toList());

        List<CrawledWord> crawledWordList = this.crawledWordRepository.getWordInNewFeed(ids);

        List<TrendingNewFeedResponse> response = new ArrayList<>();

        trendingNewFeedList.stream().forEach(trendingNewFeed -> {
            TrendingNewFeedResponse trendingNewFeedResponse = new TrendingNewFeedResponse();
            trendingNewFeedResponse.setTrendingNewFeed(trendingNewFeed);

            List<CrawledWord> crawledWordList1 = new ArrayList<>();
            crawledWordList.stream().forEach(crawledWord -> {
                if (crawledWord.getTrendingNewFeedId().equals(trendingNewFeed.getId())) {
                    crawledWordList1.add(crawledWord);
                }
            });

            trendingNewFeedResponse.setCrawledWordList(crawledWordList);
            response.add(trendingNewFeedResponse);
        });

        return response;
    }

}
