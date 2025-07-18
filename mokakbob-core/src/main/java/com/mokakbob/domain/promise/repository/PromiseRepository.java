package com.mokakbob.domain.promise.repository;

import com.mokakbob.domain.promise.domain.Promise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromiseRepository extends JpaRepository<Promise, Long> {
}
