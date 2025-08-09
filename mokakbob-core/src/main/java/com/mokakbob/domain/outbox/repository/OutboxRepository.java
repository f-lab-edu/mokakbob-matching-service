package com.mokakbob.domain.outbox.repository;

import com.mokakbob.domain.outbox.domain.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {
}
