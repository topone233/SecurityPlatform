package org.example.securityplatform.analyzer.config;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * YAML配置解析器
 */
@Slf4j
public class YamlConfigParser {

    /**
     * 解析YAML配置文件
     *
     * @param filePath YAML文件路径
     * @return 配置模型列表
     */
    public List<ConfigModel> parseConfig(String filePath) {
        List<ConfigModel> configs = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(fis);

            // 递归解析配置
            flattenConfigs(data, "", filePath, configs);

        } catch (IOException e) {
            log.error("解析YAML配置文件失败: {}", filePath, e);
        }

        return configs;
    }

    /**
     * 递归扁平化配置
     */
    private void flattenConfigs(Map<String, Object> data, String prefix, String filePath, List<ConfigModel> configs) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                // 递归处理嵌套配置
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                flattenConfigs(nestedMap, key, filePath, configs);
            } else {
                // 保存配置
                ConfigModel model = new ConfigModel();
                model.setConfigKey(key);
                model.setConfigValue(value != null ? value.toString() : "");
                model.setConfigSource("YAML");
                model.setFilePath(filePath);
                model.setCategory(categorizeConfig(key));
                configs.add(model);
            }
        }
    }

    /**
     * 对配置进行分类
     */
    private String categorizeConfig(String key) {
        String lowerKey = key.toLowerCase();

        if (lowerKey.contains("datasource") || lowerKey.contains("database") || lowerKey.contains("jdbc")) {
            return "DATABASE";
        } else if (lowerKey.contains("redis") || lowerKey.contains("cache")) {
            return "CACHE";
        } else if (lowerKey.contains("kafka") || lowerKey.contains("mq") || lowerKey.contains("rabbit")) {
            return "MQ";
        } else if (lowerKey.contains("server") || lowerKey.contains("port")) {
            return "SERVER";
        } else if (lowerKey.contains("log")) {
            return "LOGGING";
        } else if (lowerKey.contains("spring")) {
            return "SPRING";
        }

        return "GENERAL";
    }
}