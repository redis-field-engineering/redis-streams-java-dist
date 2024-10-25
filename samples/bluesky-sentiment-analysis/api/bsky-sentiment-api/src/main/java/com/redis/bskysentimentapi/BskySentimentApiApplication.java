package com.redis.bskysentimentapi;

import com.redis.bskysentimentapi.repositories.PostRepository;
import com.redis.bskysentimentapi.services.MetricsService;
import com.redis.bskysentimentapi.services.SentimentService;
import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import com.redis.streams.command.serial.TopicManager;
import com.redis.streams.exception.TopicNotFoundException;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Protocol;

import java.util.Objects;
import java.util.Set;

@SpringBootApplication
@Configuration
@EnableRedisDocumentRepositories
@EnableScheduling
public class BskySentimentApiApplication {

    @Autowired
    PostRepository postRepository;

    @Bean
    public JedisPooled jedisPooled(JedisConnectionFactory jedisConnectionFactory) throws InterruptedException {
        var cc = jedisConnectionFactory.getClientConfiguration();
        var hostAndPort = new HostAndPort(jedisConnectionFactory.getHostName(), jedisConnectionFactory.getPort());
        var standaloneConfig = jedisConnectionFactory.getStandaloneConfiguration();
        var username = standaloneConfig != null ? standaloneConfig.getUsername() : null;
        var password = standaloneConfig != null ? standaloneConfig.getPassword() : null;

        JedisPooled jedis = new JedisPooled(hostAndPort);
        byte[] infoBytes = (byte[])jedis.sendCommand(Protocol.Command.INFO);
        String info = new String(infoBytes);
        while(info.contains("loading:1")){
            Thread.sleep(1000);
            info = new String((byte[])jedis.sendCommand(Protocol.Command.INFO));
        }

        return jedis;
    }

    public static void main(String[] args) {
        SpringApplication.run(BskySentimentApiApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**").allowedOrigins("http://localhost:3001");
            }
        };
    }

    @Bean
    public TopicManager topicManager(JedisPooled jedis) throws TopicNotFoundException {
        return TopicManager.load(jedis, Constants.TOPIC_NAME);
    }
}
