package org.example.securityplatform.api;

import lombok.RequiredArgsConstructor;
import org.example.securityplatform.dto.CreateTaskRequest;
import org.example.securityplatform.dto.TaskResponse;
import org.example.securityplatform.service.TaskService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务管理API控制器
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * 创建任务
     */
    @PostMapping
    public TaskResponse createTask(@Validated @RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    /**
     * 根据ID查询任务
     */
    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    /**
     * 根据项目ID查询任务列表
     */
    @GetMapping("/project/{projectId}")
    public List<TaskResponse> getTasksByProjectId(@PathVariable Long projectId) {
        return taskService.getTasksByProjectId(projectId);
    }

    /**
     * 根据状态查询任务列表
     */
    @GetMapping("/status/{status}")
    public List<TaskResponse> getTasksByStatus(@PathVariable String status) {
        return taskService.getTasksByStatus(status);
    }

    /**
     * 查询所有任务
     */
    @GetMapping
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    /**
     * 更新任务状态
     */
    @PutMapping("/{id}/status")
    public TaskResponse updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        return taskService.updateTaskStatus(id, status);
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}