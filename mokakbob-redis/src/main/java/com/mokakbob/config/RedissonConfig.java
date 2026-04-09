package com.mokakbob.config;

import java.util.List;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(
            @Value("${spring.data.redis.cluster.nodes}") List<String> clusterNodes) {

        Config config = new Config();

        if (clusterNodes.size() == 1) {
            config.useSingleServer()
                    .setAddress(RedisConstants.REDIS_PROTOCOL_PREFIX + clusterNodes.get(0))
                    .setIdleConnectionTimeout(10000)
                    .setConnectTimeout(10000)
                    .setTimeout(3000)
                    .setRetryAttempts(3)
                    .setRetryInterval(1500);
        } else {
            var clusterConfig = config.useClusterServers()
                    .setScanInterval(2000)
                    .setIdleConnectionTimeout(10000)
                    .setConnectTimeout(10000)
                    .setTimeout(3000)
                    .setRetryAttempts(3)
                    .setRetryInterval(1500);

            for (String node : clusterNodes) {
                clusterConfig.addNodeAddress(RedisConstants.REDIS_PROTOCOL_PREFIX + node);
            }
        }

        return Redisson.create(config);
    }
}
