package org.example.securityplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.entity.AnalysisProgress;
import org.example.securityplatform.repository.AnalysisProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 分析进度服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisProgressService {

    private final AnalysisProgressRepository progressRepository;

    /**
     * 分析步骤枚举
     */
    public enum AnalysisStep {
        COMPONENT("组件分析", 1),
        CONFIG("配置分析", 2),
        URL("URL 分析", 3),
        API("API 分析", 4),
        DANGER("危险函数分析", 5);

        private final String name;
        private final int order;

        AnalysisStep(String name, int order) {
            this.name = name;
            this.order = order;
        }

        public String getName() {
            return name;
        }

        public int getOrder() {
            return order;
        }
    }

    /**
     * 开始分析
     */
    @Transactional
    public void startAnalysis(Long artifactId) {
        // 删除旧的进度记录
        progressRepository.deleteByArtifactId(artifactId);

        AnalysisProgress progress = new AnalysisProgress();
        progress.setArtifactId(artifactId);
        progress.setStatus("RUNNING");
        progress.setCurrentStep("准备中...");
        progress.setTotalSteps(5);
        progress.setCompletedSteps(0);
        progress.setProgressPercentage(0);
        progress.setStartedAt(LocalDateTime.now());

        progressRepository.save(progress);
        log.info("分析开始：artifactId={}", artifactId);
    }

    /**
     * 更新分析进度
     */
    @Transactional
    public void updateProgress(Long artifactId, AnalysisStep step, String message) {
        Optional<AnalysisProgress> progressOpt = progressRepository.findByArtifactId(artifactId);
        if (progressOpt.isPresent()) {
            AnalysisProgress progress = progressOpt.get();
            progress.setCurrentStep(step.getName());
            progress.setCompletedSteps(step.getOrder());
            progress.setProgressPercentage((step.getOrder() * 100) / 5);
            progress.setMessage(message);
            progress.setUpdatedAt(LocalDateTime.now());
            progressRepository.save(progress);
            log.debug("分析进度更新：artifactId={}, step={}, progress={}%", artifactId, step.getName(), progress.getProgressPercentage());
        }
    }

    /**
     * 完成分析
     */
    @Transactional
    public void completeAnalysis(Long artifactId, String status) {
        Optional<AnalysisProgress> progressOpt = progressRepository.findByArtifactId(artifactId);
        if (progressOpt.isPresent()) {
            AnalysisProgress progress = progressOpt.get();
            progress.setStatus(status);
            progress.setProgressPercentage(100);
            progress.setCompletedSteps(5);
            progress.setMessage("分析完成");
            progress.setCompletedAt(LocalDateTime.now());
            progressRepository.save(progress);
            log.info("分析完成：artifactId={}, status={}", artifactId, status);
        }
    }

    /**
     * 获取分析进度
     */
    public AnalysisProgress getProgress(Long artifactId) {
        return progressRepository.findByArtifactId(artifactId)
                .orElseThrow(() -> new RuntimeException("未找到分析进度记录"));
    }

    /**
     * 检查是否正在分析
     */
    public boolean isAnalyzing(Long artifactId) {
        Optional<AnalysisProgress> progressOpt = progressRepository.findByArtifactId(artifactId);
        if (progressOpt.isPresent()) {
            String status = progressOpt.get().getStatus();
            return "RUNNING".equals(status) || "PENDING".equals(status);
        }
        return false;
    }
}