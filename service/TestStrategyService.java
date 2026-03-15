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

import java.util.*;
import java.util.stream.Collectors;

/**
 * 测试策略引擎服务
 * 负责根据分析结果自动匹配测试用例，生成测试清单
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestStrategyService {

    private final TestCaseRepository testCaseRepository;
    private final ConfigCheckItemRepository configCheckItemRepository;
    private final TestChecklistRepository testChecklistRepository;
    private final ComponentInfoRepository componentInfoRepository;
    private final DangerFunctionRepository dangerFunctionRepository;
    private final ConfigInfoRepository configInfoRepository;
    private final RiskExclusionService riskExclusionService;
    private final ObjectMapper objectMapper;

    /**
     * 为制品生成测试清单
     */
    @Transactional
    public StrategyMatchResponse generateTestChecklist(Long artifactId) {
        log.info("开始为制品 {} 生成测试清单", artifactId);

        // 清除旧的测试清单
        testChecklistRepository.deleteByArtifactId(artifactId);

        // 获取制品的分析结果
        List<ComponentInfo> components = componentInfoRepository.findByArtifactId(artifactId);
        List<DangerFunction> dangerFunctions = dangerFunctionRepository.findByArtifactId(artifactId);
        List<ConfigInfo> configs = configInfoRepository.findByArtifactId(artifactId);

        // 获取所有启用的测试用例
        List<TestCase> testCases = testCaseRepository.findByEnabledTrue();

        // 匹配测试用例
        List<TestChecklist> checklists = new ArrayList<>();

        for (TestCase testCase : testCases) {
            TestChecklist checklist = matchTestCase(artifactId, testCase, components, dangerFunctions, configs);
            if (checklist != null) {
                checklists.add(checklist);
            }
        }

        // 保存测试清单
        checklists = testChecklistRepository.saveAll(checklists);

        // 执行风险排除
        riskExclusionService.performRiskExclusion(artifactId);

        // 重新加载更新后的清单
        checklists = testChecklistRepository.findByArtifactId(artifactId);

        // 构建响应
        return buildStrategyMatchResponse(artifactId, checklists);
    }

    /**
     * 匹配单个测试用例
     */
    private TestChecklist matchTestCase(Long artifactId, TestCase testCase,
                                         List<ComponentInfo> components,
                                         List<DangerFunction> dangerFunctions,
                                         List<ConfigInfo> configs) {
        TestChecklist checklist = new TestChecklist();
        checklist.setArtifactId(artifactId);
        checklist.setTestCaseId(testCase.getId());
        checklist.setCaseName(testCase.getCaseName());
        checklist.setVulnerabilityType(testCase.getVulnerabilityType());
        checklist.setRiskLevel(testCase.getRiskLevel());
        checklist.setStatus("TODO");

        boolean matched = false;

        // 检查组件匹配
        String matchedComponent = matchComponents(testCase.getApplicableComponents(), components);
        if (matchedComponent != null) {
            checklist.setMatchedComponent(matchedComponent);
            matched = true;
        }

        // 检查危险函数匹配
        String matchedDangerFunction = matchDangerFunctions(testCase.getApplicableDangerFunctions(), dangerFunctions);
        if (matchedDangerFunction != null) {
            checklist.setMatchedDangerFunction(matchedDangerFunction);
            matched = true;
        }

        // 检查配置匹配
        String matchedConfig = matchConfigs(testCase.getVulnerabilityType(), configs);
        if (matchedConfig != null) {
            checklist.setMatchedConfigKey(matchedConfig);
            matched = true;
        }

        // 如果没有匹配任何条件，标记为NA
        if (!matched) {
            // 检查是否是通用测试用例（没有特定组件/危险函数要求）
            if (isEmptyOrNull(testCase.getApplicableComponents()) &&
                isEmptyOrNull(testCase.getApplicableDangerFunctions())) {
                // 通用测试用例，保持TODO状态
                matched = true;
            } else {
                checklist.setStatus("NA");
                checklist.setExclusionReason("不适用：未检测到相关组件或危险函数");
            }
        }

        return checklist;
    }

    /**
     * 匹配组件
     */
    private String matchComponents(String applicableComponents, List<ComponentInfo> components) {
        if (isEmptyOrNull(applicableComponents) || components.isEmpty()) {
            return null;
        }

        try {
            List<String> componentList = objectMapper.readValue(applicableComponents, new TypeReference<List<String>>() {});
            for (ComponentInfo component : components) {
                for (String pattern : componentList) {
                    if (component.getComponentName().toLowerCase().contains(pattern.toLowerCase())) {
                        return component.getComponentName();
                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("解析组件列表失败: {}", applicableComponents);
        }

        return null;
    }

    /**
     * 匹配危险函数
     */
    private String matchDangerFunctions(String applicableDangerFunctions, List<DangerFunction> dangerFunctions) {
        if (isEmptyOrNull(applicableDangerFunctions) || dangerFunctions.isEmpty()) {
            return null;
        }

        try {
            List<String> functionTypes = objectMapper.readValue(applicableDangerFunctions, new TypeReference<List<String>>() {});
            for (DangerFunction df : dangerFunctions) {
                for (String type : functionTypes) {
                    if (df.getFunctionType().equalsIgnoreCase(type)) {
                        return df.getFunctionName();
                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("解析危险函数列表失败: {}", applicableDangerFunctions);
        }

        return null;
    }

    /**
     * 匹配配置
     */
    private String matchConfigs(String vulnerabilityType, List<ConfigInfo> configs) {
        if (configs.isEmpty()) {
            return null;
        }

        // 根据漏洞类型匹配相关配置
        Map<String, List<String>> vulnerabilityConfigMap = new HashMap<>();
        vulnerabilityConfigMap.put("SQL_INJECTION", Arrays.asList("datasource", "jdbc", "mybatis", "hibernate"));
        vulnerabilityConfigMap.put("XSS", Arrays.asList("thymeleaf", "freemarker", "jsp", "servlet"));
        vulnerabilityConfigMap.put("AUTH_BYPASS", Arrays.asList("security", "auth", "login", "session"));
        vulnerabilityConfigMap.put("INFO_DISCLOSURE", Arrays.asList("debug", "log", "error", "trace"));

        List<String> configPatterns = vulnerabilityConfigMap.get(vulnerabilityType);
        if (configPatterns == null) {
            return null;
        }

        for (ConfigInfo config : configs) {
            for (String pattern : configPatterns) {
                if (config.getConfigKey().toLowerCase().contains(pattern.toLowerCase())) {
                    return config.getConfigKey();
                }
            }
        }

        return null;
    }

    /**
     * 执行配置检查
     */
    @Transactional
    public List<Map<String, Object>> performConfigCheck(Long artifactId) {
        List<ConfigInfo> configs = configInfoRepository.findByArtifactId(artifactId);
        List<ConfigCheckItem> checkItems = configCheckItemRepository.findByEnabledTrue();

        List<Map<String, Object>> results = new ArrayList<>();

        for (ConfigCheckItem item : checkItems) {
            for (ConfigInfo config : configs) {
                if (config.getConfigKey().toLowerCase().contains(item.getConfigKeyPattern().toLowerCase())) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("configKey", config.getConfigKey());
                    result.put("configValue", config.getConfigValue());
                    result.put("checkItem", item.getItemName());
                    result.put("category", item.getCategory());
                    result.put("riskLevel", item.getRiskLevel());
                    result.put("description", item.getDescription());
                    result.put("remediation", item.getRemediation());

                    // 检查是否是不安全值
                    boolean isUnsafe = checkUnsafeValue(config.getConfigValue(), item);
                    result.put("isUnsafe", isUnsafe);

                    results.add(result);
                }
            }
        }

        return results;
    }

    /**
     * 检查配置值是否不安全
     */
    private boolean checkUnsafeValue(String value, ConfigCheckItem item) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        // 检查不安全值列表
        if (item.getUnsafeValues() != null) {
            try {
                List<String> unsafeValues = objectMapper.readValue(item.getUnsafeValues(), new TypeReference<List<String>>() {});
                for (String unsafe : unsafeValues) {
                    if (value.toLowerCase().contains(unsafe.toLowerCase())) {
                        return true;
                    }
                }
            } catch (JsonProcessingException e) {
                log.warn("解析不安全值列表失败: {}", item.getUnsafeValues());
            }
        }

        // 检查期望值
        if (item.getExpectedValue() != null) {
            return !value.toLowerCase().matches(item.getExpectedValue().toLowerCase());
        }

        return false;
    }

    /**
     * 构建策略匹配响应
     */
    private StrategyMatchResponse buildStrategyMatchResponse(Long artifactId, List<TestChecklist> checklists) {
        Map<String, Integer> vulnerabilityTypeCounts = new HashMap<>();
        Map<String, Integer> riskLevelCounts = new HashMap<>();
        int todoCount = 0, passCount = 0, naCount = 0;

        for (TestChecklist checklist : checklists) {
            vulnerabilityTypeCounts.merge(checklist.getVulnerabilityType(), 1, Integer::sum);
            riskLevelCounts.merge(checklist.getRiskLevel(), 1, Integer::sum);

            switch (checklist.getStatus()) {
                case "TODO": todoCount++; break;
                case "PASS": passCount++; break;
                case "NA": naCount++; break;
            }
        }

        // 获取风险排除统计
        long exclusionCount = testChecklistRepository.countByArtifactIdAndStatus(artifactId, "PASS");
        long pendingReviewCount = testChecklistRepository.findByArtifactIdAndStatus(artifactId, "PENDING_REVIEW").size();

        List<TestChecklistResponse> checklistResponses = checklists.stream()
                .map(this::toTestChecklistResponse)
                .collect(Collectors.toList());

        return StrategyMatchResponse.builder()
                .artifactId(artifactId)
                .totalTestCases(checklists.size())
                .todoCount(todoCount)
                .passCount(passCount)
                .naCount(naCount)
                .vulnerabilityTypeCounts(vulnerabilityTypeCounts)
                .riskLevelCounts(riskLevelCounts)
                .testChecklist(checklistResponses)
                .exclusionCount((int) exclusionCount)
                .pendingReviewCount((int) pendingReviewCount)
                .build();
    }

    /**
     * 转换为响应DTO
     */
    private TestChecklistResponse toTestChecklistResponse(TestChecklist checklist) {
        return TestChecklistResponse.builder()
                .id(checklist.getId())
                .artifactId(checklist.getArtifactId())
                .testCaseId(checklist.getTestCaseId())
                .caseName(checklist.getCaseName())
                .vulnerabilityType(checklist.getVulnerabilityType())
                .riskLevel(checklist.getRiskLevel())
                .matchedComponent(checklist.getMatchedComponent())
                .matchedDangerFunction(checklist.getMatchedDangerFunction())
                .matchedConfigKey(checklist.getMatchedConfigKey())
                .status(checklist.getStatus())
                .exclusionReason(checklist.getExclusionReason())
                .exclusionRuleId(checklist.getExclusionRuleId())
                .testResult(checklist.getTestResult())
                .testNotes(checklist.getTestNotes())
                .testedBy(checklist.getTestedBy())
                .testedAt(checklist.getTestedAt())
                .createdAt(checklist.getCreatedAt())
                .build();
    }

    /**
     * 获取制品的测试清单
     */
    public StrategyMatchResponse getTestChecklist(Long artifactId) {
        List<TestChecklist> checklists = testChecklistRepository.findByArtifactIdOrderByRiskLevel(artifactId);
        if (checklists.isEmpty()) {
            return generateTestChecklist(artifactId);
        }
        return buildStrategyMatchResponse(artifactId, checklists);
    }

    /**
     * 更新测试清单状态
     */
    @Transactional
    public TestChecklistResponse updateChecklistStatus(Long checklistId, String status, String testResult, String testNotes) {
        TestChecklist checklist = testChecklistRepository.findById(checklistId)
                .orElseThrow(() -> new RuntimeException("测试清单不存在: " + checklistId));

        checklist.setStatus(status);
        checklist.setTestResult(testResult);
        checklist.setTestNotes(testNotes);
        checklist.setTestedAt(java.time.LocalDateTime.now());

        checklist = testChecklistRepository.save(checklist);
        return toTestChecklistResponse(checklist);
    }

    private boolean isEmptyOrNull(String str) {
        return str == null || str.trim().isEmpty() || "[]".equals(str.trim());
    }
}