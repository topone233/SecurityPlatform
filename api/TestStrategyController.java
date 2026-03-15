package org.example.securityplatform.api;

import lombok.RequiredArgsConstructor;
import org.example.securityplatform.common.Result;
import org.example.securityplatform.dto.*;
import org.example.securityplatform.entity.TestCase;
import org.example.securityplatform.entity.ConfigCheckItem;
import org.example.securityplatform.service.TestStrategyService;
import org.example.securityplatform.service.TestCaseService;
import org.example.securityplatform.service.ConfigCheckItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 测试策略控制器
 * 提供测试策略引擎相关的API
 */
@RestController
@RequestMapping("/api/strategy")
@RequiredArgsConstructor
public class TestStrategyController {

    private final TestStrategyService testStrategyService;
    private final TestCaseService testCaseService;
    private final ConfigCheckItemService configCheckItemService;

    /**
     * 为制品生成测试清单
     */
    @PostMapping("/generate/{artifactId}")
    public Result<StrategyMatchResponse> generateTestChecklist(@PathVariable Long artifactId) {
        StrategyMatchResponse response = testStrategyService.generateTestChecklist(artifactId);
        return Result.success(response);
    }

    /**
     * 获取制品的测试清单
     */
    @GetMapping("/checklist/{artifactId}")
    public Result<StrategyMatchResponse> getTestChecklist(@PathVariable Long artifactId) {
        StrategyMatchResponse response = testStrategyService.getTestChecklist(artifactId);
        return Result.success(response);
    }

    /**
     * 更新测试清单项状态
     */
    @PutMapping("/checklist/{checklistId}/status")
    public Result<TestChecklistResponse> updateChecklistStatus(
            @PathVariable Long checklistId,
            @RequestParam String status,
            @RequestParam(required = false) String testResult,
            @RequestParam(required = false) String testNotes) {
        TestChecklistResponse response = testStrategyService.updateChecklistStatus(checklistId, status, testResult, testNotes);
        return Result.success(response);
    }

    /**
     * 执行配置检查
     */
    @GetMapping("/config-check/{artifactId}")
    public Result<List<Map<String, Object>>> performConfigCheck(@PathVariable Long artifactId) {
        List<Map<String, Object>> results = testStrategyService.performConfigCheck(artifactId);
        return Result.success(results);
    }

    // ==================== 测试用例管理 ====================

    /**
     * 获取所有测试用例
     */
    @GetMapping("/test-cases")
    public Result<List<TestCaseResponse>> getAllTestCases() {
        List<TestCaseResponse> cases = testCaseService.getAllTestCases();
        return Result.success(cases);
    }

    /**
     * 获取测试用例详情
     */
    @GetMapping("/test-cases/{id}")
    public Result<TestCaseResponse> getTestCase(@PathVariable Long id) {
        TestCaseResponse response = testCaseService.getTestCaseById(id);
        return Result.success(response);
    }

    /**
     * 创建测试用例
     */
    @PostMapping("/test-cases")
    public Result<TestCaseResponse> createTestCase(@RequestBody TestCaseRequest request) {
        TestCaseResponse response = testCaseService.createTestCase(request);
        return Result.success(response);
    }

    /**
     * 更新测试用例
     */
    @PutMapping("/test-cases/{id}")
    public Result<TestCaseResponse> updateTestCase(@PathVariable Long id, @RequestBody TestCaseRequest request) {
        TestCaseResponse response = testCaseService.updateTestCase(id, request);
        return Result.success(response);
    }

    /**
     * 删除测试用例
     */
    @DeleteMapping("/test-cases/{id}")
    public Result<Void> deleteTestCase(@PathVariable Long id) {
        testCaseService.deleteTestCase(id);
        return Result.success(null);
    }

    /**
     * 按漏洞类型获取测试用例
     */
    @GetMapping("/test-cases/by-type/{vulnerabilityType}")
    public Result<List<TestCaseResponse>> getTestCasesByType(@PathVariable String vulnerabilityType) {
        List<TestCaseResponse> cases = testCaseService.getTestCasesByVulnerabilityType(vulnerabilityType);
        return Result.success(cases);
    }

    // ==================== 配置检查项管理 ====================

    /**
     * 获取所有配置检查项
     */
    @GetMapping("/config-items")
    public Result<List<ConfigCheckItemResponse>> getAllConfigCheckItems() {
        List<ConfigCheckItemResponse> items = configCheckItemService.getAllConfigCheckItems();
        return Result.success(items);
    }

    /**
     * 获取配置检查项详情
     */
    @GetMapping("/config-items/{id}")
    public Result<ConfigCheckItemResponse> getConfigCheckItem(@PathVariable Long id) {
        ConfigCheckItemResponse response = configCheckItemService.getConfigCheckItemById(id);
        return Result.success(response);
    }

    /**
     * 按类别获取配置检查项
     */
    @GetMapping("/config-items/by-category/{category}")
    public Result<List<ConfigCheckItemResponse>> getConfigCheckItemsByCategory(@PathVariable String category) {
        List<ConfigCheckItemResponse> items = configCheckItemService.getConfigCheckItemsByCategory(category);
        return Result.success(items);
    }

    /**
     * 获取所有漏洞类型
     */
    @GetMapping("/vulnerability-types")
    public Result<List<String>> getAllVulnerabilityTypes() {
        List<String> types = testCaseService.getAllVulnerabilityTypes();
        return Result.success(types);
    }

    /**
     * 获取所有配置检查类别
     */
    @GetMapping("/config-categories")
    public Result<List<String>> getAllConfigCategories() {
        List<String> categories = configCheckItemService.getAllCategories();
        return Result.success(categories);
    }
}