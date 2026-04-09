package com.mokakbob.config;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    private static final int DEFAULT_REDIS_PORT = 6379;
    private static final int REDIS_TIMEOUT_SECONDS = 10;

    @Bean
    public AbstractRedisClient redisClient(
            @Value("${spring.data.redis.cluster.nodes}") List<String> clusterNodes
    ) {
        if (clusterNodes.size() == 1) {
            return RedisClient.create(toRedisURI(clusterNodes.get(0)));
        }

        List<RedisURI> uris = clusterNodes.stream()
                .map(RedisConfig::toRedisURI)
                .collect(Collectors.toList());

        return RedisClusterClient.create(uris);
    }

    private static RedisURI toRedisURI(String node) {
        String[] parts = node.split(":");
        String host = parts[0];
        int port = (parts.length > 1)
                ? Integer.parseInt(parts[1])
                : DEFAULT_REDIS_PORT;

        return RedisURI.builder()
                .withHost(host)
                .withPort(port)
                .withTimeout(Duration.ofSeconds(REDIS_TIMEOUT_SECONDS))
                .build();
    }
}
