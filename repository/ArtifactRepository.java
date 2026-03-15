package org.example.securityplatform.repository;

import org.example.securityplatform.entity.Artifact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 制品数据访问接口
 */
@Repository
public interface ArtifactRepository extends JpaRepository<Artifact, Long> {
    List<Artifact> findByTaskId(Long taskId);
    List<Artifact> findByStatus(String status);
    Optional<Artifact> findByTaskIdAndArtifactName(Long taskId, String artifactName);
    Optional<Artifact> findByMd5(String md5);
}