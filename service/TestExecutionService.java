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
 * 测试执行服务
 * 管理测试执行的生命周期
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestExecutionService {

    private final TestExecutionRepository executionRepository;
    private final TestChecklistRepository checklistRepository;
    private final TestCaseRepository testCaseRepository;
    private final TestEnvironmentRepository environmentRepository;
    private final ObjectMapper objectMapper;

    /**
     * 创建测试执行记录
     */
    @Transactional
    public TestExecutionResponse createExecution(TestExecutionRequest request) {
        TestExecution execution = new TestExecution();
        execution.setArtifactId(request.getArtifactId());
        execution.setChecklistId(request.getChecklistId());
        execution.setTestCaseId(request.getTestCaseId());
        execution.setExecutionName(request.getExecutionName());
        execution.setVulnerabilityType(request.getVulnerabilityType());
        execution.setExecutionType(request.getExecutionType() != null ? request.getExecutionType() : "MANUAL");
        execution.setExecutor(request.getExecutor());
        execution.setEnvironmentInfo(request.getEnvironmentInfo());
        execution.setTestData(request.getTestData());
        execution.setExpectedResult(request.getExpectedResult());
        execution.setNotes(request.getNotes());
        execution.setExecutionStatus("PENDING");

        execution = executionRepository.save(execution);
        return toResponse(execution);
    }

    /**
     * 开始执行测试
     */
    @Transactional
    public TestExecutionResponse startExecution(Long executionId) {
        TestExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new RuntimeException("测试执行记录不存在: " + executionId));

        if (!"PENDING".equals(execution.getExecutionStatus())) {
            throw new RuntimeException("测试执行状态不正确，无法启动: " + execution.getExecutionStatus());
        }

        execution.setExecutionStatus("RUNNING");
        execution.setStartTime(LocalDateTime.now());

        execution = executionRepository.save(execution);
        return toResponse(execution);
    }

    /**
     * 完成测试执行
     */
    @Transactional
    public TestExecutionResponse completeExecution(Long executionId, TestExecutionResultRequest result) {
        TestExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new RuntimeException("测试执行记录不存在: " + executionId));

        if (!"RUNNING".equals(execution.getExecutionStatus())) {
            throw new RuntimeException("测试执行状态不正确，无法完成: " + execution.getExecutionStatus());
        }

        LocalDateTime endTime = LocalDateTime.now();
        Long durationMs = null;
        if (execution.getStartTime() != null) {
            durationMs = ChronoUnit.MILLIS.between(execution.getStartTime(), endTime);
        }

        execution.setExecutionStatus("COMPLETED");
        execution.setEndTime(endTime);
        execution.setDurationMs(durationMs);
        execution.setTestResult(result.getTestResult());
        execution.setSeverity(result.getSeverity());
        execution.setReproducible(result.getReproducible());
        execution.setActualResult(result.getActualResult());
        execution.setLogOutput(result.getLogOutput());
        execution.setErrorMessage(result.getErrorMessage());
        execution.setEvidenceFiles(result.getEvidenceFiles());
        execution.setNotes(result.getNotes());

        execution = executionRepository.save(execution);

        // 更新关联的测试清单状态
        if (execution.getChecklistId() != null) {
            updateChecklistStatus(execution.getChecklistId(), result.getTestResult());
        }

        return toResponse(execution);
    }

    /**
     * 取消测试执行
     */
    @Transactional
    public TestExecutionResponse cancelExecution(Long executionId, String reason) {
        TestExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new RuntimeException("测试执行记录不存在: " + executionId));

        execution.setExecutionStatus("CANCELLED");
        execution.setEndTime(LocalDateTime.now());
        execution.setNotes(reason);

        execution = executionRepository.save(execution);
        return toResponse(execution);
    }

    /**
     * 更新测试清单状态
     */
    private void updateChecklistStatus(Long checklistId, String testResult) {
        TestChecklist checklist = checklistRepository.findById(checklistId).orElse(null);
        if (checklist != null) {
            String status = "PASS".equals(testResult) ? "PASS" : "FAIL";
            checklist.setStatus(status);
            checklistRepository.save(checklist);
        }
    }

    /**
     * 获取测试执行记录
     */
    public TestExecutionResponse getExecution(Long executionId) {
        TestExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new RuntimeException("测试执行记录不存在: " + executionId));
        return toResponse(execution);
    }

    /**
     * 获取制品的所有测试执行记录
     */
    public List<TestExecutionResponse> getExecutionsByArtifact(Long artifactId) {
        return executionRepository.findByArtifactIdOrderByCreatedAtDesc(artifactId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取测试清单的执行记录
     */
    public List<TestExecutionResponse> getExecutionsByChecklist(Long checklistId) {
        return executionRepository.findByChecklistId(checklistId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取执行统计
     */
    public Map<String, Object> getExecutionStatistics(Long artifactId) {
        Map<String, Object> stats = new HashMap<>();

        List<TestExecution> executions = executionRepository.findByArtifactId(artifactId);
        stats.put("totalExecutions", executions.size());

        // 按结果分组统计
        List<Object[]> resultCounts = executionRepository.countGroupByTestResult(artifactId);
        Map<String, Long> resultMap = new HashMap<>();
        for (Object[] row : resultCounts) {
            if (row[0] != null) {
                resultMap.put((String) row[0], (Long) row[1]);
            }
        }
        stats.put("passCount", resultMap.getOrDefault("PASS", 0L));
        stats.put("failCount", resultMap.getOrDefault("FAIL", 0L));
        stats.put("skipCount", resultMap.getOrDefault("SKIP", 0L));
        stats.put("errorCount", resultMap.getOrDefault("ERROR", 0L));

        // 按漏洞类型分组统计
        List<Object[]> vulnCounts = executionRepository.countGroupByVulnerabilityType(artifactId);
        Map<String, Long> vulnMap = new HashMap<>();
        for (Object[] row : vulnCounts) {
            if (row[0] != null) {
                vulnMap.put((String) row[0], (Long) row[1]);
            }
        }
        stats.put("vulnerabilityTypeCounts", vulnMap);

        // 计算通过率
        long total = resultMap.values().stream().mapToLong(Long::longValue).sum();
        long passCount = resultMap.getOrDefault("PASS", 0L);
        stats.put("passRate", total > 0 ? (double) passCount / total * 100 : 0);

        // 平均执行时间
        double avgDuration = executions.stream()
                .filter(e -> e.getDurationMs() != null)
                .mapToLong(TestExecution::getDurationMs)
                .average()
                .orElse(0);
        stats.put("avgDurationMs", avgDuration);

        return stats;
    }

    /**
     * 批量创建测试执行
     */
    @Transactional
    public List<TestExecutionResponse> batchCreateExecutions(Long artifactId) {
        List<TestChecklist> checklists = checklistRepository.findByArtifactIdAndStatus(artifactId, "TODO");
        List<TestExecution> executions = new ArrayList<>();

        for (TestChecklist checklist : checklists) {
            TestExecution execution = new TestExecution();
            execution.setArtifactId(artifactId);
            execution.setChecklistId(checklist.getId());
            execution.setTestCaseId(checklist.getTestCaseId());
            execution.setExecutionName("执行: " + checklist.getCaseName());
            execution.setVulnerabilityType(checklist.getVulnerabilityType());
            execution.setExecutionStatus("PENDING");
            execution.setExecutionType("MANUAL");
            executions.add(execution);
        }

        executions = executionRepository.saveAll(executions);
        return executions.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * 转换为响应DTO
     */
    private TestExecutionResponse toResponse(TestExecution execution) {
        return TestExecutionResponse.builder()
                .id(execution.getId())
                .artifactId(execution.getArtifactId())
                .checklistId(execution.getChecklistId())
                .testCaseId(execution.getTestCaseId())
                .executionName(execution.getExecutionName())
                .vulnerabilityType(execution.getVulnerabilityType())
                .executionStatus(execution.getExecutionStatus())
                .executionType(execution.getExecutionType())
                .executor(execution.getExecutor())
                .startTime(execution.getStartTime())
                .endTime(execution.getEndTime())
                .durationMs(execution.getDurationMs())
                .testResult(execution.getTestResult())
                .severity(execution.getSeverity())
                .reproducible(execution.getReproducible())
                .environmentInfo(execution.getEnvironmentInfo())
                .testData(execution.getTestData())
                .actualResult(execution.getActualResult())
                .expectedResult(execution.getExpectedResult())
                .evidenceFiles(execution.getEvidenceFiles())
                .logOutput(execution.getLogOutput())
                .errorMessage(execution.getErrorMessage())
                .notes(execution.getNotes())
                .createdAt(execution.getCreatedAt())
                .updatedAt(execution.getUpdatedAt())
                .build();
    }
}
