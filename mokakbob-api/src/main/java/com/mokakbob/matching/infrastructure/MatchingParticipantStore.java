package com.mokakbob.matching.infrastructure;

import com.mokakbob.domain.matching.domain.vo.MatchingRequestStatus;
import com.mokakbob.matching.domain.ParticipantStore;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchingParticipantStore implements ParticipantStore {

    private static final String PARTICIPATE_KEY = "user:%d:matching:participate";
    private static final String FOUND_KEY = "user:%d:matching:found";
    private static final Duration DEFAULT_TTL = Duration.ofHours(15);
    private static final String SHOW_EXIST = "1";

    private final RedisTemplate<String, String> basicRedisTemplate;

    @Override
    public boolean isAlreadyParticipating(Long memberId) {
        return Boolean.TRUE.equals(basicRedisTemplate.hasKey(participateKey(memberId))) ||
                Boolean.TRUE.equals(basicRedisTemplate.hasKey(foundKey(memberId)));
    }

    @Override
    public void transitionToParticipating(Long memberId) {
        deleteAllStates(memberId);
        basicRedisTemplate.opsForValue()
                .set(participateKey(memberId), SHOW_EXIST, DEFAULT_TTL);
    }

    @Override
    public void transitionToFound(Long memberId) {
        basicRedisTemplate.delete(participateKey(memberId));
        basicRedisTemplate.opsForValue()
                .set(foundKey(memberId), SHOW_EXIST, DEFAULT_TTL);
    }

    @Override
    public void clearAll(Long memberId) {
        deleteAllStates(memberId);
    }

    @Override
    public MatchingRequestStatus getStatue(Long memberId) {
        if (Boolean.TRUE.equals(basicRedisTemplate.hasKey(foundKey(memberId)))) {
            return MatchingRequestStatus.FOUND;
        }

        if (Boolean.TRUE.equals(basicRedisTemplate.hasKey(participateKey(memberId)))) {
            return MatchingRequestStatus.PARTICIPATE;
        }

        return MatchingRequestStatus.NONE;
    }

    private void deleteAllStates(Long memberId) {
        basicRedisTemplate.delete(List.of(
                participateKey(memberId),
                foundKey(memberId)
        ));
    }

    private String participateKey(Long memberId) {
        return PARTICIPATE_KEY.formatted(memberId);
    }

    private String foundKey(Long memberId) {
        return FOUND_KEY.formatted(memberId);
    }

}
