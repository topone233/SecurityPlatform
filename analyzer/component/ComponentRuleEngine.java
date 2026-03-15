package org.example.securityplatform.analyzer.component;

import java.util.*;

/**
 * 组件规则引擎 - 根据依赖识别组件
 */
public class ComponentRuleEngine {

    /**
     * 组件识别规则
     */
    private static final Map<String, ComponentRule> RULES = new LinkedHashMap<>();

    static {
        // ORM框架
        RULES.put("mybatis", new ComponentRule("MyBatis", "ORM", "CODE"));
        RULES.put("hibernate", new ComponentRule("Hibernate", "ORM", "CODE"));
        RULES.put("jpa", new ComponentRule("JPA", "ORM", "CODE"));

        // 缓存
        RULES.put("redis", new ComponentRule("Redis", "CACHE", "CODE"));
        RULES.put("ehcache", new ComponentRule("EhCache", "CACHE", "CODE"));

        // 消息队列
        RULES.put("kafka", new ComponentRule("Kafka", "MQ", "CODE"));
        RULES.put("rabbitmq", new ComponentRule("RabbitMQ", "MQ", "CODE"));
        RULES.put("activemq", new ComponentRule("ActiveMQ", "MQ", "CODE"));

        // 数据库
        RULES.put("mongodb", new ComponentRule("MongoDB", "DATABASE", "CODE"));
        RULES.put("elasticsearch", new ComponentRule("Elasticsearch", "SEARCH", "CODE"));

        // Web框架
        RULES.put("spring", new ComponentRule("Spring", "WEB", "CODE"));
        RULES.put("jaxrs", new ComponentRule("JAX-RS", "WEB", "CODE"));
    }

    /**
     * 根据依赖列表识别组件
     *
     * @param dependencies 依赖列表
     * @return 识别到的组件列表
     */
    public List<ComponentModel> identifyComponents(List<String> dependencies) {
        List<ComponentModel> components = new ArrayList<>();
        Set<String> identified = new HashSet<>();

        for (String dependency : dependencies) {
            String lowerDep = dependency.toLowerCase();

            for (Map.Entry<String, ComponentRule> entry : RULES.entrySet()) {
                String keyword = entry.getKey();
                ComponentRule rule = entry.getValue();

                if (lowerDep.contains(keyword)) {
                    String componentKey = keyword + ":" + rule.getComponentType();
                    if (!identified.contains(componentKey)) {
                        ComponentModel model = new ComponentModel();
                        model.setComponentName(rule.getComponentName());
                        model.setComponentType(rule.getComponentType());
                        model.setDetectionSource(rule.getDetectionSource());
                        model.setDetails(String.format("Detected from: %s", dependency));
                        components.add(model);
                        identified.add(componentKey);
                    }
                }
            }
        }

        return components;
    }

    /**
     * 组件规则
     */
    private static class ComponentRule {
        private final String componentName;
        private final String componentType;
        private final String detectionSource;

        public ComponentRule(String componentName, String componentType, String detectionSource) {
            this.componentName = componentName;
            this.componentType = componentType;
            this.detectionSource = detectionSource;
        }

        public String getComponentName() {
            return componentName;
        }

        public String getComponentType() {
            return componentType;
        }

        public String getDetectionSource() {
            return detectionSource;
        }
    }
}