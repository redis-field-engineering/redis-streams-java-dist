package com.redis.bskysentimentapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPooled;

@Component
public class Bootstrap implements CommandLineRunner {
    private final JedisPooled jedis;

    public Bootstrap(JedisPooled jedis) {
        this.jedis = jedis;
    }

    @Override
    public void run(String... args) throws Exception {
        String[] hashtags = {
                "airevolution",
                "techforgood",
                "web3innovation",
                "digitalnomadlife",
                "mentalhealthmatters",
                "sustainableliving",
                "cryptotrends",
                "womenintech",
                "futureofwork",
                "spaceexploration",
                "dataprivacy",
                "electricvehicles",
                "remoteworkculture",
                "greenenergy",
                "music",
                "rock",
                "art",
                "photography",
                "fashion",
                "food",
                "travel",
                "sport",
                "technology",
                "science",
                "opensource"
        };

        jedis.sadd(Constants.HASHTAGS_TO_QUERY, hashtags);
        jedis.sadd(Constants.KEYWORDS_TO_QUERY, "us", "politics", "election", "apple", "tesla", "ai", "history", "pop", "rock", "jazz", "boeing");

    }
}
