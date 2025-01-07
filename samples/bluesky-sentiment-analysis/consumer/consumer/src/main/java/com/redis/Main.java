package com.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.streams.AckMessage;
import com.redis.streams.TopicEntry;
import com.redis.streams.command.serial.ConsumerGroup;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Protocol;

import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Main main = new Main();
        main.run();

    }

    public static boolean containsNonAscii(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) >= 128) {
                return true;
            }
        }
        return false;
    }

    public void run() throws IOException, InterruptedException {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,parse,sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        String redisHost = System.getenv("REDIS_HOST") != null ? System.getenv("REDIS_HOST") : "localhost";
        System.out.println("Connecting to Redis at " + redisHost);
        String redisPassword = System.getenv("REDIS_PASSWORD") != null ? System.getenv("REDIS_PASSWORD") : null;
        JedisPooled jedis = redisPassword != null ? new JedisPooled(redisHost,6379, "default", redisPassword) : new JedisPooled(redisHost, 6379);

        byte[] infoBytes = (byte[])jedis.sendCommand(Protocol.Command.INFO);
        String info = new String(infoBytes);
        while(info.contains("loading:1")){
            Thread.sleep(1000);
            info = new String((byte[])jedis.sendCommand(Protocol.Command.INFO));
        }


        ConsumerGroup consumerGroup = new ConsumerGroup(jedis, "firehose-posts", "firehose-consumer-group");
        ObjectMapper objectMapper = new ObjectMapper();


        UUID consumerId = UUID.randomUUID();
        String consumerName = "firehose-consumer-" + consumerId.toString();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TopicEntry entry = consumerGroup.consume(consumerName);
                if(entry == null){
                    Thread.sleep(10);
                    continue;
                }

                Post post = Post.fromMap(entry.getMessage());
                if(!post.getLanguage().equals("en")){
                    consumerGroup.acknowledge(new AckMessage(entry));
                    continue;
                }

                Annotation annotation = new Annotation(post.getText());
                pipeline.annotate(annotation);

                List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
                int numSentences = 0;
                int sentimentSum = 0;
                for (CoreMap sentence : sentences) {
                    String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
                    sentimentSum += convertSentiment(sentiment);
                    numSentences++;
                }

                if(numSentences == 0){
                    consumerGroup.acknowledge(new AckMessage(entry));
                    continue;
                }

                double sentiment = ((double) sentimentSum / numSentences) / 4 ; // divide by 5 to normalize it.

                String key = "com.redis.bskysentimentapi.documents.Post:" + post.getCid();
                Set<String> hashTags = new HashSet<>(Arrays.asList(post.getHashTags()));
                if(hashTags.contains("nsfw"))
                {
                    consumerGroup.acknowledge(new AckMessage(entry));
                    continue;
                }

                if(hashTags.isEmpty()){
                    Map<String,Object> postMap = Map.of(
                            "cid", post.getCid(),
                            "createdAt", post.getCreatedAt(),
                            "sentiment", sentiment,
                            "text", post.getText()
                    );

                    String json = objectMapper.writeValueAsString(postMap);
                    jedis.jsonSet(key,json);
                    jedis.expire(key, 60*60*24);
                }
                else{
                    for(String hashTag : hashTags){
                        if (containsNonAscii(hashTag)) {
                            continue;
                        }

                        Map<String,Object> postMap = Map.of(
                                "cid", post.getCid(),
                                "createdAt", post.getCreatedAt(),
                                "sentiment", sentiment,
                                "text", post.getText(),
                                "hashTag", hashTag.toLowerCase()
                        );

                        String json = objectMapper.writeValueAsString(postMap);
                        jedis.jsonSet(key,json);
                        jedis.expire(key, 60*60*24);
                    }
                }

                consumerGroup.acknowledge(new AckMessage(entry));
                Thread.sleep(1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int convertSentiment(String sentiment){
        return switch (sentiment) {
            case "Very negative" -> 0;
            case "Negative" -> 1;
            case "Neutral" -> 2;
            case "Positive" -> 3;
            case "Very positive" -> 4;
            default -> 2;
        };
    }
}