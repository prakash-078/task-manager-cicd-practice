package com.example.taskmanager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {
    @Test
    void createsAndFindsTask() {
        TaskService service = new TaskService();
        Task created = service.create("Practise CI/CD");

        assertNotNull(created.id());
        assertEquals("Practise CI/CD", created.title());
        assertFalse(created.completed());
        assertTrue(service.findAll().stream().anyMatch(task -> task.id().equals(created.id())));
    }

    @Test
    void togglesTaskCompletion() {
        TaskService service = new TaskService();
        Task created = service.create("Write a test");

        Task updated = service.toggle(created.id());

        assertNotNull(updated);
        assertTrue(updated.completed());
    }

    @Test
    void rejectsBlankTaskTitle() {
        TaskService service = new TaskService();
        assertThrows(IllegalArgumentException.class, () -> service.create("   "));
    }
}
