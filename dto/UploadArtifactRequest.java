package org.example.securityplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 上传制品请求DTO
 */
@Data
public class UploadArtifactRequest {
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotBlank(message = "制品名称不能为空")
    private String artifactName;

    @NotBlank(message = "制品类型不能为空")
    private String artifactType;
}