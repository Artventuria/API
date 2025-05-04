package com.artventuria.api.dto.nfc;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNFCValidationRuleRequest {
    private String nfcTagId;
    private String ruleType;
    private String ruleValue;
    private Boolean isActive;
}