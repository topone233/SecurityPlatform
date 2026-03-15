package org.example.securityplatform.repository;

import org.example.securityplatform.entity.TestReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 测试报告数据访问接口
 */
@Repository
public interface TestReportRepository extends JpaRepository<TestReport, Long> {

    List<TestReport> findByArtifactId(Long artifactId);

    List<TestReport> findByReportStatus(String status);

    List<TestReport> findByReportType(String type);

    @Query("SELECT tr FROM TestReport tr WHERE tr.artifactId = :artifactId ORDER BY tr.createdAt DESC")
    List<TestReport> findByArtifactIdOrderByCreatedAtDesc(@Param("artifactId") Long artifactId);

    @Query("SELECT tr FROM TestReport tr WHERE tr.artifactId = :artifactId AND tr.reportStatus = 'COMPLETED' ORDER BY tr.createdAt DESC LIMIT 1")
    Optional<TestReport> findLatestCompletedReport(@Param("artifactId") Long artifactId);

    @Query("SELECT COUNT(tr) FROM TestReport tr WHERE tr.reportStatus = :status")
    long countByReportStatus(@Param("status") String status);

    @Modifying
    @Query("UPDATE TestReport tr SET tr.reportStatus = :status, tr.generatedAt = CURRENT_TIMESTAMP WHERE tr.id = :id")
    void updateReportStatus(@Param("id") Long id, @Param("status") String status);

    @Modifying
    @Query("DELETE FROM TestReport tr WHERE tr.artifactId = :artifactId")
    void deleteByArtifactId(@Param("artifactId") Long artifactId);
}
