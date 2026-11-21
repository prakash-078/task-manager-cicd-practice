package com.example.taskmanager;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getTasks() {
        return taskService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(taskService.create(request.title()));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", exception.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleTask(@PathVariable long id) {
        Task task = taskService.toggle(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable long id) {
        return taskService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
