package org.example.securityplatform.analyzer.config;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * XML配置解析器
 */
@Slf4j
public class XmlConfigParser {

    /**
     * 解析XML配置文件
     *
     * @param filePath XML文件路径
     * @return 配置模型列表
     */
    public List<ConfigModel> parseConfig(String filePath) {
        List<ConfigModel> configs = new ArrayList<>();

        try {
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            // 解析所有元素
            parseElements(doc.getDocumentElement(), "", filePath, configs);

        } catch (Exception e) {
            log.error("解析XML配置文件失败: {}", filePath, e);
        }

        return configs;
    }

    /**
     * 递归解析XML元素
     */
    private void parseElements(Element element, String prefix, String filePath, List<ConfigModel> configs) {
        String tagName = element.getTagName();
        String key = prefix.isEmpty() ? tagName : prefix + "." + tagName;
        String value = element.getTextContent().trim();

        // 如果有属性，将属性也作为配置
        if (element.hasAttributes()) {
            for (int i = 0; i < element.getAttributes().getLength(); i++) {
                org.w3c.dom.Node attr = element.getAttributes().item(i);
                ConfigModel model = new ConfigModel();
                model.setConfigKey(key + "." + attr.getNodeName());
                model.setConfigValue(attr.getNodeValue());
                model.setConfigSource("XML");
                model.setFilePath(filePath);
                model.setCategory(categorizeConfig(key));
                configs.add(model);
            }
        }

        // 保存元素值
        if (!value.isEmpty()) {
            ConfigModel model = new ConfigModel();
            model.setConfigKey(key);
            model.setConfigValue(value);
            model.setConfigSource("XML");
            model.setFilePath(filePath);
            model.setCategory(categorizeConfig(key));
            configs.add(model);
        }

        // 递归处理子元素
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            org.w3c.dom.Node child = children.item(i);
            if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                parseElements((Element) child, key, filePath, configs);
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