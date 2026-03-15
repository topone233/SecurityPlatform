package org.example.securityplatform.service;

import lombok.RequiredArgsConstructor;
import org.example.securityplatform.dto.CreateProjectRequest;
import org.example.securityplatform.dto.ProjectResponse;
import org.example.securityplatform.entity.Project;
import org.example.securityplatform.exception.BusinessException;
import org.example.securityplatform.repository.ProjectRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 项目服务类
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    /**
     * 创建项目
     *
     * @param request 创建项目请求
     * @return 项目响应
     */
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        // 检查项目名称是否已存在
        Optional<Project> existingProject = projectRepository.findByProjectName(request.getProjectName());
        if (existingProject.isPresent()) {
            throw new BusinessException("项目名称已存在");
        }

        Project project = new Project();
        BeanUtils.copyProperties(request, project);

        Project savedProject = projectRepository.save(project);
        return toResponse(savedProject);
    }

    /**
     * 根据ID查询项目
     *
     * @param id 项目ID
     * @return 项目响应
     */
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("项目不存在"));
        return toResponse(project);
    }

    /**
     * 根据项目名称查询项目
     *
     * @param projectName 项目名称
     * @return 项目响应
     */
    public ProjectResponse getProjectByName(String projectName) {
        Project project = projectRepository.findByProjectName(projectName)
                .orElseThrow(() -> new BusinessException("项目不存在"));
        return toResponse(project);
    }

    /**
     * 查询所有项目
     *
     * @return 项目响应列表
     */
    public List<ProjectResponse> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 删除项目
     *
     * @param id 项目ID
     */
    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("项目不存在"));
        projectRepository.delete(project);
    }

    private ProjectResponse toResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        BeanUtils.copyProperties(project, response);
        return response;
    }
}