package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 制品响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtifactResponse {
    private Long id;
    private Long taskId;
    private String artifactName;
    private String artifactType;
    private String filePath;
    private Long fileSize;
    private String md5;
    private LocalDateTime uploadTime;
    private String status;
}