package org.example.securityplatform.repository;

import org.example.securityplatform.entity.RiskExclusion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 风险排除记录数据访问接口
 */
@Repository
public interface RiskExclusionRepository extends JpaRepository<RiskExclusion, Long> {

    List<RiskExclusion> findByArtifactId(Long artifactId);

    List<RiskExclusion> findByArtifactIdAndStatus(Long artifactId, String status);

    List<RiskExclusion> findByDangerFunctionId(Long dangerFunctionId);

    @Query("SELECT re FROM RiskExclusion re WHERE re.artifactId = :artifactId ORDER BY re.createdAt DESC")
    List<RiskExclusion> findByArtifactIdOrderByCreatedAtDesc(@Param("artifactId") Long artifactId);

    @Query("SELECT COUNT(re) FROM RiskExclusion re WHERE re.artifactId = :artifactId AND re.status = :status")
    long countByArtifactIdAndStatus(@Param("artifactId") Long artifactId, @Param("status") String status);

    @Query("SELECT re.status, COUNT(re) FROM RiskExclusion re WHERE re.artifactId = :artifactId GROUP BY re.status")
    List<Object[]> countGroupByStatus(@Param("artifactId") Long artifactId);

    @Modifying
    @Query("DELETE FROM RiskExclusion re WHERE re.artifactId = :artifactId")
    void deleteByArtifactId(@Param("artifactId") Long artifactId);

    @Modifying
    @Query("UPDATE RiskExclusion re SET re.status = :status, re.reviewer = :reviewer, re.reviewNotes = :notes, re.reviewedAt = CURRENT_TIMESTAMP WHERE re.id = :id")
    void updateReviewStatus(@Param("id") Long id, @Param("status") String status,
                            @Param("reviewer") String reviewer, @Param("notes") String notes);
}