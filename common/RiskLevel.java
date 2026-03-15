package org.example.securityplatform.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 风险等级枚举
 */
@Getter
@AllArgsConstructor
public enum RiskLevel {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low");

    private final String value;
}