package org.example.securityplatform.repository;

import org.example.securityplatform.entity.TestChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 测试清单数据访问接口
 */
@Repository
public interface TestChecklistRepository extends JpaRepository<TestChecklist, Long> {

    List<TestChecklist> findByArtifactId(Long artifactId);

    List<TestChecklist> findByArtifactIdAndStatus(Long artifactId, String status);

    List<TestChecklist> findByArtifactIdAndVulnerabilityType(Long artifactId, String vulnerabilityType);

    @Query("SELECT tc FROM TestChecklist tc WHERE tc.artifactId = :artifactId ORDER BY tc.riskLevel DESC, tc.createdAt ASC")
    List<TestChecklist> findByArtifactIdOrderByRiskLevel(@Param("artifactId") Long artifactId);

    @Query("SELECT COUNT(tc) FROM TestChecklist tc WHERE tc.artifactId = :artifactId AND tc.status = :status")
    long countByArtifactIdAndStatus(@Param("artifactId") Long artifactId, @Param("status") String status);

    @Query("SELECT tc.vulnerabilityType, COUNT(tc) FROM TestChecklist tc WHERE tc.artifactId = :artifactId GROUP BY tc.vulnerabilityType")
    List<Object[]> countGroupByVulnerabilityType(@Param("artifactId") Long artifactId);

    @Query("SELECT tc.status, COUNT(tc) FROM TestChecklist tc WHERE tc.artifactId = :artifactId GROUP BY tc.status")
    List<Object[]> countGroupByStatus(@Param("artifactId") Long artifactId);

    @Modifying
    @Query("DELETE FROM TestChecklist tc WHERE tc.artifactId = :artifactId")
    void deleteByArtifactId(@Param("artifactId") Long artifactId);

    @Modifying
    @Query("UPDATE TestChecklist tc SET tc.status = :status, tc.exclusionReason = :reason, tc.exclusionRuleId = :ruleId WHERE tc.id = :id")
    void updateStatusAndExclusion(@Param("id") Long id, @Param("status") String status,
                                   @Param("reason") String reason, @Param("ruleId") Long ruleId);
}