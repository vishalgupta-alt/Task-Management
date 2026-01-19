package com.antino.taskmanagement.Controller;

import com.antino.taskmanagement.Entity.Task;
import com.antino.taskmanagement.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("/create")
    public Task createTask(
            @RequestBody Task task,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized");
        }

        String token = authHeader.replace("Bearer ", "");

        return taskService.createTask(task, token);
    }

    @GetMapping
    public List<Task> getAllTasks(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String search) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized");
        }

        String token = authHeader.replace("Bearer ", "");

        return taskService.getAllTasks(token, status, priority, search);
    }

    @GetMapping("/{id}")
    public Task getSingleTask(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized");
        }

        String token = authHeader.replace("Bearer ", "");

        return taskService.getTaskById(id, token);
    }

    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable Long id,
            @RequestBody Task task,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized");
        }

        String token = authHeader.replace("Bearer ", "");

        return taskService.updateTask(id, task, token);
    }
    @DeleteMapping("/{id}")
    public String deleteTask(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader("Role") String role
    ) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized");
        }

        String token = authHeader.replace("Bearer ", "");

        taskService.deleteTask(id, token, role);

        return "Task deleted successfully";
    }

}
