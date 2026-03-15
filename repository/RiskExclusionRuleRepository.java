package org.example.securityplatform.repository;

import org.example.securityplatform.entity.RiskExclusionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 风险排除规则数据访问接口
 */
@Repository
public interface RiskExclusionRuleRepository extends JpaRepository<RiskExclusionRule, Long> {

    List<RiskExclusionRule> findByEnabledTrue();

    List<RiskExclusionRule> findByRuleType(String ruleType);

    List<RiskExclusionRule> findByEnabledTrueOrderByPriorityDesc();

    @Query("SELECT rer FROM RiskExclusionRule rer WHERE rer.enabled = true AND " +
           "(rer.applicableComponents IS NULL OR rer.applicableComponents LIKE %:component%)")
    List<RiskExclusionRule> findByApplicableComponent(@Param("component") String component);

    @Query("SELECT rer FROM RiskExclusionRule rer WHERE rer.enabled = true AND " +
           "(rer.applicableFunctionTypes IS NULL OR rer.applicableFunctionTypes LIKE %:functionType%)")
    List<RiskExclusionRule> findByApplicableFunctionType(@Param("functionType") String functionType);

    @Query("SELECT rer FROM RiskExclusionRule rer WHERE rer.enabled = true ORDER BY rer.priority DESC")
    List<RiskExclusionRule> findAllEnabledOrderByPriority();
}