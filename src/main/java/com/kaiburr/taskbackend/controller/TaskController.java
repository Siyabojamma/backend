
package com.kaiburr.taskbackend.controller;

import com.kaiburr.taskbackend.model.Task;
import com.kaiburr.taskbackend.model.TaskExecution;
import com.kaiburr.taskbackend.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<Iterable<Task>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping(params = "id")
    public ResponseEntity<Task> getById(@RequestParam String id) {
        Optional<Task> t = service.findById(id);
        return t.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchByName(@RequestParam String name) {
        // In a full implementation, service would expose search endpoint; for brevity, this is a placeholder.
        return ResponseEntity.ok(List.of());
    }

    @PutMapping
    public ResponseEntity<Task> upsert(@RequestBody Task task) {
        if (task.getCommand() == null || task.getCommand().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Task saved = service.save(task);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam String id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/executions")
    public ResponseEntity<TaskExecution> run(@PathVariable String id, @RequestBody String command) {
        try {
            TaskExecution te = service.runCommandInPod(id, command);
            return ResponseEntity.ok(te);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
