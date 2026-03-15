package org.example.securityplatform.repository;

import org.example.securityplatform.entity.AnalysisProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 分析进度数据访问接口
 */
@Repository
public interface AnalysisProgressRepository extends JpaRepository<AnalysisProgress, Long> {
    Optional<AnalysisProgress> findByArtifactId(Long artifactId);
    void deleteByArtifactId(Long artifactId);
}