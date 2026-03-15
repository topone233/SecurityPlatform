package org.example.securityplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.securityplatform.common.ArtifactStatus;
import org.example.securityplatform.common.ValidationConstants;
import org.example.securityplatform.config.FileUploadConfig;
import org.example.securityplatform.dto.ArtifactResponse;
import org.example.securityplatform.entity.Artifact;
import org.example.securityplatform.entity.Task;
import org.example.securityplatform.exception.BusinessException;
import org.example.securityplatform.repository.ArtifactRepository;
import org.example.securityplatform.repository.TaskRepository;
import org.example.securityplatform.util.FileUtil;
import org.example.securityplatform.util.HashUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 制品服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final TaskRepository taskRepository;
    private final FileUploadConfig fileUploadConfig;

    /**
     * 上传制品
     *
     * @param taskId      任务 ID
     * @param file        上传的文件
     * @param artifactType 制品类型
     * @return 制品响应
     */
    @Transactional
    public ArtifactResponse uploadArtifact(Long taskId, MultipartFile file, String artifactType) {
        // 验证任务是否存在
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("任务不存在"));

        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 校验文件大小
        if (file.getSize() > ValidationConstants.MAX_FILE_SIZE) {
            throw new BusinessException("文件大小超过限制 (500MB)");
        }

        // 校验制品类型
        if (!isValidArtifactType(artifactType)) {
            throw new BusinessException("不支持的制品类型：" + artifactType);
        }

        // 校验文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = FileUtil.getExtension(originalFilename);
        if (!isValidExtension(extension)) {
            throw new BusinessException("不支持的文件格式：" + extension);
        }

        // 校验制品类型与扩展名是否匹配
        if (!isTypeExtensionMatch(artifactType, extension)) {
            throw new BusinessException("制品类型与文件扩展名不匹配");
        }

        try {
            // 生成唯一的文件名
            String uniqueFileName = UUID.randomUUID().toString() + "." + extension;

            // 创建任务目录
            String taskDir = fileUploadConfig.getBasePath() + File.separator + taskId;
            FileUtil.createDirectoryIfNotExists(taskDir);

            // 保存文件
            String filePath = taskDir + File.separator + uniqueFileName;
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());

            // 计算 MD5
            String md5 = HashUtil.calculateMD5(filePath);

            // 检查是否已存在相同的 MD5 文件
            Optional<Artifact> existingArtifact = artifactRepository.findByMd5(md5);
            if (existingArtifact.isPresent()) {
                Files.deleteIfExists(path);
                log.warn("已存在相同的 MD5 文件：{}", md5);
                throw new BusinessException("已存在相同的制品文件");
            }

            // 保存制品信息
            Artifact artifact = new Artifact();
            artifact.setTaskId(taskId);
            artifact.setArtifactName(originalFilename);
            artifact.setArtifactType(artifactType);
            artifact.setFilePath(filePath);
            artifact.setFileSize(file.getSize());
            artifact.setMd5(md5);
            artifact.setStatus(ArtifactStatus.UPLOADED);

            Artifact savedArtifact = artifactRepository.save(artifact);
            log.info("制品上传成功：id={}, name={}, size={}", artifact.getId(), originalFilename, file.getSize());
            return toResponse(savedArtifact);

        } catch (IOException e) {
            log.error("文件上传失败：taskId={}, fileName={}", taskId, originalFilename, e);
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 校验制品类型
     */
    private boolean isValidArtifactType(String artifactType) {
        return Arrays.stream(ValidationConstants.ALLOWED_ARTIFACT_TYPES)
                .anyMatch(type -> type.equalsIgnoreCase(artifactType));
    }

    /**
     * 校验文件扩展名
     */
    private boolean isValidExtension(String extension) {
        return Arrays.stream(ValidationConstants.ALLOWED_EXTENSIONS)
                .anyMatch(ext -> ext.equalsIgnoreCase(extension));
    }

    /**
     * 校验制品类型与扩展名是否匹配
     */
    private boolean isTypeExtensionMatch(String artifactType, String extension) {
        for (String[] mapping : ValidationConstants.TYPE_EXTENSION_MAP) {
            if (mapping[0].equalsIgnoreCase(artifactType) && mapping[1].equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 下载制品文件
     *
     * @param id 制品 ID
     * @return 文件响应
     */
    public ResponseEntity<Resource> downloadArtifact(Long id) {
        Artifact artifact = artifactRepository.findById(id)
                .orElseThrow(() -> new BusinessException("制品不存在"));

        if (artifact.getFilePath() == null) {
            throw new BusinessException("制品文件不存在");
        }

        Path filePath = Paths.get(artifact.getFilePath());
        if (!Files.exists(filePath)) {
            throw new BusinessException("制品文件已丢失");
        }

        try {
            Resource resource = new FileSystemResource(filePath);
            String contentType = getContentType(artifact.getArtifactType());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + artifact.getArtifactName() + "\"")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(artifact.getFileSize()))
                    .body(resource);

        } catch (Exception e) {
            log.error("文件下载失败：artifactId={}", id, e);
            throw new BusinessException("文件下载失败：" + e.getMessage());
        }
    }

    /**
     * 获取内容类型
     */
    private String getContentType(String artifactType) {
        switch (artifactType.toUpperCase()) {
            case "JAR":
                return "application/java-archive";
            case "WAR":
                return "application/x-war";
            case "ZIP":
                return "application/zip";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 根据 ID 查询制品
     *
     * @param id 制品 ID
     * @return 制品响应
     */
    public ArtifactResponse getArtifactById(Long id) {
        Artifact artifact = artifactRepository.findById(id)
                .orElseThrow(() -> new BusinessException("制品不存在"));
        return toResponse(artifact);
    }

    /**
     * 根据任务 ID 查询制品列表
     *
     * @param taskId 任务 ID
     * @return 制品响应列表
     */
    public List<ArtifactResponse> getArtifactsByTaskId(Long taskId) {
        List<Artifact> artifacts = artifactRepository.findByTaskId(taskId);
        return artifacts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据状态查询制品列表
     *
     * @param status 制品状态
     * @return 制品响应列表
     */
    public List<ArtifactResponse> getArtifactsByStatus(String status) {
        List<Artifact> artifacts = artifactRepository.findByStatus(status);
        return artifacts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 查询所有制品
     *
     * @return 制品响应列表
     */
    public List<ArtifactResponse> getAllArtifacts() {
        List<Artifact> artifacts = artifactRepository.findAll();
        return artifacts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 更新制品状态
     *
     * @param id     制品 ID
     * @param status 新状态
     * @return 制品响应
     */
    @Transactional
    public ArtifactResponse updateArtifactStatus(Long id, String status) {
        Artifact artifact = artifactRepository.findById(id)
                .orElseThrow(() -> new BusinessException("制品不存在"));
        artifact.setStatus(status);
        Artifact savedArtifact = artifactRepository.save(artifact);
        return toResponse(savedArtifact);
    }

    /**
     * 删除制品
     *
     * @param id 制品 ID
     */
    @Transactional
    public void deleteArtifact(Long id) {
        Artifact artifact = artifactRepository.findById(id)
                .orElseThrow(() -> new BusinessException("制品不存在"));

        // 删除文件
        try {
            if (artifact.getFilePath() != null) {
                Files.deleteIfExists(Paths.get(artifact.getFilePath()));
            }
        } catch (IOException e) {
            log.error("删除文件失败：{}", artifact.getFilePath(), e);
        }

        // 删除数据库记录
        artifactRepository.delete(artifact);
    }

    private ArtifactResponse toResponse(Artifact artifact) {
        ArtifactResponse response = new ArtifactResponse();
        BeanUtils.copyProperties(artifact, response);
        return response;
    }
}