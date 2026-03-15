package org.example.securityplatform.repository;

import org.example.securityplatform.entity.TestExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试执行记录数据访问接口
 */
@Repository
public interface TestExecutionRepository extends JpaRepository<TestExecution, Long> {

    List<TestExecution> findByArtifactId(Long artifactId);

    List<TestExecution> findByArtifactIdAndExecutionStatus(Long artifactId, String status);

    List<TestExecution> findByChecklistId(Long checklistId);

    List<TestExecution> findByTestCaseId(Long testCaseId);

    List<TestExecution> findByTestResult(String testResult);

    List<TestExecution> findByVulnerabilityType(String vulnerabilityType);

    @Query("SELECT te FROM TestExecution te WHERE te.artifactId = :artifactId ORDER BY te.createdAt DESC")
    List<TestExecution> findByArtifactIdOrderByCreatedAtDesc(@Param("artifactId") Long artifactId);

    @Query("SELECT te FROM TestExecution te WHERE te.artifactId = :artifactId AND te.executionStatus = 'RUNNING'")
    List<TestExecution> findRunningExecutions(@Param("artifactId") Long artifactId);

    @Query("SELECT COUNT(te) FROM TestExecution te WHERE te.artifactId = :artifactId AND te.testResult = :result")
    long countByArtifactIdAndTestResult(@Param("artifactId") Long artifactId, @Param("result") String result);

    @Query("SELECT te.testResult, COUNT(te) FROM TestExecution te WHERE te.artifactId = :artifactId GROUP BY te.testResult")
    List<Object[]> countGroupByTestResult(@Param("artifactId") Long artifactId);

    @Query("SELECT te.vulnerabilityType, COUNT(te) FROM TestExecution te WHERE te.artifactId = :artifactId GROUP BY te.vulnerabilityType")
    List<Object[]> countGroupByVulnerabilityType(@Param("artifactId") Long artifactId);

    @Modifying
    @Query("UPDATE TestExecution te SET te.executionStatus = :status, te.endTime = :endTime, te.durationMs = :duration WHERE te.id = :id")
    void updateExecutionStatus(@Param("id") Long id, @Param("status") String status,
                               @Param("endTime") LocalDateTime endTime, @Param("duration") Long duration);

    @Modifying
    @Query("UPDATE TestExecution te SET te.testResult = :result, te.actualResult = :actualResult, te.notes = :notes WHERE te.id = :id")
    void updateTestResult(@Param("id") Long id, @Param("result") String result,
                          @Param("actualResult") String actualResult, @Param("notes") String notes);

    @Modifying
    @Query("DELETE FROM TestExecution te WHERE te.artifactId = :artifactId")
    void deleteByArtifactId(@Param("artifactId") Long artifactId);
}
