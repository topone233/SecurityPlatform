package org.example.securityplatform.api;

import lombok.RequiredArgsConstructor;
import org.example.securityplatform.dto.ArtifactResponse;
import org.example.securityplatform.service.ArtifactService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 制品管理 API 控制器
 */
@RestController
@RequestMapping("/api/artifacts")
@RequiredArgsConstructor
public class ArtifactController {

    private final ArtifactService artifactService;

    /**
     * 上传制品
     */
    @PostMapping("/upload")
    public ArtifactResponse uploadArtifact(
            @RequestParam("taskId") Long taskId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("artifactType") String artifactType) {
        return artifactService.uploadArtifact(taskId, file, artifactType);
    }

    /**
     * 下载制品文件
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadArtifact(@PathVariable Long id) {
        return artifactService.downloadArtifact(id);
    }

    /**
     * 根据 ID 查询制品
     */
    @GetMapping("/{id}")
    public ArtifactResponse getArtifactById(@PathVariable Long id) {
        return artifactService.getArtifactById(id);
    }

    /**
     * 根据任务 ID 查询制品列表
     */
    @GetMapping("/task/{taskId}")
    public List<ArtifactResponse> getArtifactsByTaskId(@PathVariable Long taskId) {
        return artifactService.getArtifactsByTaskId(taskId);
    }

    /**
     * 根据状态查询制品列表
     */
    @GetMapping("/status/{status}")
    public List<ArtifactResponse> getArtifactsByStatus(@PathVariable String status) {
        return artifactService.getArtifactsByStatus(status);
    }

    /**
     * 查询所有制品
     */
    @GetMapping
    public List<ArtifactResponse> getAllArtifacts() {
        return artifactService.getAllArtifacts();
    }

    /**
     * 更新制品状态
     */
    @PutMapping("/{id}/status")
    public ArtifactResponse updateArtifactStatus(@PathVariable Long id, @RequestParam String status) {
        return artifactService.updateArtifactStatus(id, status);
    }

    /**
     * 删除制品
     */
    @DeleteMapping("/{id}")
    public void deleteArtifact(@PathVariable Long id) {
        artifactService.deleteArtifact(id);
    }
}