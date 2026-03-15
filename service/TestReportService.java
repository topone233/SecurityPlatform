package org.example.securityplatform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.dto.*;
import org.example.securityplatform.entity.*;
import org.example.securityplatform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 测试报告服务
 * 生成和管理测试报告
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestReportService {

    private final TestReportRepository reportRepository;
    private final TestExecutionRepository executionRepository;
    private final TestChecklistRepository checklistRepository;
    private final ComponentInfoRepository componentInfoRepository;
    private final DangerFunctionRepository dangerFunctionRepository;
    private final ConfigInfoRepository configInfoRepository;
    private final ArtifactRepository artifactRepository;
    private final ObjectMapper objectMapper;

    /**
     * 生成测试报告
     */
    @Transactional
    public TestReportResponse generateReport(Long artifactId, String reportType, String generatedBy) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new RuntimeException("制品不存在: " + artifactId));

        TestReport report = new TestReport();
        report.setArtifactId(artifactId);
        report.setReportName("测试报告 - " + artifact.getFileName() + " - " + LocalDateTime.now());
        report.setReportType(reportType != null ? reportType : "FULL");
        report.setReportStatus("GENERATING");
        report.setGeneratedBy(generatedBy);
        report.setStartTime(LocalDateTime.now());

        report = reportRepository.save(report);

        try {
            // 收集数据
            List<TestExecution> executions = executionRepository.findByArtifactId(artifactId);
            List<TestChecklist> checklists = checklistRepository.findByArtifactId(artifactId);
            List<ComponentInfo> components = componentInfoRepository.findByArtifactId(artifactId);
            List<DangerFunction> dangerFunctions = dangerFunctionRepository.findByArtifactId(artifactId);
            List<ConfigInfo> configs = configInfoRepository.findByArtifactId(artifactId);

            // 计算统计信息
            calculateStatistics(report, executions, checklists);

            // 生成摘要
            generateSummary(report, executions, dangerFunctions, configs);

            // 生成建议
            generateRecommendations(report, dangerFunctions, configs);

            // 计算执行时间
            report.setEndTime(LocalDateTime.now());
            report.setTotalDurationMs(ChronoUnit.MILLIS.between(report.getStartTime(), report.getEndTime()));
            report.setReportStatus("COMPLETED");

        } catch (Exception e) {
            log.error("生成报告失败", e);
            report.setReportStatus("FAILED");
        }

        report = reportRepository.save(report);
        return toResponse(report);
    }

    /**
     * 计算统计信息
     */
    private void calculateStatistics(TestReport report, List<TestExecution> executions, List<TestChecklist> checklists) {
        report.setTotalTestCases(checklists.size());

        // 按执行结果统计
        int passCount = 0, failCount = 0, skipCount = 0, errorCount = 0;
        for (TestExecution execution : executions) {
            if (execution.getTestResult() != null) {
                switch (execution.getTestResult()) {
                    case "PASS": passCount++; break;
                    case "FAIL": failCount++; break;
                    case "SKIP": skipCount++; break;
                    case "ERROR": errorCount++; break;
                }
            }
        }
        report.setExecutedCount(executions.size());
        report.setPassCount(passCount);
        report.setFailCount(failCount);
        report.setSkipCount(skipCount);
        report.setErrorCount(errorCount);

        // 按风险等级统计
        int criticalCount = 0, highCount = 0, mediumCount = 0, lowCount = 0;
        for (TestChecklist checklist : checklists) {
            if (checklist.getRiskLevel() != null) {
                switch (checklist.getRiskLevel()) {
                    case "CRITICAL": criticalCount++; break;
                    case "HIGH": highCount++; break;
                    case "MEDIUM": mediumCount++; break;
                    case "LOW": lowCount++; break;
                }
            }
        }
        report.setCriticalCount(criticalCount);
        report.setHighCount(highCount);
        report.setMediumCount(mediumCount);
        report.setLowCount(lowCount);
    }

    /**
     * 生成摘要
     */
    private void generateSummary(TestReport report, List<TestExecution> executions,
                                  List<DangerFunction> dangerFunctions, List<ConfigInfo> configs) {
        StringBuilder summary = new StringBuilder();

        summary.append("## 测试报告摘要\n\n");

        // 执行情况
        summary.append("### 测试执行情况\n");
        summary.append(String.format("- 总测试用例数: %d\n", report.getTotalTestCases()));
        summary.append(String.format("- 已执行: %d\n", report.getExecutedCount()));
        summary.append(String.format("- 通过: %d\n", report.getPassCount()));
        summary.append(String.format("- 失败: %d\n", report.getFailCount()));
        summary.append(String.format("- 跳过: %d\n", report.getSkipCount()));
        summary.append(String.format("- 错误: %d\n\n", report.getErrorCount()));

        // 通过率
        int total = report.getPassCount() + report.getFailCount() + report.getSkipCount() + report.getErrorCount();
        double passRate = total > 0 ? (double) report.getPassCount() / total * 100 : 0;
        summary.append(String.format("### 通过率: %.2f%%\n\n", passRate));

        // 危险函数统计
        summary.append("### 危险函数分析\n");
        Map<String, Long> functionTypeCounts = dangerFunctions.stream()
                .collect(Collectors.groupingBy(DangerFunction::getFunctionType, Collectors.counting()));
        functionTypeCounts.forEach((type, count) ->
                summary.append(String.format("- %s: %d\n", type, count)));
        summary.append("\n");

        // 配置问题
        summary.append("### 配置项分析\n");
        summary.append(String.format("- 总配置项: %d\n", configs.size()));

        report.setSummary(summary.toString());

        // 漏洞摘要JSON
        try {
            Map<String, Object> vulnSummary = new HashMap<>();
            vulnSummary.put("critical", report.getCriticalCount());
            vulnSummary.put("high", report.getHighCount());
            vulnSummary.put("medium", report.getMediumCount());
            vulnSummary.put("low", report.getLowCount());
            report.setVulnerabilitySummary(objectMapper.writeValueAsString(vulnSummary));
        } catch (JsonProcessingException e) {
            log.warn("生成漏洞摘要失败", e);
        }
    }

    /**
     * 生成建议
     */
    private void generateRecommendations(TestReport report, List<DangerFunction> dangerFunctions, List<ConfigInfo> configs) {
        StringBuilder recommendations = new StringBuilder();

        recommendations.append("## 安全建议\n\n");

        // 基于危险函数的建议
        Set<String> functionTypes = dangerFunctions.stream()
                .map(DangerFunction::getFunctionType)
                .collect(Collectors.toSet());

        if (functionTypes.contains("COMMAND_EXECUTION")) {
            recommendations.append("### 命令执行风险\n");
            recommendations.append("- 检测到命令执行函数调用，建议:\n");
            recommendations.append("  1. 避免直接拼接用户输入到命令中\n");
            recommendations.append("  2. 使用参数化方式传递命令参数\n");
            recommendations.append("  3. 对用户输入进行严格的白名单校验\n\n");
        }

        if (functionTypes.contains("DESERIALIZATION")) {
            recommendations.append("### 反序列化风险\n");
            recommendations.append("- 检测到反序列化操作，建议:\n");
            recommendations.append("  1. 使用安全的序列化方式（如JSON）\n");
            recommendations.append("  2. 配置反序列化白名单\n");
            recommendations.append("  3. 对反序列化数据进行签名校验\n\n");
        }

        if (functionTypes.contains("SQL_EXECUTION")) {
            recommendations.append("### SQL注入风险\n");
            recommendations.append("- 检测到SQL执行操作，建议:\n");
            recommendations.append("  1. 使用预编译语句（PreparedStatement）\n");
            recommendations.append("  2. 避免拼接SQL语句\n");
            recommendations.append("  3. 使用ORM框架的参数化查询\n\n");
        }

        // 基于配置的建议
        for (ConfigInfo config : configs) {
            String key = config.getConfigKey().toLowerCase();
            String value = config.getConfigValue();

            if (key.contains("debug") && "true".equalsIgnoreCase(value)) {
                recommendations.append("### 调试配置风险\n");
                recommendations.append("- 检测到调试模式开启: ").append(config.getConfigKey()).append("\n");
                recommendations.append("- 建议: 生产环境关闭调试模式\n\n");
            }

            if (key.contains("password") && !value.contains("***") && !value.contains("${")) {
                recommendations.append("### 敏感配置风险\n");
                recommendations.append("- 检测到可能的明文密码配置: ").append(config.getConfigKey()).append("\n");
                recommendations.append("- 建议: 使用加密配置或密钥管理服务\n\n");
            }
        }

        report.setRecommendations(recommendations.toString());
    }

    /**
     * 获取报告
     */
    public TestReportResponse getReport(Long reportId) {
        TestReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("报告不存在: " + reportId));
        return toResponse(report);
    }

    /**
     * 获取制品的报告列表
     */
    public List<TestReportResponse> getReportsByArtifact(Long artifactId) {
        return reportRepository.findByArtifactIdOrderByCreatedAtDesc(artifactId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取最新的完成报告
     */
    public TestReportResponse getLatestReport(Long artifactId) {
        TestReport report = reportRepository.findLatestCompletedReport(artifactId)
                .orElseThrow(() -> new RuntimeException("没有找到已完成的报告"));
        return toResponse(report);
    }

    /**
     * 删除报告
     */
    @Transactional
    public void deleteReport(Long reportId) {
        reportRepository.deleteById(reportId);
    }

    /**
     * 转换为响应DTO
     */
    private TestReportResponse toResponse(TestReport report) {
        Map<String, Object> vulnSummary = null;
        Map<String, Object> componentSummary = null;

        try {
            if (report.getVulnerabilitySummary() != null) {
                vulnSummary = objectMapper.readValue(report.getVulnerabilitySummary(),
                        new TypeReference<Map<String, Object>>() {});
            }
            if (report.getComponentSummary() != null) {
                componentSummary = objectMapper.readValue(report.getComponentSummary(),
                        new TypeReference<Map<String, Object>>() {});
            }
        } catch (JsonProcessingException e) {
            log.warn("解析JSON失败", e);
        }

        return TestReportResponse.builder()
                .id(report.getId())
                .artifactId(report.getArtifactId())
                .reportName(report.getReportName())
                .reportType(report.getReportType())
                .reportStatus(report.getReportStatus())
                .generatedBy(report.getGeneratedBy())
                .generatedAt(report.getGeneratedAt())
                .totalTestCases(report.getTotalTestCases())
                .executedCount(report.getExecutedCount())
                .passCount(report.getPassCount())
                .failCount(report.getFailCount())
                .skipCount(report.getSkipCount())
                .errorCount(report.getErrorCount())
                .criticalCount(report.getCriticalCount())
                .highCount(report.getHighCount())
                .mediumCount(report.getMediumCount())
                .lowCount(report.getLowCount())
                .startTime(report.getStartTime())
                .endTime(report.getEndTime())
                .totalDurationMs(report.getTotalDurationMs())
                .summary(report.getSummary())
                .vulnerabilitySummary(vulnSummary)
                .componentSummary(componentSummary)
                .recommendations(report.getRecommendations())
                .reportFilePath(report.getReportFilePath())
                .reportFormat(report.getReportFormat())
                .fileSizeBytes(report.getFileSizeBytes())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
