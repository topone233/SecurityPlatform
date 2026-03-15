package org.example.securityplatform.analyzer.url;

import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.entity.ExternalUrl;
import org.example.securityplatform.repository.ExternalUrlRepository;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 外部URL检测服务
 */
@Slf4j
@Service
public class ExternalUrlDetectorService {

    @Autowired
    private ExternalUrlRepository externalUrlRepository;

    // URL匹配模式
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(https?://[\\w\\-]+(\\.[\\w\\-]+)+[\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 分析制品并检测外部URL
     *
     * @param artifactId 制品ID
     * @param jarPath    Jar包路径
     */
    public void analyzeArtifact(Long artifactId, String jarPath) {
        try {
            log.info("开始分析制品外部URL: artifactId={}, jarPath={}", artifactId, jarPath);

            // 解压到临时目录
            String tempDir = System.getProperty("java.io.tmpdir") + "/security_platform/" + artifactId;
            FileUtil.createDirectoryIfNotExists(tempDir);

            JarUtil.extractJar(jarPath, tempDir);

            // 扫描所有文件
            List<Path> allFiles = FileUtil.listFilesRecursively(tempDir);
            log.info("扫描到 {} 个文件", allFiles.size());

            // 检测URL
            List<ExternalUrlModel> allUrls = new ArrayList<>();
            for (Path filePath : allFiles) {
                String content = readFileContent(filePath);
                if (content != null && !content.isEmpty()) {
                    allUrls.addAll(detectUrls(content, filePath.toString()));
                }
            }

            log.info("检测到 {} 个外部URL", allUrls.size());

            // 保存URL信息
            for (ExternalUrlModel model : allUrls) {
                ExternalUrl info = new ExternalUrl();
                info.setArtifactId(artifactId);
                info.setUrl(model.getUrl());
                info.setProtocol(model.getProtocol());
                info.setUsageContext(model.getUsageContext());
                info.setFilePath(model.getFilePath());
                externalUrlRepository.save(info);
            }

            log.info("外部URL分析完成: artifactId={}, URL数量={}", artifactId, allUrls.size());

        } catch (IOException e) {
            log.error("外部URL分析失败: artifactId={}", artifactId, e);
            throw new RuntimeException("外部URL分析失败", e);
        }
    }

    /**
     * 从文本中检测URL
     *
     * @param text    文本内容
     * @param filePath 文件路径
     * @return URL模型列表
     */
    private List<ExternalUrlModel> detectUrls(String text, String filePath) {
        List<ExternalUrlModel> urls = new ArrayList<>();
        Matcher matcher = URL_PATTERN.matcher(text);

        while (matcher.find()) {
            String url = matcher.group(1);
            String protocol = url.startsWith("https") ? "HTTPS" : "HTTP";

            ExternalUrlModel model = new ExternalUrlModel();
            model.setUrl(url);
            model.setProtocol(protocol);
            model.setFilePath(filePath);
            model.setUsageContext(determineContext(text, url));

            urls.add(model);
        }

        return urls;
    }

    /**
     * 确定URL的使用上下文
     */
    private String determineContext(String text, String url) {
        String context = "";

        // 检查是否在配置文件中
        if (text.contains("url=") || text.contains("URL=")) {
            context = "CONFIG";
        } else if (text.contains("@Value") || text.contains("${")) {
            context = "PROPERTY_INJECTION";
        } else if (text.contains("request") || text.contains("http")) {
            context = "HTTP_REQUEST";
        } else if (text.contains("ws:") || text.contains("wss:")) {
            context = "WEBSOCKET";
        }

        return context;
    }

    /**
     * 读取文件内容
     */
    private String readFileContent(Path filePath) {
        try {
            // 只读取文本文件
            String fileName = filePath.getFileName().toString();
            if (fileName.endsWith(".class") || fileName.endsWith(".jar") || fileName.endsWith(".zip")) {
                return null;
            }

            byte[] bytes = Files.readAllBytes(filePath);
            return new String(bytes);
        } catch (IOException e) {
            log.warn("读取文件失败: {}", filePath, e);
            return null;
        }
    }

    /**
     * 获取制品的外部URL列表
     *
     * @param artifactId 制品ID
     * @return URL列表
     */
    public List<ExternalUrl> getUrlsByArtifactId(Long artifactId) {
        return externalUrlRepository.findByArtifactId(artifactId);
    }
}