package org.example.securityplatform.api;

import lombok.RequiredArgsConstructor;
import org.example.securityplatform.dto.CreateProjectRequest;
import org.example.securityplatform.dto.ProjectResponse;
import org.example.securityplatform.service.ProjectService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目管理API控制器
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 创建项目
     */
    @PostMapping
    public ProjectResponse createProject(@Validated @RequestBody CreateProjectRequest request) {
        return projectService.createProject(request);
    }

    /**
     * 根据ID查询项目
     */
    @GetMapping("/{id}")
    public ProjectResponse getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    /**
     * 根据项目名称查询项目
     */
    @GetMapping("/name/{projectName}")
    public ProjectResponse getProjectByName(@PathVariable String projectName) {
        return projectService.getProjectByName(projectName);
    }

    /**
     * 查询所有项目
     */
    @GetMapping
    public List<ProjectResponse> getAllProjects() {
        return projectService.getAllProjects();
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}