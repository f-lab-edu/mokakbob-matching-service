package com.mokakbob.common.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mokakbob.common.config.TestJpaConfig;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestJpaConfig.class)
@SuppressWarnings("NonAsciiCharacters")
class BaseEntityTest {

    @Autowired
    private TestEntityManager em;

    @Test
    void 생성_및_수정_시간이_자동으로_생성된다() {
        // given
        TestEntity testEntity = new TestEntity("테스트");
        em.persistAndFlush(testEntity);
        LocalDateTime expectedTime = LocalDateTime.ofInstant(
                java.time.Instant.parse("2025-01-01T00:00:00Z"),
                ZoneId.of("Asia/Seoul")
        );

        // then
        assertThat(testEntity.getCreatedAt()).isEqualTo(expectedTime);
        assertThat(testEntity.getUpdatedAt()).isEqualTo(expectedTime);
    }

    @Test
    void 수정_시간이_올바르게_업데이트된다() {
        // given
        TestEntity testEntity = new TestEntity("테스트");
        em.persistAndFlush(testEntity);

        // when
        TestJpaConfig.advanceBySeconds(3600);
        testEntity.name = "수정값";
        em.persistAndFlush(testEntity);
        em.clear();
        TestEntity found = em.find(TestEntity.class,testEntity.id);


        // then
        assertThat(found.getUpdatedAt()).isAfter(found.getCreatedAt());
    }



    @Entity
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    static class TestEntity extends BaseEntity {

        @Id
        @GeneratedValue
        private Long id;

        private String name;

        public TestEntity(String name) {
            this.name = name;
        }

        public LocalDateTime getCreatedAt() {
            return super.createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return super.updatedAt;
        }
    }
}
