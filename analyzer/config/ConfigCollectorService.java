package org.example.securityplatform.analyzer.config;

import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.entity.ConfigInfo;
import org.example.securityplatform.repository.ConfigInfoRepository;
import org.example.securityplatform.util.FileUtil;
import org.example.securityplatform.util.JarUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * 配置收集服务
 */
@Slf4j
@Service
public class ConfigCollectorService {

    @Autowired
    private ConfigInfoRepository configInfoRepository;

    private final YamlConfigParser yamlParser = new YamlConfigParser();
    private final PropertiesConfigParser propertiesParser = new PropertiesConfigParser();
    private final XmlConfigParser xmlParser = new XmlConfigParser();

    /**
     * 分析制品并收集配置
     *
     * @param artifactId 制品ID
     * @param jarPath    Jar包路径
     */
    public void analyzeArtifact(Long artifactId, String jarPath) {
        try {
            log.info("开始分析制品配置: artifactId={}, jarPath={}", artifactId, jarPath);

            // 获取配置文件条目
            List<JarEntry> configEntries = JarUtil.getConfigEntries(jarPath);
            log.info("扫描到 {} 个配置文件", configEntries.size());

            // 解压到临时目录
            String tempDir = System.getProperty("java.io.tmpdir") + "/security_platform/" + artifactId;
            FileUtil.createDirectoryIfNotExists(tempDir);

            JarUtil.extractJar(jarPath, tempDir);

            // 解析配置文件
            List<ConfigModel> allConfigs = new ArrayList<>();
            for (JarEntry entry : configEntries) {
                String entryName = entry.getName();
                String configPath = tempDir + "/" + entryName;

                if (entryName.endsWith(".yml") || entryName.endsWith(".yaml")) {
                    allConfigs.addAll(yamlParser.parseConfig(configPath));
                } else if (entryName.endsWith(".properties")) {
                    allConfigs.addAll(propertiesParser.parseConfig(configPath));
                } else if (entryName.endsWith(".xml")) {
                    allConfigs.addAll(xmlParser.parseConfig(configPath));
                }
            }

            log.info("解析到 {} 个配置项", allConfigs.size());

            // 保存配置信息
            for (ConfigModel model : allConfigs) {
                ConfigInfo info = new ConfigInfo();
                info.setArtifactId(artifactId);
                info.setConfigKey(model.getConfigKey());
                info.setConfigValue(model.getConfigValue());
                info.setConfigSource(model.getConfigSource());
                info.setFilePath(model.getFilePath());
                info.setCategory(model.getCategory());
                configInfoRepository.save(info);
            }

            log.info("配置分析完成: artifactId={}, 配置数量={}", artifactId, allConfigs.size());

        } catch (IOException e) {
            log.error("配置分析失败: artifactId={}", artifactId, e);
            throw new RuntimeException("配置分析失败", e);
        }
    }

    /**
     * 获取制品的配置列表
     *
     * @param artifactId 制品ID
     * @return 配置列表
     */
    public List<ConfigInfo> getConfigsByArtifactId(Long artifactId) {
        return configInfoRepository.findByArtifactId(artifactId);
    }
}