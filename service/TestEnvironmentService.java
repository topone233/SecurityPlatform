package org.example.securityplatform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.dto.TestEnvironmentResponse;
import org.example.securityplatform.entity.TestEnvironment;
import org.example.securityplatform.repository.TestEnvironmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试环境服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestEnvironmentService {

    private final TestEnvironmentRepository environmentRepository;
    private final ObjectMapper objectMapper;

    /**
     * 获取所有活跃的测试环境
     */
    public List<TestEnvironmentResponse> getActiveEnvironments() {
        return environmentRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有测试环境
     */
    public List<TestEnvironmentResponse> getAllEnvironments() {
        return environmentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取测试环境详情
     */
    public TestEnvironmentResponse getEnvironment(Long id) {
        TestEnvironment env = environmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("测试环境不存在: " + id));
        return toResponse(env);
    }

    /**
     * 按类型获取测试环境
     */
    public List<TestEnvironmentResponse> getEnvironmentsByType(String envType) {
        return environmentRepository.findByEnvType(envType).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 创建测试环境
     */
    @Transactional
    public TestEnvironmentResponse createEnvironment(String envName, String envType,
                                                      String description, Map<String, Object> configData) {
        TestEnvironment env = new TestEnvironment();
        env.setEnvName(envName);
        env.setEnvType(envType);
        env.setDescription(description);
        env.setIsActive(true);

        try {
            if (configData != null) {
                env.setConfigData(objectMapper.writeValueAsString(configData));
            }
        } catch (JsonProcessingException e) {
            log.warn("序列化配置数据失败", e);
        }

        env = environmentRepository.save(env);
        return toResponse(env);
    }

    /**
     * 更新测试环境
     */
    @Transactional
    public TestEnvironmentResponse updateEnvironment(Long id, String envName, String envType,
                                                      String description, Map<String, Object> configData) {
        TestEnvironment env = environmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("测试环境不存在: " + id));

        if (envName != null) env.setEnvName(envName);
        if (envType != null) env.setEnvType(envType);
        if (description != null) env.setDescription(description);

        try {
            if (configData != null) {
                env.setConfigData(objectMapper.writeValueAsString(configData));
            }
        } catch (JsonProcessingException e) {
            log.warn("序列化配置数据失败", e);
        }

        env = environmentRepository.save(env);
        return toResponse(env);
    }

    /**
     * 启用/禁用测试环境
     */
    @Transactional
    public TestEnvironmentResponse toggleEnvironment(Long id, boolean active) {
        TestEnvironment env = environmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("测试环境不存在: " + id));
        env.setIsActive(active);
        env = environmentRepository.save(env);
        return toResponse(env);
    }

    /**
     * 删除测试环境
     */
    @Transactional
    public void deleteEnvironment(Long id) {
        environmentRepository.deleteById(id);
    }

    /**
     * 转换为响应DTO
     */
    private TestEnvironmentResponse toResponse(TestEnvironment env) {
        Map<String, Object> configData = null;
        try {
            if (env.getConfigData() != null) {
                configData = objectMapper.readValue(env.getConfigData(),
                        new TypeReference<Map<String, Object>>() {});
            }
        } catch (JsonProcessingException e) {
            log.warn("解析配置数据失败", e);
        }

        return TestEnvironmentResponse.builder()
                .id(env.getId())
                .envName(env.getEnvName())
                .envType(env.getEnvType())
                .description(env.getDescription())
                .configData(configData)
                .isActive(env.getIsActive())
                .createdAt(env.getCreatedAt())
                .updatedAt(env.getUpdatedAt())
                .build();
    }
}
