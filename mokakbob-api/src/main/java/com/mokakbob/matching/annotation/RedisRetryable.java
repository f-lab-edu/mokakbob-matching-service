package com.mokakbob.matching.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Retryable(
        retryFor = RedisConnectionFailureException.class,
        maxAttempts = 5,
        backoff = @Backoff(delay = 2000)
)
public @interface RedisRetryable {
}
