package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 制品实体
 */
@Data
@Entity
@Table(name = "artifact")
public class Artifact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "artifact_name", nullable = false, length = 255)
    private String artifactName;

    @Column(name = "artifact_type", nullable = false, length = 50)
    private String artifactType;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "md5", length = 64)
    private String md5;

    @Column(name = "upload_time", updatable = false)
    private LocalDateTime uploadTime;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @PrePersist
    protected void onCreate() {
        uploadTime = LocalDateTime.now();
        if (status == null) {
            status = "UPLOADED";
        }
    }
}