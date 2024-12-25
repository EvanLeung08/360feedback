package com.evan.feedback.repository;

import com.evan.feedback.model.Dimension;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DimensionRepository extends JpaRepository<Dimension, Long> {
}