package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 配置检查项响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigCheckItemResponse {
    private Long id;
    private String itemName;
    private String category;
    private String configKeyPattern;
    private String checkRule;
    private String expectedValue;
    private String unsafeValues;
    private String riskLevel;
    private String description;
    private String remediation;
    private String standardReference;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}