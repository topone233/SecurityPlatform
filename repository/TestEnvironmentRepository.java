package org.example.securityplatform.repository;

import org.example.securityplatform.entity.TestEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 测试环境数据访问接口
 */
@Repository
public interface TestEnvironmentRepository extends JpaRepository<TestEnvironment, Long> {

    List<TestEnvironment> findByIsActiveTrue();

    List<TestEnvironment> findByEnvType(String envType);

    List<TestEnvironment> findByEnvName(String envName);
}
