package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 外部URL实体
 */
@Data
@Entity
@Table(name = "external_url")
public class ExternalUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artifact_id", nullable = false)
    private Long artifactId;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "protocol", nullable = false, length = 20)
    private String protocol;

    @Column(name = "usage_context", length = 255)
    private String usageContext;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}