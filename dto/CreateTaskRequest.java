package org.example.securityplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 创建任务请求DTO
 */
@Data
public class CreateTaskRequest {
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    private String testVersion;

    private LocalDate testTime;

    private String createdBy;
}