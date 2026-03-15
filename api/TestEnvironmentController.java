package org.example.securityplatform.api;

import lombok.RequiredArgsConstructor;
import org.example.securityplatform.common.Result;
import org.example.securityplatform.dto.TestEnvironmentResponse;
import org.example.securityplatform.service.TestEnvironmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 测试环境控制器
 */
@RestController
@RequestMapping("/api/test-environment")
@RequiredArgsConstructor
public class TestEnvironmentController {

    private final TestEnvironmentService environmentService;

    /**
     * 获取所有活跃的测试环境
     */
    @GetMapping("/active")
    public Result<List<TestEnvironmentResponse>> getActiveEnvironments() {
        List<TestEnvironmentResponse> responses = environmentService.getActiveEnvironments();
        return Result.success(responses);
    }

    /**
     * 获取所有测试环境
     */
    @GetMapping
    public Result<List<TestEnvironmentResponse>> getAllEnvironments() {
        List<TestEnvironmentResponse> responses = environmentService.getAllEnvironments();
        return Result.success(responses);
    }

    /**
     * 获取测试环境详情
     */
    @GetMapping("/{id}")
    public Result<TestEnvironmentResponse> getEnvironment(@PathVariable Long id) {
        TestEnvironmentResponse response = environmentService.getEnvironment(id);
        return Result.success(response);
    }

    /**
     * 按类型获取测试环境
     */
    @GetMapping("/type/{envType}")
    public Result<List<TestEnvironmentResponse>> getEnvironmentsByType(@PathVariable String envType) {
        List<TestEnvironmentResponse> responses = environmentService.getEnvironmentsByType(envType);
        return Result.success(responses);
    }

    /**
     * 创建测试环境
     */
    @PostMapping
    public Result<TestEnvironmentResponse> createEnvironment(
            @RequestParam String envName,
            @RequestParam String envType,
            @RequestParam(required = false) String description,
            @RequestBody(required = false) Map<String, Object> configData) {
        TestEnvironmentResponse response = environmentService.createEnvironment(envName, envType, description, configData);
        return Result.success(response);
    }

    /**
     * 更新测试环境
     */
    @PutMapping("/{id}")
    public Result<TestEnvironmentResponse> updateEnvironment(
            @PathVariable Long id,
            @RequestParam(required = false) String envName,
            @RequestParam(required = false) String envType,
            @RequestParam(required = false) String description,
            @RequestBody(required = false) Map<String, Object> configData) {
        TestEnvironmentResponse response = environmentService.updateEnvironment(id, envName, envType, description, configData);
        return Result.success(response);
    }

    /**
     * 启用测试环境
     */
    @PutMapping("/{id}/enable")
    public Result<TestEnvironmentResponse> enableEnvironment(@PathVariable Long id) {
        TestEnvironmentResponse response = environmentService.toggleEnvironment(id, true);
        return Result.success(response);
    }

    /**
     * 禁用测试环境
     */
    @PutMapping("/{id}/disable")
    public Result<TestEnvironmentResponse> disableEnvironment(@PathVariable Long id) {
        TestEnvironmentResponse response = environmentService.toggleEnvironment(id, false);
        return Result.success(response);
    }

    /**
     * 删除测试环境
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteEnvironment(@PathVariable Long id) {
        environmentService.deleteEnvironment(id);
        return Result.success(null);
    }
}
