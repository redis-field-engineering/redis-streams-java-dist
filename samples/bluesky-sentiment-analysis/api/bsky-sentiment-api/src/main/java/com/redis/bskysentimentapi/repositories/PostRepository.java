package com.redis.bskysentimentapi.repositories;

import com.redis.bskysentimentapi.documents.Post;
import com.redis.om.spring.annotations.*;
import com.redis.om.spring.repository.RedisDocumentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import redis.clients.jedis.search.aggr.AggregationResult;
import redis.clients.jedis.search.aggr.SortedField;

import java.util.Optional;
import java.util.Set;

public interface PostRepository extends RedisDocumentRepository<Post, String> {
    Optional<Post> findByCid(String cid);
    Iterable<Post> findByHashTagAndCreatedAtBetween(String hashTag, long from, long to);
    Iterable<Post> findByHashTag(String hashTag);

    @Aggregation(
            groupBy = @GroupBy(
                    properties = {"@hashTag"},
                    reduce = {@Reducer(func = ReducerFunction.COUNT, alias = "hashTagCount")}
            ),
            sortBy = @SortBy(field = "@hashTagCount", direction = Sort.Direction.DESC),
            limit = 21
    )
    AggregationResult mostPopularHashTags();

    @Aggregation(groupBy = @GroupBy(
            reduce = {@Reducer(func= ReducerFunction.TOLIST, args = {"@hashTag"}, alias = "tags")}
    ))
    AggregationResult findTags();

    @Query("@hashTag:{$tag}")
    Iterable<Post> genericQuery(@Param("tag") String tag);

    @Aggregation(
            value = "@hashTag:{$tag}",
            groupBy = @GroupBy(
                    reduce = {@Reducer(func= ReducerFunction.AVG, args = {"@sentiment"}, alias = "sentiment")}
            ))
    AggregationResult getSentimentForHashtag(@Param("tag") String tag);

    @Aggregation(
            value = "@text:{$keyWords}",
            groupBy = @GroupBy(
                    reduce = {@Reducer(func= ReducerFunction.AVG, args = {"@sentiment"}, alias = "sentiment")}
            ))
    AggregationResult getSentimentForKeyWords(String keyWords);

}
