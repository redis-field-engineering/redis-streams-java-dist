package com.redis.bskysentimentapi.services;

import com.google.common.util.concurrent.AtomicDouble;
import com.redis.streams.ConsumerGroupStatus;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {
    private final MeterRegistry registry;
    private final SentimentService sentimentService;
    private final ConcurrentHashMap<String, AtomicDouble> gauges = new ConcurrentHashMap<>();
    private final AtomicLong pending = new AtomicLong(0);
    private final AtomicLong topicLength = new AtomicLong(0);
    private final AtomicLong consumerLag = new AtomicLong(0);


    public MetricsService(MeterRegistry registry, SentimentService sentimentService) {
        this.registry = registry;
        this.sentimentService = sentimentService;
        initialize();
    }

    private void initialize(){
        Gauge.builder("topic_stats_pending", pending, AtomicLong::get)
                .description("Pending messages in topic.")
                .register(registry);

        Gauge.builder("topic_stats_length", topicLength, AtomicLong::get)
                .description("Length of topic.")
                .register(registry);

        Gauge.builder("topic_stats_lag", consumerLag, AtomicLong::get)
                .description("Consumer lag.")
                .register(registry);
    }

    public void queryKeyWords (){
        Set<String> keywordsToQuery = sentimentService.getKeyWords();
//        Map<String, Double> sentiments = sentimentService.getSentimentForKeyWords(keywordsToQuery);

        for(String keyword : keywordsToQuery){
            Optional<Double> sentiment = sentimentService.getSentimentForKeyWord(keyword);
            if(sentiment.isEmpty()){
                continue;
            }


            if(!gauges.containsKey(keyword)){
                AtomicDouble gauge = new AtomicDouble();
                Gauge.builder(String.format("%s-sentiment",keyword), gauge, AtomicDouble::get)
                        .tag("keyword", keyword)
                        .description("Sentiment for " + keyword)
                        .register(registry);
                gauges.put(keyword, gauge);
            }

            gauges.get(keyword).set(sentiment.get());

        }
    }

    public void queryHashTags (){
        Set<String> hashtagsToQuery = sentimentService.getTrackedHashTags();
        Map<String, Double> sentiments = sentimentService.getSentimentForHashTags(hashtagsToQuery);
        if(sentiments == null){
            return;
        }

        for(Map.Entry<String, Double> entry : sentiments.entrySet()){
            if(!gauges.containsKey(entry.getKey())){
                AtomicDouble gauge = new AtomicDouble();
                Gauge.builder(String.format("%s-sentiment",entry.getKey()), gauge, AtomicDouble::get)
                        .tag("hashTag", entry.getKey())
                        .description("Sentiment for " + entry.getKey())
                        .register(registry);
                gauges.put(entry.getKey(), gauge);
            }

            gauges.get(entry.getKey()).set(entry.getValue());
        }
    }

    public void updateConsumerStats(ConsumerGroupStatus stats){
        pending.set(stats.getPendingEntryCount());
        topicLength.set(stats.getTopicEntryCount());
        consumerLag.set(stats.getConsumerLag());
    }
}
