package org.example.securityplatform.api;

import lombok.RequiredArgsConstructor;
import org.example.securityplatform.dto.AnalysisCacheResponse;
import org.example.securityplatform.dto.AnalysisProgressResponse;
import org.example.securityplatform.entity.AnalysisCache;
import org.example.securityplatform.entity.AnalysisProgress;
import org.example.securityplatform.service.AnalysisCacheService;
import org.example.securityplatform.service.AnalysisProgressService;
import org.example.securityplatform.service.analysis.AnalysisEngineService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 分析进度管理 API 控制器
 */
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisProgressController {

    private final AnalysisEngineService analysisEngineService;
    private final AnalysisProgressService progressService;
    private final AnalysisCacheService cacheService;

    /**
     * 查询分析进度
     */
    @GetMapping("/progress/{artifactId}")
    public AnalysisProgressResponse getAnalysisProgress(@PathVariable Long artifactId) {
        try {
            AnalysisProgress progress = analysisEngineService.getAnalysisProgress(artifactId);
            AnalysisProgressResponse response = new AnalysisProgressResponse();
            BeanUtils.copyProperties(progress, response);
            return response;
        } catch (RuntimeException e) {
            // 如果没有进度记录，返回默认值
            AnalysisProgressResponse response = new AnalysisProgressResponse();
            response.setArtifactId(artifactId);
            response.setStatus("PENDING");
            response.setProgressPercentage(0);
            return response;
        }
    }

    /**
     * 查询缓存状态
     */
    @GetMapping("/cache/{artifactId}")
    public Map<String, Object> getCacheStatus(@PathVariable Long artifactId) {
        Map<String, Object> result = new HashMap<>();
        result.put("artifactId", artifactId);

        try {
            // 获取制品 MD5
            var artifactOpt = analysisEngineService.getClass(); // 需要通过其他方式获取 MD5
            // 这里简化处理，直接返回缓存相关信息

            result.put("hasCache", false);
            result.put("message", "请通过 MD5 查询缓存");
            return result;
        } catch (Exception e) {
            result.put("hasCache", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    /**
     * 清除分析缓存
     */
    @DeleteMapping("/cache/{md5}")
    public void clearCache(@PathVariable String md5) {
        cacheService.deleteCache(md5);
    }

    /**
     * 手动触发缓存清理
     */
    @PostMapping("/cache/cleanup")
    public Map<String, Integer> cleanupCache() {
        Map<String, Integer> result = new HashMap<>();
        // 这里可以添加手动清理逻辑
        result.put("deleted", 0);
        return result;
    }
}