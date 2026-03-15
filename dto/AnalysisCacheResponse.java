package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 分析缓存响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisCacheResponse {
    private Long id;
    private String artifactMd5;
    private Integer apiCount;
    private Integer componentCount;
    private Integer dangerCount;
    private Integer configCount;
    private Integer urlCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Map<String, Integer> stats;
}