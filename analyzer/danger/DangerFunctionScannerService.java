package org.example.securityplatform.analyzer.danger;

import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.entity.DangerFunction;
import org.example.securityplatform.repository.DangerFunctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 危险函数扫描服务
 */
@Slf4j
@Service
public class DangerFunctionScannerService {

    @Autowired
    private DangerFunctionRepository dangerFunctionRepository;

    /**
     * 分析制品并扫描危险函数
     *
     * @param artifactId 制品ID
     * @param jarPath    Jar包路径
     */
    public void analyzeArtifact(Long artifactId, String jarPath) {
        try {
            log.info("开始扫描危险函数: artifactId={}, jarPath={}", artifactId, jarPath);

            List<DangerFunctionModel> allDangers = new ArrayList<>();

            // 命令执行扫描
            CommandExecutionScanner commandScanner = new CommandExecutionScanner(jarPath);
            allDangers.addAll(commandScanner.scan());
            log.info("命令执行扫描完成: 发现 {} 个", allDangers.size());

            // 反序列化扫描
            DeserializationScanner deserializationScanner = new DeserializationScanner(jarPath);
            List<DangerFunctionModel> deserializationDangers = deserializationScanner.scan();
            allDangers.addAll(deserializationDangers);
            log.info("反序列化扫描完成: 发现 {} 个", deserializationDangers.size());

            // 文件操作扫描
            FileIoScanner fileIoScanner = new FileIoScanner(jarPath);
            List<DangerFunctionModel> fileIoDangers = fileIoScanner.scan();
            allDangers.addAll(fileIoDangers);
            log.info("文件操作扫描完成: 发现 {} 个", fileIoDangers.size());

            log.info("共发现 {} 个危险函数", allDangers.size());

            // 保存危险函数信息
            for (DangerFunctionModel model : allDangers) {
                DangerFunction info = new DangerFunction();
                info.setArtifactId(artifactId);
                info.setFunctionName(model.getFunctionName());
                info.setFunctionType(model.getFunctionType());
                info.setClassName(model.getClassName());
                info.setLineNumber(model.getLineNumber());
                info.setFilePath(model.getFilePath());
                info.setMethodName(model.getMethodName());
                info.setRiskLevel(model.getRiskLevel());
                dangerFunctionRepository.save(info);
            }

            log.info("危险函数扫描完成: artifactId={}, 危险函数数量={}", artifactId, allDangers.size());

        } catch (IOException e) {
            log.error("危险函数扫描失败: artifactId={}", artifactId, e);
            throw new RuntimeException("危险函数扫描失败", e);
        }
    }

    /**
     * 获取制品的危险函数列表
     *
     * @param artifactId 制品ID
     * @return 危险函数列表
     */
    public List<DangerFunction> getDangersByArtifactId(Long artifactId) {
        return dangerFunctionRepository.findByArtifactId(artifactId);
    }

    /**
     * 根据风险等级获取危险函数列表
     *
     * @param artifactId 制品ID
     * @param riskLevel  风险等级
     * @return 危险函数列表
     */
    public List<DangerFunction> getDangersByRiskLevel(Long artifactId, String riskLevel) {
        return dangerFunctionRepository.findByArtifactIdAndRiskLevel(artifactId, riskLevel);
    }
}