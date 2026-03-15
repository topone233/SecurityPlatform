package org.example.securityplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.dto.ConfigCheckItemResponse;
import org.example.securityplatform.entity.ConfigCheckItem;
import org.example.securityplatform.repository.ConfigCheckItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置检查项服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigCheckItemService {

    private final ConfigCheckItemRepository configCheckItemRepository;

    /**
     * 获取所有配置检查项
     */
    public List<ConfigCheckItemResponse> getAllConfigCheckItems() {
        return configCheckItemRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有启用的配置检查项
     */
    public List<ConfigCheckItemResponse> getAllEnabledConfigCheckItems() {
        return configCheckItemRepository.findByEnabledTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取配置检查项
     */
    public ConfigCheckItemResponse getConfigCheckItemById(Long id) {
        ConfigCheckItem item = configCheckItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("配置检查项不存在: " + id));
        return toResponse(item);
    }

    /**
     * 根据类别获取配置检查项
     */
    public List<ConfigCheckItemResponse> getConfigCheckItemsByCategory(String category) {
        return configCheckItemRepository.findByCategoryAndEnabledTrue(category).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据风险级别获取配置检查项
     */
    public List<ConfigCheckItemResponse> getConfigCheckItemsByRiskLevel(String riskLevel) {
        return configCheckItemRepository.findByRiskLevel(riskLevel).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 创建配置检查项
     */
    @Transactional
    public ConfigCheckItemResponse createConfigCheckItem(ConfigCheckItem item) {
        item.setEnabled(true);
        item = configCheckItemRepository.save(item);
        return toResponse(item);
    }

    /**
     * 更新配置检查项
     */
    @Transactional
    public ConfigCheckItemResponse updateConfigCheckItem(Long id, ConfigCheckItem updates) {
        ConfigCheckItem item = configCheckItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("配置检查项不存在: " + id));

        if (updates.getItemName() != null) {
            item.setItemName(updates.getItemName());
        }
        if (updates.getCategory() != null) {
            item.setCategory(updates.getCategory());
        }
        if (updates.getConfigKeyPattern() != null) {
            item.setConfigKeyPattern(updates.getConfigKeyPattern());
        }
        if (updates.getCheckRule() != null) {
            item.setCheckRule(updates.getCheckRule());
        }
        if (updates.getExpectedValue() != null) {
            item.setExpectedValue(updates.getExpectedValue());
        }
        if (updates.getUnsafeValues() != null) {
            item.setUnsafeValues(updates.getUnsafeValues());
        }
        if (updates.getRiskLevel() != null) {
            item.setRiskLevel(updates.getRiskLevel());
        }
        if (updates.getDescription() != null) {
            item.setDescription(updates.getDescription());
        }
        if (updates.getRemediation() != null) {
            item.setRemediation(updates.getRemediation());
        }
        if (updates.getStandardReference() != null) {
            item.setStandardReference(updates.getStandardReference());
        }

        item = configCheckItemRepository.save(item);
        return toResponse(item);
    }

    /**
     * 删除配置检查项
     */
    @Transactional
    public void deleteConfigCheckItem(Long id) {
        configCheckItemRepository.deleteById(id);
    }

    /**
     * 启用/禁用配置检查项
     */
    @Transactional
    public ConfigCheckItemResponse toggleConfigCheckItem(Long id, boolean enabled) {
        ConfigCheckItem item = configCheckItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("配置检查项不存在: " + id));
        item.setEnabled(enabled);
        item = configCheckItemRepository.save(item);
        return toResponse(item);
    }

    /**
     * 获取所有类别
     */
    public List<String> getAllCategories() {
        return configCheckItemRepository.findAllCategories();
    }

    /**
     * 转换为响应DTO
     */
    private ConfigCheckItemResponse toResponse(ConfigCheckItem item) {
        return ConfigCheckItemResponse.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .category(item.getCategory())
                .configKeyPattern(item.getConfigKeyPattern())
                .checkRule(item.getCheckRule())
                .expectedValue(item.getExpectedValue())
                .unsafeValues(item.getUnsafeValues())
                .riskLevel(item.getRiskLevel())
                .description(item.getDescription())
                .remediation(item.getRemediation())
                .standardReference(item.getStandardReference())
                .enabled(item.getEnabled())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}