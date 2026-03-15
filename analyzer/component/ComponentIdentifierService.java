package org.example.securityplatform.analyzer.component;

import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.repository.ApiInfoRepository;
import org.example.securityplatform.repository.ComponentInfoRepository;
import org.example.securityplatform.entity.ComponentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 组件识别服务
 */
@Slf4j
@Service
public class ComponentIdentifierService {

    @Autowired
    private ComponentInfoRepository componentInfoRepository;

    private final DependencyScanner dependencyScanner = new DependencyScanner();
    private final ComponentRuleEngine ruleEngine = new ComponentRuleEngine();

    /**
     * 分析制品并识别组件
     *
     * @param artifactId 制品ID
     * @param jarPath    Jar包路径
     */
    public void analyzeArtifact(Long artifactId, String jarPath) {
        try {
            log.info("开始分析制品组件: artifactId={}, jarPath={}", artifactId, jarPath);

            // 扫描依赖
            List<String> dependencies = dependencyScanner.scanDependencies(jarPath);
            log.info("扫描到 {} 个依赖", dependencies.size());

            // 识别组件
            List<ComponentModel> components = ruleEngine.identifyComponents(dependencies);
            log.info("识别到 {} 个组件", components.size());

            // 保存组件信息
            for (ComponentModel model : components) {
                ComponentInfo info = new ComponentInfo();
                info.setArtifactId(artifactId);
                info.setComponentName(model.getComponentName());
                info.setComponentType(model.getComponentType());
                info.setDetectionSource(model.getDetectionSource());
                info.setDetails(model.getDetails());
                componentInfoRepository.save(info);
            }

            log.info("组件分析完成: artifactId={}, 组件数量={}", artifactId, components.size());

        } catch (IOException e) {
            log.error("组件分析失败: artifactId={}", artifactId, e);
            throw new RuntimeException("组件分析失败", e);
        }
    }

    /**
     * 获取制品的组件列表
     *
     * @param artifactId 制品ID
     * @return 组件列表
     */
    public List<ComponentInfo> getComponentsByArtifactId(Long artifactId) {
        return componentInfoRepository.findByArtifactId(artifactId);
    }
}