package com.mokakbob.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.data.redis.sentinel")
@Getter
@Setter
public class RedisSentinelProps {
    private String master;
    private List<String> nodes;
}
