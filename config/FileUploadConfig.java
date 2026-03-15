package org.example.securityplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.File;

/**
 * 文件上传配置类
 */
@Configuration
public class FileUploadConfig {

    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${file.max-size:500MB}")
    private String maxSize;

    private File uploadDirectory;

    @PostConstruct
    public void init() {
        uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public File getUploadDirectory() {
        return uploadDirectory;
    }

    public String getMaxSize() {
        return maxSize;
    }

    /**
     * 获取上传文件的基础路径
     */
    public String getBasePath() {
        return uploadDirectory.getAbsolutePath();
    }
}