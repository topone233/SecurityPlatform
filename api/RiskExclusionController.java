package org.example.securityplatform.api;

import lombok.RequiredArgsConstructor;
import org.example.securityplatform.common.Result;
import org.example.securityplatform.dto.RiskExclusionResponse;
import org.example.securityplatform.service.RiskExclusionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 风险排除控制器
 * 提供风险自动排除相关的API
 */
@RestController
@RequestMapping("/api/risk-exclusion")
@RequiredArgsConstructor
public class RiskExclusionController {

    private final RiskExclusionService riskExclusionService;

    /**
     * 执行风险排除分析
     */
    @PostMapping("/analyze/{artifactId}")
    public Result<Void> performRiskExclusion(@PathVariable Long artifactId) {
        riskExclusionService.performRiskExclusion(artifactId);
        return Result.success(null);
    }

    /**
     * 获取风险排除列表
     */
    @GetMapping("/list/{artifactId}")
    public Result<List<RiskExclusionResponse>> getExclusions(@PathVariable Long artifactId) {
        List<RiskExclusionResponse> exclusions = riskExclusionService.getExclusions(artifactId);
        return Result.success(exclusions);
    }

    /**
     * 获取待审核的风险排除
     */
    @GetMapping("/pending-review/{artifactId}")
    public Result<List<RiskExclusionResponse>> getPendingReviewExclusions(@PathVariable Long artifactId) {
        List<RiskExclusionResponse> exclusions = riskExclusionService.getPendingReviewExclusions(artifactId);
        return Result.success(exclusions);
    }

    /**
     * 审核风险排除
     */
    @PutMapping("/review/{exclusionId}")
    public Result<RiskExclusionResponse> reviewExclusion(
            @PathVariable Long exclusionId,
            @RequestParam String status,
            @RequestParam(required = false) String reviewer,
            @RequestParam(required = false) String notes) {
        RiskExclusionResponse response = riskExclusionService.reviewExclusion(exclusionId, status, reviewer, notes);
        return Result.success(response);
    }

    /**
     * 获取风险排除统计
     */
    @GetMapping("/statistics/{artifactId}")
    public Result<Map<String, Object>> getExclusionStatistics(@PathVariable Long artifactId) {
        Map<String, Object> stats = riskExclusionService.getExclusionStatistics(artifactId);
        return Result.success(stats);
    }
}