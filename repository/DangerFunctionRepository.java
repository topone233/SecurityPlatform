package org.example.securityplatform.repository;

import org.example.securityplatform.entity.DangerFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 危险函数数据访问接口
 */
@Repository
public interface DangerFunctionRepository extends JpaRepository<DangerFunction, Long> {
    List<DangerFunction> findByArtifactId(Long artifactId);
    List<DangerFunction> findByArtifactIdAndRiskLevel(Long artifactId, String riskLevel);
    List<DangerFunction> findByFunctionNameContaining(String functionName);
    List<DangerFunction> findByRiskLevel(String riskLevel);
}