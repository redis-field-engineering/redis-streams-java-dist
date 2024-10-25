package com.redis.bskysentimentapi;

public interface Constants {
    String INDEX_NAME="com.redis.bskysentimentapi.documents.PostIdx";
    String HASHTAGS_TO_QUERY = "hashtagsToQuery";
    String KEYWORDS_TO_QUERY = "keywordsToQuery";
    String TOPIC_NAME = "firehose-posts";
    String CONSUMER_GROUP_NAME = "firehose-consumer-group";
}
