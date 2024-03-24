package com.example.urlshortner.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class AppConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private Integer redisPort;



    @Bean
    public RedisClient redisClient(){
        return RedisClient.create(new RedisURI(redisHost, redisPort, Duration.ofSeconds(10L)));
    }

    @Bean
    @Autowired
    public Bucket distributedBucket(RedisClient redisClient){
        LettuceBasedProxyManager proxyManager = LettuceBasedProxyManager.builderFor(redisClient)
                .withExpirationStrategy(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofSeconds(10)))
                .build();

        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
        BucketConfiguration configuration = BucketConfiguration.builder()
                .addLimit(limit)
                .build();
        return proxyManager.builder().build("bucket".getBytes(), configuration);
    }

}
