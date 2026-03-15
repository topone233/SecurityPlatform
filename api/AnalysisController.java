package org.example.securityplatform.api;

import lombok.RequiredArgsConstructor;
import org.example.securityplatform.dto.AnalysisResultResponse;
import org.example.securityplatform.entity.ApiInfo;
import org.example.securityplatform.entity.ComponentInfo;
import org.example.securityplatform.entity.ConfigInfo;
import org.example.securityplatform.entity.DangerFunction;
import org.example.securityplatform.entity.ExternalUrl;
import org.example.securityplatform.repository.ApiInfoRepository;
import org.example.securityplatform.repository.ArtifactRepository;
import org.example.securityplatform.repository.ComponentInfoRepository;
import org.example.securityplatform.repository.ConfigInfoRepository;
import org.example.securityplatform.repository.DangerFunctionRepository;
import org.example.securityplatform.repository.ExternalUrlRepository;
import org.example.securityplatform.service.analysis.AnalysisEngineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分析管理API控制器
 */
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisEngineService analysisEngineService;
    private final ArtifactRepository artifactRepository;
    private final ApiInfoRepository apiInfoRepository;
    private final ComponentInfoRepository componentInfoRepository;
    private final DangerFunctionRepository dangerFunctionRepository;
    private final ConfigInfoRepository configInfoRepository;
    private final ExternalUrlRepository externalUrlRepository;

    /**
     * 触发分析（异步）
     */
    @PostMapping("/trigger/{artifactId}")
    public String triggerAnalysis(@PathVariable Long artifactId) {
        artifactRepository.findById(artifactId)
                .orElseThrow(() -> new RuntimeException("制品不存在"));

        analysisEngineService.analyzeArtifactAsync(artifactId);
        return "分析任务已提交，请稍后查询结果";
    }

    /**
     * 触发分析（同步）
     */
    @PostMapping("/trigger-sync/{artifactId}")
    public AnalysisResultResponse triggerAnalysisSync(@PathVariable Long artifactId) {
        analysisEngineService.analyzeArtifact(artifactId);
        return getAnalysisResult(artifactId);
    }

    /**
     * 查询分析结果
     */
    @GetMapping("/result/{artifactId}")
    public AnalysisResultResponse getAnalysisResult(@PathVariable Long artifactId) {
        var artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new RuntimeException("制品不存在"));

        // 获取API信息
        List<ApiInfo> apis = apiInfoRepository.findByArtifactId(artifactId);
        List<AnalysisResultResponse.ApiSummary> apiSummaries = apis.stream()
                .map(api -> new AnalysisResultResponse.ApiSummary(
                        api.getUrl(),
                        api.getHttpMethod(),
                        api.getControllerClass(),
                        api.getMethodName(),
                        api.getFramework()))
                .collect(Collectors.toList());

        // 获取组件信息
        List<ComponentInfo> components = componentInfoRepository.findByArtifactId(artifactId);
        List<AnalysisResultResponse.ComponentSummary> componentSummaries = components.stream()
                .map(c -> new AnalysisResultResponse.ComponentSummary(
                        c.getComponentName(),
                        c.getComponentType(),
                        c.getDetectionSource()))
                .collect(Collectors.toList());

        // 获取危险函数信息
        List<DangerFunction> dangers = dangerFunctionRepository.findByArtifactId(artifactId);
        List<AnalysisResultResponse.DangerSummary> dangerSummaries = dangers.stream()
                .map(d -> new AnalysisResultResponse.DangerSummary(
                        d.getFunctionName(),
                        d.getFunctionType(),
                        d.getClassName(),
                        d.getLineNumber(),
                        d.getRiskLevel()))
                .collect(Collectors.toList());

        // 获取配置信息
        List<ConfigInfo> configs = configInfoRepository.findByArtifactId(artifactId);
        List<AnalysisResultResponse.ConfigSummary> configSummaries = configs.stream()
                .map(c -> new AnalysisResultResponse.ConfigSummary(
                        c.getConfigKey(),
                        c.getConfigValue(),
                        c.getCategory()))
                .collect(Collectors.toList());

        // 获取外部URL信息
        List<ExternalUrl> urls = externalUrlRepository.findByArtifactId(artifactId);
        List<AnalysisResultResponse.UrlSummary> urlSummaries = urls.stream()
                .map(u -> new AnalysisResultResponse.UrlSummary(
                        u.getUrl(),
                        u.getProtocol(),
                        u.getUsageContext()))
                .collect(Collectors.toList());

        AnalysisResultResponse response = new AnalysisResultResponse();
        response.setArtifactId(artifactId);
        response.setStatus(artifact.getStatus());
        response.setApis(apiSummaries);
        response.setComponents(componentSummaries);
        response.setDangers(dangerSummaries);
        response.setConfigs(configSummaries);
        response.setUrls(urlSummaries);

        return response;
    }

    /**
     * 查询分析进度
     */
    @GetMapping("/progress/{artifactId}")
    public String getAnalysisProgress(@PathVariable Long artifactId) {
        var artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new RuntimeException("制品不存在"));

        return artifact.getStatus();
    }
}