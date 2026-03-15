package org.example.securityplatform.repository;

import org.example.securityplatform.entity.ConfigInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 配置信息数据访问接口
 */
@Repository
public interface ConfigInfoRepository extends JpaRepository<ConfigInfo, Long> {
    List<ConfigInfo> findByArtifactId(Long artifactId);
    List<ConfigInfo> findByArtifactIdAndCategory(Long artifactId, String category);
    List<ConfigInfo> findByConfigKeyContaining(String configKey);
}