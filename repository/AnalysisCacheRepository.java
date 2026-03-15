package org.example.securityplatform.repository;

import org.example.securityplatform.entity.AnalysisCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 分析缓存数据访问接口
 */
@Repository
public interface AnalysisCacheRepository extends JpaRepository<AnalysisCache, Long> {
    Optional<AnalysisCache> findByArtifactMd5(String md5);

    @Query("SELECT ac FROM AnalysisCache ac WHERE ac.expiresAt < :now")
    List<AnalysisCache> findExpired(LocalDateTime now);

    void deleteByArtifactMd5(String md5);
}