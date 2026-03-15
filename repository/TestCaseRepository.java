package org.example.securityplatform.repository;

import org.example.securityplatform.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 测试用例数据访问接口
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    List<TestCase> findByEnabledTrue();

    List<TestCase> findByVulnerabilityType(String vulnerabilityType);

    List<TestCase> findByVulnerabilityTypeAndEnabledTrue(String vulnerabilityType);

    List<TestCase> findByRiskLevel(String riskLevel);

    @Query("SELECT tc FROM TestCase tc WHERE tc.enabled = true AND " +
           "(tc.applicableComponents IS NULL OR tc.applicableComponents LIKE %:component%)")
    List<TestCase> findByApplicableComponent(@Param("component") String component);

    @Query("SELECT tc FROM TestCase tc WHERE tc.enabled = true AND " +
           "(tc.applicableDangerFunctions IS NULL OR tc.applicableDangerFunctions LIKE %:functionType%)")
    List<TestCase> findByApplicableDangerFunction(@Param("functionType") String functionType);

    @Query("SELECT DISTINCT tc.vulnerabilityType FROM TestCase tc WHERE tc.enabled = true")
    List<String> findAllVulnerabilityTypes();
}