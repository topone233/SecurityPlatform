package org.example.securityplatform.repository;

import org.example.securityplatform.entity.ApiInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * API信息数据访问接口
 */
@Repository
public interface ApiInfoRepository extends JpaRepository<ApiInfo, Long> {
    List<ApiInfo> findByArtifactId(Long artifactId);
    List<ApiInfo> findByArtifactIdAndFramework(Long artifactId, String framework);
    List<ApiInfo> findByUrlContaining(String url);
}