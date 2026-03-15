package org.example.securityplatform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.dto.RiskExclusionResponse;
import org.example.securityplatform.entity.*;
import org.example.securityplatform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 风险自动排除服务
 * 基于规则自动判断哪些风险可以被排除
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskExclusionService {

    private final RiskExclusionRuleRepository ruleRepository;
    private final RiskExclusionRepository exclusionRepository;
    private final DangerFunctionRepository dangerFunctionRepository;
    private final ComponentInfoRepository componentInfoRepository;
    private final TestChecklistRepository testChecklistRepository;
    private final ObjectMapper objectMapper;

    /**
     * 执行风险排除分析
     */
    @Transactional
    public void performRiskExclusion(Long artifactId) {
        log.info("开始执行风险排除分析，制品ID: {}", artifactId);

        // 清除旧的风险排除记录
        exclusionRepository.deleteByArtifactId(artifactId);

        // 获取危险函数列表
        List<DangerFunction> dangerFunctions = dangerFunctionRepository.findByArtifactId(artifactId);

        // 获取组件列表
        List<ComponentInfo> components = componentInfoRepository.findByArtifactId(artifactId);

        // 获取所有启用的排除规则，按优先级排序
        List<RiskExclusionRule> rules = ruleRepository.findAllEnabledOrderByPriority();

        // 对每个危险函数应用排除规则
        List<RiskExclusion> exclusions = new ArrayList<>();

        for (DangerFunction df : dangerFunctions) {
            RiskExclusionResult result = applyExclusionRules(df, components, rules);
            if (result != null && result.excluded) {
                RiskExclusion exclusion = createExclusionRecord(artifactId, df, result);
                exclusions.add(exclusion);

                // 更新测试清单状态
                updateTestChecklistStatus(artifactId, df, result);
            }
        }

        // 保存风险排除记录
        exclusionRepository.saveAll(exclusions);

        log.info("风险排除分析完成，共排除 {} 个风险", exclusions.size());
    }

    /**
     * 应用排除规则
     */
    private RiskExclusionResult applyExclusionRules(DangerFunction df, List<ComponentInfo> components, List<RiskExclusionRule> rules) {
        for (RiskExclusionRule rule : rules) {
            if (matchesRule(df, components, rule)) {
                RiskExclusionResult result = new RiskExclusionResult();
                result.excluded = true;
                result.rule = rule;
                result.reason = rule.getExclusionReason();
                result.confidenceLevel = rule.getConfidenceLevel();
                result.requiresManualReview = rule.getRequiresManualReview();
                return result;
            }
        }
        return null;
    }

    /**
     * 检查危险函数是否匹配排除规则
     */
    private boolean matchesRule(DangerFunction df, List<ComponentInfo> components, RiskExclusionRule rule) {
        // 检查组件匹配
        if (rule.getApplicableComponents() != null && !isEmptyJson(rule.getApplicableComponents())) {
            if (!matchesComponents(components, rule.getApplicableComponents())) {
                return false;
            }
        }

        // 检查函数类型匹配
        if (rule.getApplicableFunctionTypes() != null && !isEmptyJson(rule.getApplicableFunctionTypes())) {
            if (!matchesFunctionType(df.getFunctionType(), rule.getApplicableFunctionTypes())) {
                return false;
            }
        }

        // 检查排除条件
        if (rule.getExclusionCondition() != null && !rule.getExclusionCondition().isEmpty()) {
            return evaluateCondition(df, rule.getExclusionCondition());
        }

        // 如果没有特定条件，检查函数类型是否匹配
        return rule.getApplicableFunctionTypes() != null || rule.getApplicableComponents() != null;
    }

    /**
     * 检查组件是否匹配
     */
    private boolean matchesComponents(List<ComponentInfo> components, String applicableComponents) {
        try {
            List<String> patterns = objectMapper.readValue(applicableComponents, new TypeReference<List<String>>() {});
            for (ComponentInfo component : components) {
                for (String pattern : patterns) {
                    if (component.getComponentName().toLowerCase().contains(pattern.toLowerCase())) {
                        return true;
                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("解析组件列表失败: {}", applicableComponents);
        }
        return false;
    }

    /**
     * 检查函数类型是否匹配
     */
    private boolean matchesFunctionType(String functionType, String applicableFunctionTypes) {
        try {
            List<String> types = objectMapper.readValue(applicableFunctionTypes, new TypeReference<List<String>>() {});
            return types.stream().anyMatch(t -> t.equalsIgnoreCase(functionType));
        } catch (JsonProcessingException e) {
            log.warn("解析函数类型列表失败: {}", applicableFunctionTypes);
        }
        return false;
    }

    /**
     * 评估排除条件
     */
    private boolean evaluateCondition(DangerFunction df, String condition) {
        // 解析条件表达式
        // 支持格式: field LIKE "pattern" OR field LIKE "pattern"
        condition = condition.trim();

        // 分割OR条件
        String[] orConditions = condition.split(" OR ");
        for (String orCond : orConditions) {
            if (evaluateSingleCondition(df, orCond.trim())) {
                return true;
            }
        }

        // 分割AND条件
        String[] andConditions = condition.split(" AND ");
        boolean allMatch = true;
        for (String andCond : andConditions) {
            if (!evaluateSingleCondition(df, andCond.trim())) {
                allMatch = false;
                break;
            }
        }

        return allMatch;
    }

    /**
     * 评估单个条件
     */
    private boolean evaluateSingleCondition(DangerFunction df, String condition) {
        // 解析 field LIKE "pattern" 或 field NOT LIKE "pattern" 或 field IN [...]
        Pattern likePattern = Pattern.compile("(\\w+)\\s+(NOT\\s+)?LIKE\\s+\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Pattern inPattern = Pattern.compile("(\\w+)\\s+IN\\s+\\[([^\\]]+)\\]", Pattern.CASE_INSENSITIVE);

        Matcher likeMatcher = likePattern.matcher(condition);
        if (likeMatcher.find()) {
            String field = likeMatcher.group(1);
            boolean notLike = likeMatcher.group(2) != null;
            String pattern = likeMatcher.group(3);

            String fieldValue = getFieldValue(df, field);
            if (fieldValue == null) return false;

            boolean matches = fieldValue.toLowerCase().contains(pattern.toLowerCase().replace("%", ""));
            return notLike ? !matches : matches;
        }

        Matcher inMatcher = inPattern.matcher(condition);
        if (inMatcher.find()) {
            String field = inMatcher.group(1);
            String[] values = inMatcher.group(2).split(",");

            String fieldValue = getFieldValue(df, field);
            if (fieldValue == null) return false;

            for (String value : values) {
                value = value.trim().replace("\"", "");
                if (fieldValue.toLowerCase().contains(value.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    /**
     * 获取字段值
     */
    private String getFieldValue(DangerFunction df, String field) {
        switch (field.toLowerCase()) {
            case "function_name":
                return df.getFunctionName();
            case "function_type":
                return df.getFunctionType();
            case "class_name":
                return df.getClassName();
            case "file_path":
                return df.getFilePath();
            case "method_name":
                return df.getMethodName();
            default:
                return null;
        }
    }

    /**
     * 创建风险排除记录
     */
    private RiskExclusion createExclusionRecord(Long artifactId, DangerFunction df, RiskExclusionResult result) {
        RiskExclusion exclusion = new RiskExclusion();
        exclusion.setArtifactId(artifactId);
        exclusion.setDangerFunctionId(df.getId());
        exclusion.setRuleId(result.rule.getId());
        exclusion.setFunctionName(df.getFunctionName());
        exclusion.setFunctionType(df.getFunctionType());
        exclusion.setClassName(df.getClassName());
        exclusion.setFilePath(df.getFilePath());
        exclusion.setLineNumber(df.getLineNumber());
        exclusion.setOriginalRiskLevel(df.getRiskLevel());
        exclusion.setExclusionReason(result.reason);
        exclusion.setConfidenceLevel(result.confidenceLevel);
        exclusion.setStatus(result.requiresManualReview ? "PENDING_REVIEW" : "APPROVED");
        return exclusion;
    }

    /**
     * 更新测试清单状态
     */
    private void updateTestChecklistStatus(Long artifactId, DangerFunction df, RiskExclusionResult result) {
        // 查找匹配的测试清单项
        List<TestChecklist> checklists = testChecklistRepository.findByArtifactIdAndVulnerabilityType(
                artifactId, mapFunctionTypeToVulnerability(df.getFunctionType()));

        for (TestChecklist checklist : checklists) {
            if (df.getFunctionName().equals(checklist.getMatchedDangerFunction())) {
                checklist.setStatus(result.requiresManualReview ? "PENDING_REVIEW" : "PASS");
                checklist.setExclusionReason(result.reason);
                checklist.setExclusionRuleId(result.rule.getId());
                testChecklistRepository.save(checklist);
            }
        }
    }

    /**
     * 将函数类型映射到漏洞类型
     */
    private String mapFunctionTypeToVulnerability(String functionType) {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("COMMAND_EXECUTION", "COMMAND_INJECTION");
        mapping.put("PROCESS_BUILDER", "COMMAND_INJECTION");
        mapping.put("DESERIALIZATION", "DESERIALIZATION");
        mapping.put("FILE_IO", "FILE_UPLOAD");
        mapping.put("FILE_WRITE", "FILE_UPLOAD");
        mapping.put("SQL_EXECUTION", "SQL_INJECTION");
        mapping.put("DYNAMIC_SQL", "SQL_INJECTION");
        mapping.put("HTTP_REQUEST", "SSRF");
        mapping.put("LOGGING", "INFO_DISCLOSURE");
        mapping.put("XML_PARSER", "XXE");

        return mapping.getOrDefault(functionType, functionType);
    }

    /**
     * 获取风险排除列表
     */
    public List<RiskExclusionResponse> getExclusions(Long artifactId) {
        List<RiskExclusion> exclusions = exclusionRepository.findByArtifactIdOrderByCreatedAtDesc(artifactId);
        return exclusions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取待审核的风险排除
     */
    public List<RiskExclusionResponse> getPendingReviewExclusions(Long artifactId) {
        List<RiskExclusion> exclusions = exclusionRepository.findByArtifactIdAndStatus(artifactId, "PENDING_REVIEW");
        return exclusions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 审核风险排除
     */
    @Transactional
    public RiskExclusionResponse reviewExclusion(Long exclusionId, String status, String reviewer, String notes) {
        exclusionRepository.updateReviewStatus(exclusionId, status, reviewer, notes);

        RiskExclusion exclusion = exclusionRepository.findById(exclusionId)
                .orElseThrow(() -> new RuntimeException("风险排除记录不存在: " + exclusionId));

        // 同步更新测试清单状态
        if (exclusion.getDangerFunctionId() != null) {
            DangerFunction df = dangerFunctionRepository.findById(exclusion.getDangerFunctionId()).orElse(null);
            if (df != null) {
                List<TestChecklist> checklists = testChecklistRepository.findByArtifactIdAndVulnerabilityType(
                        exclusion.getArtifactId(), mapFunctionTypeToVulnerability(df.getFunctionType()));
                for (TestChecklist checklist : checklists) {
                    if (df.getFunctionName().equals(checklist.getMatchedDangerFunction())) {
                        checklist.setStatus("APPROVED".equals(status) ? "PASS" : "TODO");
                        checklist.setExclusionReason(notes);
                        testChecklistRepository.save(checklist);
                    }
                }
            }
        }

        return toResponse(exclusion);
    }

    /**
     * 获取风险排除统计
     */
    public Map<String, Object> getExclusionStatistics(Long artifactId) {
        Map<String, Object> stats = new HashMap<>();

        List<Object[]> statusCounts = exclusionRepository.countGroupByStatus(artifactId);
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] row : statusCounts) {
            statusMap.put((String) row[0], (Long) row[1]);
        }

        stats.put("totalExclusions", exclusionRepository.findByArtifactId(artifactId).size());
        stats.put("approvedCount", statusMap.getOrDefault("APPROVED", 0L));
        stats.put("pendingReviewCount", statusMap.getOrDefault("PENDING_REVIEW", 0L));
        stats.put("rejectedCount", statusMap.getOrDefault("REJECTED", 0L));
        stats.put("statusBreakdown", statusMap);

        return stats;
    }

    /**
     * 转换为响应DTO
     */
    private RiskExclusionResponse toResponse(RiskExclusion exclusion) {
        return RiskExclusionResponse.builder()
                .id(exclusion.getId())
                .artifactId(exclusion.getArtifactId())
                .dangerFunctionId(exclusion.getDangerFunctionId())
                .ruleId(exclusion.getRuleId())
                .functionName(exclusion.getFunctionName())
                .functionType(exclusion.getFunctionType())
                .className(exclusion.getClassName())
                .filePath(exclusion.getFilePath())
                .lineNumber(exclusion.getLineNumber())
                .originalRiskLevel(exclusion.getOriginalRiskLevel())
                .exclusionReason(exclusion.getExclusionReason())
                .confidenceLevel(exclusion.getConfidenceLevel())
                .status(exclusion.getStatus())
                .reviewer(exclusion.getReviewer())
                .reviewNotes(exclusion.getReviewNotes())
                .reviewedAt(exclusion.getReviewedAt())
                .createdAt(exclusion.getCreatedAt())
                .build();
    }

    private boolean isEmptyJson(String json) {
        return json == null || json.trim().isEmpty() || "[]".equals(json.trim()) || "null".equals(json.trim());
    }

    /**
     * 内部类：风险排除结果
     */
    private static class RiskExclusionResult {
        boolean excluded;
        RiskExclusionRule rule;
        String reason;
        String confidenceLevel;
        boolean requiresManualReview;
    }
}