package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 测试执行结果DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestExecutionResultRequest {
    private String testResult;
    private String severity;
    private Boolean reproducible;
    private String actualResult;
    private String logOutput;
    private String errorMessage;
    private String evidenceFiles;
    private String notes;
}
