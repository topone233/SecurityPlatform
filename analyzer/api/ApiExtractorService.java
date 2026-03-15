package org.example.securityplatform.analyzer.api;

import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.entity.ApiInfo;
import org.example.securityplatform.repository.ApiInfoRepository;
import org.example.securityplatform.util.FileUtil;
import org.example.securityplatform.util.JarUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * API提取服务
 */
@Slf4j
@Service
public class ApiExtractorService {

    @Autowired
    private ApiInfoRepository apiInfoRepository;

    private final List<ApiExtractorStrategy> strategies = new ArrayList<>();

    public ApiExtractorService() {
        strategies.add(new SpringMvcApiExtractor());
        strategies.add(new JaxRsApiExtractor());
    }

    /**
     * 分析制品并提取API
     *
     * @param artifactId 制品ID
     * @param jarPath    Jar包路径
     */
    public void analyzeArtifact(Long artifactId, String jarPath) {
        try {
            log.info("开始分析制品API: artifactId={}, jarPath={}", artifactId, jarPath);

            // 解压到临时目录
            String tempDir = System.getProperty("java.io.tmpdir") + "/security_platform/" + artifactId;
            FileUtil.createDirectoryIfNotExists(tempDir);

            JarUtil.extractJar(jarPath, tempDir);

            // 获取所有Java文件
            List<Path> javaFiles = FileUtil.listFilesByExtension(tempDir, "java");
            log.info("扫描到 {} 个Java文件", javaFiles.size());

            // 提取API
            List<ApiModel> allApis = new ArrayList<>();
            for (Path javaFile : javaFiles) {
                for (ApiExtractorStrategy strategy : strategies) {
                    if (strategy.supports(javaFile.toString())) {
                        List<ApiModel> apis = strategy.extract(javaFile.toString());
                        allApis.addAll(apis);
                    }
                }
            }

            log.info("提取到 {} 个API", allApis.size());

            // 保存API信息
            for (ApiModel model : allApis) {
                ApiInfo info = new ApiInfo();
                info.setArtifactId(artifactId);
                info.setUrl(model.getUrl());
                info.setHttpMethod(model.getHttpMethod());
                info.setControllerClass(model.getControllerClass());
                info.setMethodName(model.getMethodName());
                info.setParameters(model.getParameters());
                info.setFramework(model.getFramework());
                apiInfoRepository.save(info);
            }

            log.info("API分析完成: artifactId={}, API数量={}", artifactId, allApis.size());

        } catch (IOException e) {
            log.error("API分析失败: artifactId={}", artifactId, e);
            throw new RuntimeException("API分析失败", e);
        }
    }

    /**
     * 获取制品的API列表
     *
     * @param artifactId 制品ID
     * @return API列表
     */
    public List<ApiInfo> getApisByArtifactId(Long artifactId) {
        return apiInfoRepository.findByArtifactId(artifactId);
    }
}