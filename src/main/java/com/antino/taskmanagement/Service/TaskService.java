package com.antino.taskmanagement.Service;

import com.antino.taskmanagement.Entity.Task;
import com.antino.taskmanagement.Repository.TaskRepository;
import com.antino.taskmanagement.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public Task createTask(Task task, String token) {

        // get current user email from JWT
        String email = jwtUtil.getEmailFromToken(token);

        // auto-set createdBy
        task.setCreatedBy(email);

        // default status if not sent
        if (task.getStatus() == null) {
            task.setStatus("PENDING");
        }
        return taskRepository.save(task);
    }


    public List<Task> getAllTasks(
            String token,
            String status,
            String priority,
            String search) {

        String email = jwtUtil.getEmailFromToken(token);

        // default: user sees own tasks
        List<Task> tasks = taskRepository.findByCreatedBy(email);

        if (status != null) {
            tasks = taskRepository.findByStatus(status);
        }

        if (priority != null) {
            tasks = taskRepository.findByPriority(priority);
        }

        if (search != null) {
            tasks = taskRepository.findByTitleContainingIgnoreCase(search);
        }

        return tasks;
    }
    public Task getTaskById(Long taskId, String token) {

        String email = jwtUtil.getEmailFromToken(token);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Authorization: creator OR admin
        if (!task.getCreatedBy().equals(email)) {
            throw new RuntimeException("Access denied");
        }

        return task;
    }

    public Task updateTask(Long taskId, Task updatedTask, String token) {

        String email = jwtUtil.getEmailFromToken(token);

        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Authorization: only creator can update
        if (!existingTask.getCreatedBy().equals(email)) {
            throw new RuntimeException("Access denied");
        }

        // Update all fields
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setPriority(updatedTask.getPriority());

        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long taskId, String token, String role) {

        String email = jwtUtil.getEmailFromToken(token);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Authorization
        if (!task.getCreatedBy().equals(email) && !role.equals("admin")) {
            throw new RuntimeException("You are not allowed to delete this task");
        }

        taskRepository.delete(task);
    }

}
