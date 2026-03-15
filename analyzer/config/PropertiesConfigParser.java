package org.example.securityplatform.analyzer.config;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Properties配置解析器
 */
@Slf4j
public class PropertiesConfigParser {

    /**
     * 解析Properties配置文件
     *
     * @param filePath Properties文件路径
     * @return 配置模型列表
     */
    public List<ConfigModel> parseConfig(String filePath) {
        List<ConfigModel> configs = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath)) {
            Properties props = new Properties();
            props.load(fis);

            for (String key : props.stringPropertyNames()) {
                ConfigModel model = new ConfigModel();
                model.setConfigKey(key);
                model.setConfigValue(props.getProperty(key));
                model.setConfigSource("PROPERTIES");
                model.setFilePath(filePath);
                model.setCategory(categorizeConfig(key));
                configs.add(model);
            }

        } catch (IOException e) {
            log.error("解析Properties配置文件失败: {}", filePath, e);
        }

        return configs;
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