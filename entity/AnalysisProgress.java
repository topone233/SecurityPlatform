package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分析进度实体
 */
@Data
@Entity
@Table(name = "analysis_progress")
public class AnalysisProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artifact_id", nullable = false)
    private Long artifactId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "current_step", length = 100)
    private String currentStep;

    @Column(name = "total_steps")
    private Integer totalSteps;

    @Column(name = "completed_steps")
    private Integer completedSteps;

    @Column(name = "progress_percentage")
    private Integer progressPercentage;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
        if (totalSteps == null) {
            totalSteps = 5;
        }
        if (completedSteps == null) {
            completedSteps = 0;
        }
        if (progressPercentage == null) {
            progressPercentage = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}