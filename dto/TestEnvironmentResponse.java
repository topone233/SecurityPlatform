package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 测试环境响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestEnvironmentResponse {
    private Long id;
    private String envName;
    private String envType;
    private String description;
    private Map<String, Object> configData;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
