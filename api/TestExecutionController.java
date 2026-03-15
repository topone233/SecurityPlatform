package org.example.securityplatform.api;

import lombok.RequiredArgsConstructor;
import org.example.securityplatform.common.Result;
import org.example.securityplatform.dto.*;
import org.example.securityplatform.service.TestExecutionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 测试执行控制器
 */
@RestController
@RequestMapping("/api/test-execution")
@RequiredArgsConstructor
public class TestExecutionController {

    private final TestExecutionService executionService;

    /**
     * 创建测试执行记录
     */
    @PostMapping
    public Result<TestExecutionResponse> createExecution(@RequestBody TestExecutionRequest request) {
        TestExecutionResponse response = executionService.createExecution(request);
        return Result.success(response);
    }

    /**
     * 批量创建测试执行（为所有TODO状态的测试清单创建执行记录）
     */
    @PostMapping("/batch/{artifactId}")
    public Result<List<TestExecutionResponse>> batchCreateExecutions(@PathVariable Long artifactId) {
        List<TestExecutionResponse> responses = executionService.batchCreateExecutions(artifactId);
        return Result.success(responses);
    }

    /**
     * 开始执行测试
     */
    @PutMapping("/{id}/start")
    public Result<TestExecutionResponse> startExecution(@PathVariable Long id) {
        TestExecutionResponse response = executionService.startExecution(id);
        return Result.success(response);
    }

    /**
     * 完成测试执行
     */
    @PutMapping("/{id}/complete")
    public Result<TestExecutionResponse> completeExecution(
            @PathVariable Long id,
            @RequestBody TestExecutionResultRequest result) {
        TestExecutionResponse response = executionService.completeExecution(id, result);
        return Result.success(response);
    }

    /**
     * 取消测试执行
     */
    @PutMapping("/{id}/cancel")
    public Result<TestExecutionResponse> cancelExecution(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        TestExecutionResponse response = executionService.cancelExecution(id, reason);
        return Result.success(response);
    }

    /**
     * 获取测试执行记录
     */
    @GetMapping("/{id}")
    public Result<TestExecutionResponse> getExecution(@PathVariable Long id) {
        TestExecutionResponse response = executionService.getExecution(id);
        return Result.success(response);
    }

    /**
     * 获取制品的所有测试执行记录
     */
    @GetMapping("/artifact/{artifactId}")
    public Result<List<TestExecutionResponse>> getExecutionsByArtifact(@PathVariable Long artifactId) {
        List<TestExecutionResponse> responses = executionService.getExecutionsByArtifact(artifactId);
        return Result.success(responses);
    }

    /**
     * 获取测试清单的执行记录
     */
    @GetMapping("/checklist/{checklistId}")
    public Result<List<TestExecutionResponse>> getExecutionsByChecklist(@PathVariable Long checklistId) {
        List<TestExecutionResponse> responses = executionService.getExecutionsByChecklist(checklistId);
        return Result.success(responses);
    }

    /**
     * 获取执行统计
     */
    @GetMapping("/statistics/{artifactId}")
    public Result<Map<String, Object>> getExecutionStatistics(@PathVariable Long artifactId) {
        Map<String, Object> stats = executionService.getExecutionStatistics(artifactId);
        return Result.success(stats);
    }
}
