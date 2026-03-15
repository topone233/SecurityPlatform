package org.example.securityplatform.analyzer.api;

import java.util.List;

/**
 * API提取策略接口
 */
public interface ApiExtractorStrategy {

    /**
     * 判断是否支持该文件
     *
     * @param filePath 文件路径
     * @return 是否支持
     */
    boolean supports(String filePath);

    /**
     * 提取API信息
     *
     * @param filePath 文件路径
     * @return API模型列表
     */
    List<ApiModel> extract(String filePath);
}