package com.redis.bskysentimentapi.documents;


import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    private String cid;
    @Searchable
    private String text;
    @Indexed
    private long createdAt;
    @Indexed
    private double sentiment;
    @Indexed
    private String hashTag;
}
