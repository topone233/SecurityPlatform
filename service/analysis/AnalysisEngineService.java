package org.example.securityplatform.service.analysis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.analyzer.api.ApiExtractorService;
import org.example.securityplatform.analyzer.component.ComponentIdentifierService;
import org.example.securityplatform.analyzer.config.ConfigCollectorService;
import org.example.securityplatform.analyzer.danger.DangerFunctionScannerService;
import org.example.securityplatform.analyzer.url.ExternalUrlDetectorService;
import org.example.securityplatform.common.ArtifactStatus;
import org.example.securityplatform.entity.Artifact;
import org.example.securityplatform.repository.ApiInfoRepository;
import org.example.securityplatform.repository.ArtifactRepository;
import org.example.securityplatform.repository.ComponentInfoRepository;
import org.example.securityplatform.repository.ConfigInfoRepository;
import org.example.securityplatform.repository.DangerFunctionRepository;
import org.example.securityplatform.repository.ExternalUrlRepository;
import org.example.securityplatform.service.AnalysisCacheService;
import org.example.securityplatform.service.AnalysisProgressService;
import org.example.securityplatform.service.AnalysisProgressService.AnalysisStep;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 分析引擎总控服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisEngineService {

    private final ArtifactRepository artifactRepository;
    private final ComponentIdentifierService componentIdentifierService;
    private final ConfigCollectorService configCollectorService;
    private final ExternalUrlDetectorService externalUrlDetectorService;
    private final ApiExtractorService apiExtractorService;
    private final DangerFunctionScannerService dangerFunctionScannerService;

    private final ApiInfoRepository apiInfoRepository;
    private final ComponentInfoRepository componentInfoRepository;
    private final ConfigInfoRepository configInfoRepository;
    private final DangerFunctionRepository dangerFunctionRepository;
    private final ExternalUrlRepository externalUrlRepository;

    private final AnalysisProgressService progressService;
    private final AnalysisCacheService cacheService;

    /**
     * 异步分析制品（带进度追踪和缓存）
     *
     * @param artifactId 制品 ID
     * @return CompletableFuture
     */
    @Async
    @Transactional
    public CompletableFuture<Void> analyzeArtifactAsync(Long artifactId) {
        log.info("开始异步分析制品：artifactId={}", artifactId);
        long startTime = System.currentTimeMillis();

        try {
            // 获取制品信息
            Artifact artifact = artifactRepository.findById(artifactId)
                    .orElseThrow(() -> new RuntimeException("制品不存在：" + artifactId));

            String md5 = artifact.getMd5();
            String jarPath = artifact.getFilePath();

            // 检查缓存
            if (cacheService.isValidCache(md5)) {
                log.info("命中缓存，跳过分析：artifactId={}, md5={}", artifactId, md5);
                updateArtifactStatus(artifactId, ArtifactStatus.ANALYZED);
                return CompletableFuture.completedFuture(null);
            }

            // 检查是否正在分析
            if (progressService.isAnalyzing(artifactId)) {
                log.warn("制品正在分析中：artifactId={}", artifactId);
                throw new RuntimeException("制品正在分析中，请稍后重试");
            }

            // 更新状态为分析中
            updateArtifactStatus(artifactId, ArtifactStatus.ANALYZING);

            // 开始进度追踪
            progressService.startAnalysis(artifactId);

            // 执行各项分析
            analyzeComponents(artifactId, jarPath);
            analyzeConfigs(artifactId, jarPath);
            analyzeUrls(artifactId, jarPath);
            analyzeApis(artifactId, jarPath);
            analyzeDangers(artifactId, jarPath);

            // 更新状态为已分析
            updateArtifactStatus(artifactId, ArtifactStatus.ANALYZED);

            // 完成进度追踪
            progressService.completeAnalysis(artifactId, "COMPLETED");

            // 获取统计信息并保存到缓存
            Map<String, Integer> stats = getAnalysisStats(artifactId);
            cacheService.saveCache(md5, stats);

            long endTime = System.currentTimeMillis();
            log.info("制品分析完成：artifactId={}, 耗时={}ms, 统计={}", artifactId, endTime - startTime, stats);

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("制品分析失败：artifactId={}", artifactId, e);
            updateArtifactStatus(artifactId, ArtifactStatus.FAILED);
            progressService.completeAnalysis(artifactId, "FAILED");
            throw new RuntimeException("制品分析失败", e);
        }
    }

    /**
     * 同步分析制品
     *
     * @param artifactId 制品 ID
     */
    @Transactional
    public void analyzeArtifact(Long artifactId) {
        try {
            analyzeArtifactAsync(artifactId).get();
        } catch (Exception e) {
            throw new RuntimeException("制品分析失败", e);
        }
    }

    /**
     * 获取分析统计信息
     */
    public Map<String, Integer> getAnalysisStats(Long artifactId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("apiCount", apiInfoRepository.findByArtifactId(artifactId).size());
        stats.put("componentCount", componentInfoRepository.findByArtifactId(artifactId).size());
        stats.put("configCount", configInfoRepository.findByArtifactId(artifactId).size());
        stats.put("dangerCount", dangerFunctionRepository.findByArtifactId(artifactId).size());
        stats.put("urlCount", externalUrlRepository.findByArtifactId(artifactId).size());
        return stats;
    }

    /**
     * 获取分析进度
     */
    public org.example.securityplatform.entity.AnalysisProgress getAnalysisProgress(Long artifactId) {
        return progressService.getProgress(artifactId);
    }

    /**
     * 更新制品状态
     */
    private void updateArtifactStatus(Long artifactId, String status) {
        artifactRepository.findById(artifactId).ifPresent(artifact -> {
            artifact.setStatus(status);
            artifactRepository.save(artifact);
        });
    }

    /**
     * 分析组件
     */
    private void analyzeComponents(Long artifactId, String jarPath) {
        try {
            log.info("开始组件分析：artifactId={}", artifactId);
            progressService.updateProgress(artifactId, AnalysisStep.COMPONENT, "正在扫描依赖...");
            componentIdentifierService.analyzeArtifact(artifactId, jarPath);
        } catch (Exception e) {
            log.error("组件分析失败：artifactId={}", artifactId, e);
        }
    }

    /**
     * 分析配置
     */
    private void analyzeConfigs(Long artifactId, String jarPath) {
        try {
            log.info("开始配置分析：artifactId={}", artifactId);
            progressService.updateProgress(artifactId, AnalysisStep.CONFIG, "正在解析配置文件...");
            configCollectorService.analyzeArtifact(artifactId, jarPath);
        } catch (Exception e) {
            log.error("配置分析失败：artifactId={}", artifactId, e);
        }
    }

    /**
     * 分析外部 URL
     */
    private void analyzeUrls(Long artifactId, String jarPath) {
        try {
            log.info("开始 URL 分析：artifactId={}", artifactId);
            progressService.updateProgress(artifactId, AnalysisStep.URL, "正在检测外部 URL...");
            externalUrlDetectorService.analyzeArtifact(artifactId, jarPath);
        } catch (Exception e) {
            log.error("URL 分析失败：artifactId={}", artifactId, e);
        }
    }

    /**
     * 分析 API
     */
    private void analyzeApis(Long artifactId, String jarPath) {
        try {
            log.info("开始 API 分析：artifactId={}", artifactId);
            progressService.updateProgress(artifactId, AnalysisStep.API, "正在提取 API 信息...");
            apiExtractorService.analyzeArtifact(artifactId, jarPath);
        } catch (Exception e) {
            log.error("API 分析失败：artifactId={}", artifactId, e);
        }
    }

    /**
     * 分析危险函数
     */
    private void analyzeDangers(Long artifactId, String jarPath) {
        try {
            log.info("开始危险函数分析：artifactId={}", artifactId);
            progressService.updateProgress(artifactId, AnalysisStep.DANGER, "正在扫描危险函数...");
            dangerFunctionScannerService.analyzeArtifact(artifactId, jarPath);
        } catch (Exception e) {
            log.error("危险函数分析失败：artifactId={}", artifactId, e);
        }
    }
}