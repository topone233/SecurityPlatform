package org.example.securityplatform.repository;

import org.example.securityplatform.entity.ExternalUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 外部URL数据访问接口
 */
@Repository
public interface ExternalUrlRepository extends JpaRepository<ExternalUrl, Long> {
    List<ExternalUrl> findByArtifactId(Long artifactId);
    List<ExternalUrl> findByArtifactIdAndProtocol(Long artifactId, String protocol);
    List<ExternalUrl> findByUrlContaining(String url);
}