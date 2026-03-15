package org.example.securityplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.dto.TestCaseRequest;
import org.example.securityplatform.dto.TestCaseResponse;
import org.example.securityplatform.entity.TestCase;
import org.example.securityplatform.repository.TestCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试用例服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;

    /**
     * 获取所有测试用例
     */
    public List<TestCaseResponse> getAllTestCases() {
        return testCaseRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有启用的测试用例
     */
    public List<TestCaseResponse> getAllEnabledTestCases() {
        return testCaseRepository.findByEnabledTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取测试用例
     */
    public TestCaseResponse getTestCaseById(Long id) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("测试用例不存在: " + id));
        return toResponse(testCase);
    }

    /**
     * 根据漏洞类型获取测试用例
     */
    public List<TestCaseResponse> getTestCasesByVulnerabilityType(String vulnerabilityType) {
        return testCaseRepository.findByVulnerabilityTypeAndEnabledTrue(vulnerabilityType).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 创建测试用例
     */
    @Transactional
    public TestCaseResponse createTestCase(TestCaseRequest request) {
        TestCase testCase = new TestCase();
        testCase.setCaseName(request.getCaseName());
        testCase.setVulnerabilityType(request.getVulnerabilityType());
        testCase.setRiskLevel(request.getRiskLevel());
        testCase.setApplicableComponents(request.getApplicableComponents());
        testCase.setApplicableDangerFunctions(request.getApplicableDangerFunctions());
        testCase.setDetectionMethod(request.getDetectionMethod());
        testCase.setTestSteps(request.getTestSteps());
        testCase.setPayloadTemplate(request.getPayloadTemplate());
        testCase.setExpectedResult(request.getExpectedResult());
        testCase.setReferences(request.getReferences());
        testCase.setEnabled(true);

        testCase = testCaseRepository.save(testCase);
        return toResponse(testCase);
    }

    /**
     * 更新测试用例
     */
    @Transactional
    public TestCaseResponse updateTestCase(Long id, TestCaseRequest request) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("测试用例不存在: " + id));

        if (request.getCaseName() != null) {
            testCase.setCaseName(request.getCaseName());
        }
        if (request.getVulnerabilityType() != null) {
            testCase.setVulnerabilityType(request.getVulnerabilityType());
        }
        if (request.getRiskLevel() != null) {
            testCase.setRiskLevel(request.getRiskLevel());
        }
        if (request.getApplicableComponents() != null) {
            testCase.setApplicableComponents(request.getApplicableComponents());
        }
        if (request.getApplicableDangerFunctions() != null) {
            testCase.setApplicableDangerFunctions(request.getApplicableDangerFunctions());
        }
        if (request.getDetectionMethod() != null) {
            testCase.setDetectionMethod(request.getDetectionMethod());
        }
        if (request.getTestSteps() != null) {
            testCase.setTestSteps(request.getTestSteps());
        }
        if (request.getPayloadTemplate() != null) {
            testCase.setPayloadTemplate(request.getPayloadTemplate());
        }
        if (request.getExpectedResult() != null) {
            testCase.setExpectedResult(request.getExpectedResult());
        }
        if (request.getReferences() != null) {
            testCase.setReferences(request.getReferences());
        }

        testCase = testCaseRepository.save(testCase);
        return toResponse(testCase);
    }

    /**
     * 删除测试用例
     */
    @Transactional
    public void deleteTestCase(Long id) {
        testCaseRepository.deleteById(id);
    }

    /**
     * 启用/禁用测试用例
     */
    @Transactional
    public TestCaseResponse toggleTestCase(Long id, boolean enabled) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("测试用例不存在: " + id));
        testCase.setEnabled(enabled);
        testCase = testCaseRepository.save(testCase);
        return toResponse(testCase);
    }

    /**
     * 获取所有漏洞类型
     */
    public List<String> getAllVulnerabilityTypes() {
        return testCaseRepository.findAllVulnerabilityTypes();
    }

    /**
     * 转换为响应DTO
     */
    private TestCaseResponse toResponse(TestCase testCase) {
        return TestCaseResponse.builder()
                .id(testCase.getId())
                .caseName(testCase.getCaseName())
                .vulnerabilityType(testCase.getVulnerabilityType())
                .riskLevel(testCase.getRiskLevel())
                .applicableComponents(testCase.getApplicableComponents())
                .applicableDangerFunctions(testCase.getApplicableDangerFunctions())
                .detectionMethod(testCase.getDetectionMethod())
                .testSteps(testCase.getTestSteps())
                .payloadTemplate(testCase.getPayloadTemplate())
                .expectedResult(testCase.getExpectedResult())
                .references(testCase.getReferences())
                .enabled(testCase.getEnabled())
                .createdAt(testCase.getCreatedAt())
                .updatedAt(testCase.getUpdatedAt())
                .build();
    }
}