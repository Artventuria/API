package com.artventuria.api.repository.jpa.nfc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.artventuria.api.domain.postgresql.NFCValidationRule;

@Repository
public interface NFCValidationRuleRepository extends JpaRepository<NFCValidationRule, Integer> {
}