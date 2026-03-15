package org.example.securityplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建项目请求DTO
 */
@Data
public class CreateProjectRequest {
    @NotBlank(message = "项目名称不能为空")
    private String projectName;

    private String productName;

    private String description;

    private String createdBy;
}