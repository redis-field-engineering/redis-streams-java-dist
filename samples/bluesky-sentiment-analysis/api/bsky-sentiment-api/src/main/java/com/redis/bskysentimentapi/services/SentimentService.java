package com.redis.bskysentimentapi.services;

import com.redis.bskysentimentapi.Constants;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.aggr.AggregationBuilder;
import redis.clients.jedis.search.aggr.AggregationResult;
import redis.clients.jedis.search.aggr.Group;
import redis.clients.jedis.search.aggr.Reducers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class SentimentService {
    private final JedisPooled jedis;

    public SentimentService(JedisPooled jedis) {
        this.jedis = jedis;
    }


    public double getSentimentForHashTag(String tag) {
        AggregationBuilder builder = new AggregationBuilder(String.format("@hashTag:{%s}", tag));
        Group group = new Group();
        group.reduce(Reducers.avg("@sentiment"));
        builder.groupBy(group);
        AggregationResult result = jedis.ftAggregate(Constants.INDEX_NAME, builder);
        return Double.parseDouble(result.getResults().get(0).get("sentiment").toString());
    }

    public Optional<Double> getSentimentForKeyWord(String keyword) {
        long now = Instant.now().getEpochSecond() * 1000;
        AggregationBuilder builder = new AggregationBuilder(String.format("@text:%s @createdAt:[%d %d]", keyword, now - 10000, now));
        Group group = new Group();
        group.reduce(Reducers.avg("@sentiment").as("sentiment"));
        builder.groupBy(group);
        AggregationResult result = jedis.ftAggregate(Constants.INDEX_NAME, builder);
        if(result.getResults().isEmpty()){
            return Optional.empty();
        }
        return Optional.of(Double.parseDouble(result.getResults().get(0).get("sentiment").toString()));
    }

    public Map<String, Double> getSentimentForKeyWords(Set<String> keyWords){
        if(keyWords.isEmpty()){
            return new HashMap<>();
        }
        String queryString = String.join("|", keyWords);
        long now = Instant.now().getEpochSecond() * 1000;
        AggregationBuilder builder = new AggregationBuilder(String.format("@text:%s @createdAt:[%d %d]", queryString, now - 10000, now));
        Group group = new Group("@text");
        group.reduce(Reducers.avg("@sentiment").as("sentiment"));
        builder.groupBy(group);
        AggregationResult result = jedis.ftAggregate(Constants.INDEX_NAME, builder);

        Map<String, Double> sentiments = new HashMap<>();
        result.getResults().forEach(r -> {
            if(!r.containsKey("text") || !r.containsKey("sentiment") || r.get("text") == null || r.get("sentiment") == null){
                return;
            }
            String text = r.get("text").toString();
            double sentiment = Double.parseDouble(r.get("sentiment").toString());
            sentiments.put(text, sentiment);
        });
        return sentiments;
    }

    public Map<String, Double> getSentimentForHashTags(Set<String> tags){
        if(tags.isEmpty()){
            return new HashMap<>();
        }
        String queryString = String.join("|", tags);
        long now = Instant.now().getEpochSecond() * 1000;
        AggregationBuilder builder = new AggregationBuilder(String.format("@hashTag:{%s} @createdAt:[%d %d]", queryString, now - 10000, now));
        Group group = new Group("@hashTag");
        group.reduce(Reducers.avg("@sentiment").as("sentiment"));
        builder.groupBy(group);
        AggregationResult result = jedis.ftAggregate(Constants.INDEX_NAME, builder);

        Map<String, Double> sentiments = new HashMap<>();
        result.getResults().forEach(r -> {
            if(!r.containsKey("hashTag") || !r.containsKey("sentiment") || r.get("hashTag") == null || r.get("sentiment") == null){
                return;
            }
            String hashTag = r.get("hashTag").toString();
            double sentiment = Double.parseDouble(r.get("sentiment").toString());
            sentiments.put(hashTag, sentiment);
        });
        return sentiments;
    }

    public Map<String, Long> getCountsForHashTags(Set<String> tags){
        if(tags.isEmpty()){
            return new HashMap<>();
        }
        String queryString = String.join("|", tags);
        AggregationBuilder builder = new AggregationBuilder(String.format("@hashTag:{%s}", queryString));
        Group group = new Group("@hashTag");
        group.reduce(Reducers.count().as("count"));
        builder.groupBy(group);
        AggregationResult result = jedis.ftAggregate(Constants.INDEX_NAME, builder);

        Map<String, Long> counts = new HashMap<>();
        result.getResults().forEach(r -> {
            if(!r.containsKey("hashTag") || !r.containsKey("count") || r.get("hashTag") == null || r.get("count") == null){
                if(r.containsKey("hashTag")){
                    counts.put(r.get("hashTag").toString(), 0L);
                }
                return;
            }
            String hashTag = r.get("hashTag").toString();
            long count = Long.parseLong(r.get("count").toString());
            counts.put(hashTag, count);
        });
        return counts;
    }

    public void registerHashTag(String hashTag){
        jedis.sadd(Constants.HASHTAGS_TO_QUERY, hashTag);
    }

    public void deregisterHashtag(String hashTag){
        jedis.srem(Constants.HASHTAGS_TO_QUERY, hashTag);
    }

    public Set<String> getTrackedHashTags(){
        return jedis.smembers(Constants.HASHTAGS_TO_QUERY);
    }

    public void registerKeyword(String keyword){
        jedis.sadd(Constants.KEYWORDS_TO_QUERY, keyword);
    }

    public void deregisterKeyword(String keyword){
        jedis.srem(Constants.KEYWORDS_TO_QUERY, keyword);
    }

    public Set<String> getKeyWords(){
        return jedis.smembers(Constants.KEYWORDS_TO_QUERY);
    }
}
