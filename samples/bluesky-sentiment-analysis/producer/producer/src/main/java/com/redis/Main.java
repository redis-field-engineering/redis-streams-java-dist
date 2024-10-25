package com.redis;

import com.redis.streams.Producer;
import com.redis.streams.command.serial.TopicManager;
import com.redis.streams.command.serial.TopicProducer;
import com.redis.streams.exception.InvalidTopicException;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Protocol;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class Main {

    final static String TOPIC_NAME = "firehose-posts";

    public static void main(String[] args) throws InvalidTopicException, InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        String uri = "wss://bsky.network/xrpc/com.atproto.sync.subscribeRepos";

        String redisHost = System.getenv("REDIS_HOST") != null ? System.getenv("REDIS_HOST") : "localhost";
        String redisPassword = System.getenv("REDIS_PASSWORD") != null ? System.getenv("REDIS_PASSWORD") : null;
        JedisPooled jedis = redisPassword != null ? new JedisPooled(redisHost,6379, "default", redisPassword) : new JedisPooled(redisHost, 6379);

        byte[] infoBytes = (byte[])jedis.sendCommand(Protocol.Command.INFO);
        String info = new String(infoBytes);
        while(info.contains("loading:1")){
            Thread.sleep(1000);
            info = new String((byte[])jedis.sendCommand(Protocol.Command.INFO));
        }

        TopicManager.createTopic(jedis, TOPIC_NAME);
        Producer producer = new TopicProducer(jedis, TOPIC_NAME);


        try{
            final BskyClientEndpoint clientEndpoint = new BskyClientEndpoint(URI.create(uri), latch, producer);
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}