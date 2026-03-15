package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分析结果缓存实体
 */
@Data
@Entity
@Table(name = "analysis_cache")
public class AnalysisCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artifact_md5", nullable = false, length = 64, unique = true)
    private String artifactMd5;

    @Column(name = "api_count")
    private Integer apiCount;

    @Column(name = "component_count")
    private Integer componentCount;

    @Column(name = "danger_count")
    private Integer dangerCount;

    @Column(name = "config_count")
    private Integer configCount;

    @Column(name = "url_count")
    private Integer urlCount;

    @Column(name = "cache_data", columnDefinition = "JSONB")
    private String cacheData;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // 默认 7 天过期
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusDays(7);
        }
    }
}