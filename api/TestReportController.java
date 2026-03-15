package org.example.securityplatform.api;

import lombok.RequiredArgsConstructor;
import org.example.securityplatform.common.Result;
import org.example.securityplatform.dto.TestReportResponse;
import org.example.securityplatform.service.TestReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试报告控制器
 */
@RestController
@RequestMapping("/api/test-report")
@RequiredArgsConstructor
public class TestReportController {

    private final TestReportService reportService;

    /**
     * 生成测试报告
     */
    @PostMapping("/generate/{artifactId}")
    public Result<TestReportResponse> generateReport(
            @PathVariable Long artifactId,
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) String generatedBy) {
        TestReportResponse response = reportService.generateReport(artifactId, reportType, generatedBy);
        return Result.success(response);
    }

    /**
     * 获取报告
     */
    @GetMapping("/{id}")
    public Result<TestReportResponse> getReport(@PathVariable Long id) {
        TestReportResponse response = reportService.getReport(id);
        return Result.success(response);
    }

    /**
     * 获取制品的报告列表
     */
    @GetMapping("/artifact/{artifactId}")
    public Result<List<TestReportResponse>> getReportsByArtifact(@PathVariable Long artifactId) {
        List<TestReportResponse> responses = reportService.getReportsByArtifact(artifactId);
        return Result.success(responses);
    }

    /**
     * 获取最新的完成报告
     */
    @GetMapping("/latest/{artifactId}")
    public Result<TestReportResponse> getLatestReport(@PathVariable Long artifactId) {
        TestReportResponse response = reportService.getLatestReport(artifactId);
        return Result.success(response);
    }

    /**
     * 删除报告
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return Result.success(null);
    }
}
