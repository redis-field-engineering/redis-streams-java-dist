package com.redis.bskysentimentapi;

import com.redis.bskysentimentapi.services.MetricsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SentimentGatherer {

    private final MetricsService metricsService;

    public SentimentGatherer(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Scheduled(fixedRate = 5000)
    public void gatherSentiments(){
        metricsService.queryHashTags();
    }

    @Scheduled(fixedRate = 5000)
    public void gatherSentimentsForKeyWords(){
        metricsService.queryKeyWords();
    }
}
