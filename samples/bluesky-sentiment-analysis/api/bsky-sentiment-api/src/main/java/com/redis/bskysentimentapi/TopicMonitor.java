package com.redis.bskysentimentapi;

import com.redis.bskysentimentapi.services.MetricsService;
import com.redis.streams.command.serial.TopicManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TopicMonitor {
    private final MetricsService metricsService;
    private final TopicManager topicManager;

    public TopicMonitor(MetricsService metricsService, TopicManager topicManager) {
        this.metricsService = metricsService;
        this.topicManager = topicManager;
    }

    @Scheduled(fixedRate = 5000)
    public void monitorTopic(){
        metricsService.updateConsumerStats(topicManager.getConsumerGroupStatus(Constants.CONSUMER_GROUP_NAME));
    }

}
