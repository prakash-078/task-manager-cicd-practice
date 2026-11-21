package com.example.taskmanager;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final ConcurrentHashMap<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public TaskService() {
        create("Learn Java and Spring Boot");
        create("Run Maven tests");
        create("Build a Docker image");
    }

    public List<Task> findAll() {
        return tasks.values().stream()
                .sorted(Comparator.comparing(Task::id))
                .toList();
    }

    public Task create(String title) {
        String cleanTitle = title == null ? "" : title.trim();
        if (cleanTitle.isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        long id = nextId.getAndIncrement();
        Task task = new Task(id, cleanTitle, false);
        tasks.put(id, task);
        return task;
    }

    public Task toggle(long id) {
        return tasks.computeIfPresent(id,
                (key, oldTask) -> new Task(oldTask.id(), oldTask.title(), !oldTask.completed()));
    }

    public boolean delete(long id) {
        return tasks.remove(id) != null;
    }
}
