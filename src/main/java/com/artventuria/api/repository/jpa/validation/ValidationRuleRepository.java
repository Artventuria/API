package com.artventuria.api.repository.jpa.validation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.artventuria.api.domain.postgresql.ValidationRule;

@Repository
public interface ValidationRuleRepository extends JpaRepository<ValidationRule, Integer> {
    // Basic CRUD operations are provided by JpaRepository
}