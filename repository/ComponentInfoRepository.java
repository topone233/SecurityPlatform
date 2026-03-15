package org.example.securityplatform.repository;

import org.example.securityplatform.entity.ComponentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 组件信息数据访问接口
 */
@Repository
public interface ComponentInfoRepository extends JpaRepository<ComponentInfo, Long> {
    List<ComponentInfo> findByArtifactId(Long artifactId);
    List<ComponentInfo> findByArtifactIdAndComponentType(Long artifactId, String componentType);
    List<ComponentInfo> findByComponentNameContaining(String componentName);
}