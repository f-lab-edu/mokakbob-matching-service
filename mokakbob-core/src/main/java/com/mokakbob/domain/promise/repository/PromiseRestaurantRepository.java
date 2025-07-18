package com.mokakbob.domain.promise.repository;

import com.mokakbob.domain.promise.domain.PromiseRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromiseRestaurantRepository extends JpaRepository<PromiseRestaurant, Long> {
}
