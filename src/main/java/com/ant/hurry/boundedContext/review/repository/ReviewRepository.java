package com.ant.hurry.boundedContext.review.repository;

import com.ant.hurry.boundedContext.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
