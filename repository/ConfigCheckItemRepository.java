package org.example.securityplatform.repository;

import org.example.securityplatform.entity.ConfigCheckItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 配置检查清单数据访问接口
 */
@Repository
public interface ConfigCheckItemRepository extends JpaRepository<ConfigCheckItem, Long> {

    List<ConfigCheckItem> findByEnabledTrue();

    List<ConfigCheckItem> findByCategory(String category);

    List<ConfigCheckItem> findByCategoryAndEnabledTrue(String category);

    List<ConfigCheckItem> findByRiskLevel(String riskLevel);

    @Query("SELECT cci FROM ConfigCheckItem cci WHERE cci.enabled = true AND " +
           "cci.configKeyPattern LIKE %:keyPattern%")
    List<ConfigCheckItem> findByConfigKeyPatternContaining(@Param("keyPattern") String keyPattern);

    @Query("SELECT DISTINCT cci.category FROM ConfigCheckItem cci WHERE cci.enabled = true")
    List<String> findAllCategories();
}