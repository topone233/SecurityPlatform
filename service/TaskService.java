package org.example.securityplatform.service;

import lombok.RequiredArgsConstructor;
import org.example.securityplatform.common.TaskStatus;
import org.example.securityplatform.dto.CreateTaskRequest;
import org.example.securityplatform.dto.TaskResponse;
import org.example.securityplatform.entity.Project;
import org.example.securityplatform.entity.Task;
import org.example.securityplatform.exception.BusinessException;
import org.example.securityplatform.repository.ProjectRepository;
import org.example.securityplatform.repository.TaskRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 任务服务类
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    /**
     * 创建任务
     *
     * @param request 创建任务请求
     * @return 任务响应
     */
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        // 验证项目是否存在
        Optional<Project> projectOpt = projectRepository.findById(request.getProjectId());
        if (projectOpt.isEmpty()) {
            throw new BusinessException("项目不存在");
        }

        // 检查是否已存在同名任务
        Optional<Task> existingTask = taskRepository.findByProjectIdAndTaskName(
                request.getProjectId(), request.getTaskName());
        if (existingTask.isPresent()) {
            throw new BusinessException("该项目下已存在同名任务");
        }

        Task task = new Task();
        BeanUtils.copyProperties(request, task);
        task.setStatus(TaskStatus.CREATED);

        Task savedTask = taskRepository.save(task);
        return toResponse(savedTask);
    }

    /**
     * 根据ID查询任务
     *
     * @param id 任务ID
     * @return 任务响应
     */
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));
        return toResponse(task);
    }

    /**
     * 根据项目ID查询任务列表
     *
     * @param projectId 项目ID
     * @return 任务响应列表
     */
    public List<TaskResponse> getTasksByProjectId(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据状态查询任务列表
     *
     * @param status 任务状态
     * @return 任务响应列表
     */
    public List<TaskResponse> getTasksByStatus(String status) {
        List<Task> tasks = taskRepository.findByStatus(status);
        return tasks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 查询所有任务
     *
     * @return 任务响应列表
     */
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 更新任务状态
     *
     * @param id     任务ID
     * @param status 新状态
     * @return 任务响应
     */
    @Transactional
    public TaskResponse updateTaskStatus(Long id, String status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));
        task.setStatus(status);
        Task savedTask = taskRepository.save(task);
        return toResponse(savedTask);
    }

    /**
     * 删除任务
     *
     * @param id 任务ID
     */
    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));
        taskRepository.delete(task);
    }

    private TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        BeanUtils.copyProperties(task, response);
        return response;
    }
}