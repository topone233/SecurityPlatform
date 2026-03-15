package org.example.securityplatform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.entity.AnalysisCache;
import org.example.securityplatform.repository.AnalysisCacheRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 分析缓存服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisCacheService {

    private final AnalysisCacheRepository cacheRepository;
    private final ObjectMapper objectMapper;

    /**
     * 检查缓存是否有效
     */
    public boolean isValidCache(String md5) {
        Optional<AnalysisCache> cacheOpt = cacheRepository.findByArtifactMd5(md5);
        if (cacheOpt.isPresent()) {
            AnalysisCache cache = cacheOpt.get();
            // 检查是否过期
            if (cache.getExpiresAt() != null && cache.getExpiresAt().isBefore(LocalDateTime.now())) {
                log.info("缓存已过期：md5={}", md5);
                cacheRepository.delete(cache);
                return false;
            }
            log.info("命中缓存：md5={}, apiCount={}, componentCount={}, dangerCount={}",
                    md5, cache.getApiCount(), cache.getComponentCount(), cache.getDangerCount());
            return true;
        }
        return false;
    }

    /**
     * 保存分析结果到缓存
     */
    @Transactional
    public void saveCache(String md5, Map<String, Integer> stats) {
        try {
            AnalysisCache cache = new AnalysisCache();
            cache.setArtifactMd5(md5);
            cache.setApiCount(stats.getOrDefault("apiCount", 0));
            cache.setComponentCount(stats.getOrDefault("componentCount", 0));
            cache.setDangerCount(stats.getOrDefault("dangerCount", 0));
            cache.setConfigCount(stats.getOrDefault("configCount", 0));
            cache.setUrlCount(stats.getOrDefault("urlCount", 0));

            // 序列化完整数据
            cache.setCacheData(objectMapper.writeValueAsString(stats));

            cacheRepository.save(cache);
            log.info("缓存已保存：md5={}, 统计={}", md5, stats);

        } catch (JsonProcessingException e) {
            log.error("缓存序列化失败：md5={}", md5, e);
        }
    }

    /**
     * 获取缓存的统计信息
     */
    public Map<String, Integer> getCachedStats(String md5) {
        Optional<AnalysisCache> cacheOpt = cacheRepository.findByArtifactMd5(md5);
        if (cacheOpt.isPresent()) {
            AnalysisCache cache = cacheOpt.get();
            Map<String, Integer> stats = new HashMap<>();
            stats.put("apiCount", cache.getApiCount());
            stats.put("componentCount", cache.getComponentCount());
            stats.put("dangerCount", cache.getDangerCount());
            stats.put("configCount", cache.getConfigCount());
            stats.put("urlCount", cache.getUrlCount());
            return stats;
        }
        return null;
    }

    /**
     * 获取缓存的详细信息
     */
    public Optional<AnalysisCache> getCache(String md5) {
        return cacheRepository.findByArtifactMd5(md5);
    }

    /**
     * 删除缓存
     */
    @Transactional
    public void deleteCache(String md5) {
        cacheRepository.deleteByArtifactMd5(md5);
        log.info("缓存已删除：md5={}", md5);
    }

    /**
     * 定时清理过期缓存
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨 2 点执行
    @Transactional
    public void cleanupExpiredCache() {
        List<AnalysisCache> expiredCaches = cacheRepository.findExpired(LocalDateTime.now());
        for (AnalysisCache cache : expiredCaches) {
            cacheRepository.delete(cache);
            log.info("清理过期缓存：md5={}", cache.getArtifactMd5());
        }
        log.info("过期缓存清理完成，共清理 {} 条记录", expiredCaches.size());
    }
}