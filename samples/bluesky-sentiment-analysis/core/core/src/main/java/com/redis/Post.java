package com.redis;

import lombok.*;

import java.util.Map;

@Data
public class Post {
    private final String text;
    private final String did;
    private final String language;
    private final String[] hashTags;
    private final long createdAt;
    private final String cid;

    public Post(String text, String did, String language, String[] hashTags, long createdAt, String cid) {
        this.text = text;
        this.did = did;
        this.language = language;
        this.hashTags = hashTags;
        this.createdAt = createdAt;
        this.cid = cid;
    }

    public Map<String,String> toMap(){
        return Map.of(
                "text", text,
                "did", did,
                "language", language,
                "hashTags", String.join(",", hashTags),
                "createdAt", String.valueOf(createdAt),
                "cid", cid
        );
    }

    public static Post fromMap(Map<String,String> map){
        return new Post(
                map.get("text"),
                map.get("did"),
                map.get("language"),
                map.get("hashTags").split(","),
                Long.parseLong(map.get("createdAt")),
                map.get("cid")
        );
    }
}
