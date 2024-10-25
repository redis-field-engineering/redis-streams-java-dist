package com.redis.bskysentimentapi.controllers;

import com.redis.bskysentimentapi.Constants;
import com.redis.bskysentimentapi.repositories.PostRepository;
import com.redis.bskysentimentapi.documents.Post;
import com.redis.bskysentimentapi.services.SentimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.aggr.AggregationBuilder;
import redis.clients.jedis.search.aggr.AggregationResult;
import redis.clients.jedis.search.aggr.Group;
import redis.clients.jedis.search.aggr.Reducers;

import java.util.*;

@RestController
@CrossOrigin
public class PostController {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JedisPooled jedis;

    @Autowired
    private SentimentService sentimentService;

    @GetMapping("/posts")
    public Iterable<Post> getPostsByHashTag(@RequestParam String hashTag) {
        return postRepository.genericQuery(hashTag);
    }

    @GetMapping("/tags/popular")
    public Map<String,Map<String,Object>> getPopularPosts() {
        AggregationResult aggregationResult = postRepository.mostPopularHashTags();
        Map<String, Long> hashTagCountMap = new LinkedHashMap<>();
        aggregationResult.getResults().forEach(x-> {
                if(x.containsKey("hashTag") && x.containsKey("hashTagCount") && x.get("hashTag") != null && x.get("hashTagCount") != null) {
                    hashTagCountMap.put(x.get("hashTag").toString(), Long.parseLong(x.get("hashTagCount").toString()));
                }
            }
        );

        hashTagCountMap.remove("");
        String[] hashTags = hashTagCountMap.keySet().toArray(new String[0]);
        List<Boolean> membership = jedis.smismember(Constants.HASHTAGS_TO_QUERY, hashTags);
        Map<String,Map<String, Object>> result = new LinkedHashMap<>();
        for(int i = 0; i < hashTags.length; i++) {
            Map<String,Object> entry = new HashMap<>();
            entry.put("isTracked", membership.get(i));
            entry.put("count", hashTagCountMap.get(hashTags[i]));
            result.put(hashTags[i], entry);
        }

        return result;
    }

    @GetMapping("/tags")
    public Map<String,Long> getTags() {
        Set<String> trackedHashtags = sentimentService.getTrackedHashTags();
        Map<String,Long> result =sentimentService.getCountsForHashTags(trackedHashtags);
        Set<String> missingTrackedHashtags = new HashSet<>(trackedHashtags);
        missingTrackedHashtags.removeAll(result.keySet());
        missingTrackedHashtags.forEach(x->result.put(x,0L));
        return result;
    }

    @GetMapping("/tags/sentiment")
    public double getTagSentiment(@RequestParam String tag) {
        AggregationBuilder builder = new AggregationBuilder(String.format("@hashTag:{%s}", tag));
        return aggregateSentiment(builder);
    }

    @PostMapping("/tags")
    public void addTag(@RequestParam String tag) {
        if(tag == null || tag.isEmpty()) {
            return;
        }
        sentimentService.registerHashTag(tag);
    }

    @DeleteMapping("/tags")
    public void removeTag(@RequestParam String tag) {
        sentimentService.deregisterHashtag(tag);
    }

    @GetMapping("/keywords/sentiment")
    public double getKeywordsSentiment(@RequestParam String keywords) {
        AggregationBuilder builder = new AggregationBuilder(keywords);
        return aggregateSentiment(builder);
    }

    @PostMapping("/keywords")
    public void addKeyword(@RequestParam String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return;
        }
        sentimentService.registerKeyword(keyword);
    }

    @GetMapping("/keywords")
    public Set<String> trackedKeywords(){
        return sentimentService.getKeyWords();
    }
    @DeleteMapping("/keywords")
    public void removeKeyword(@RequestParam String keyword) {
        sentimentService.deregisterKeyword(keyword);
    }

    private double aggregateSentiment(AggregationBuilder builder) {
        Group group = new Group();
        group.reduce(Reducers.avg("@sentiment").as("sentiment"));
        builder.groupBy(group);
        AggregationResult result = jedis.ftAggregate("com.redis.bskysentimentapi.documents.PostIdx", builder);
        if (result.getResults().isEmpty()) {
            return -1;
        }

        return Double.parseDouble(result.getResults().get(0).get("sentiment").toString());
    }
}
