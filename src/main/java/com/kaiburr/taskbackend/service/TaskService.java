
package com.kaiburr.taskbackend.service;

import com.kaiburr.taskbackend.model.Task;
import com.kaiburr.taskbackend.model.TaskExecution;
import com.kaiburr.taskbackend.repository.TaskRepository;
import com.kaiburr.taskbackend.util.CommandValidator;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class TaskService {
    private final TaskRepository repo;

    @Value("${KUBERNETES_NAMESPACE:default}")
    private String namespace;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public Iterable<Task> findAll() { return repo.findAll(); }
    public Optional<Task> findById(String id) { return repo.findById(id); }
    public Task save(Task t) { return repo.save(t); }
    public void deleteById(String id) { repo.deleteById(id); }

    public TaskExecution runCommandInPod(String taskId, String command) throws Exception {
        if (!CommandValidator.isSafe(command)) {
            throw new IllegalArgumentException("Command not allowed: " + command);
        }
        Task task = repo.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        Instant start = Instant.now();
        String logs = "";
        Instant end = start;

        try (KubernetesClient client = new KubernetesClientBuilder().build()){
            String podName = "taskexec-" + Instant.now().toEpochMilli();
            Pod pod = new PodBuilder()
                    .withNewMetadata().withName(podName).endMetadata()
                    .withNewSpec()
                        .withRestartPolicy("Never")
                        .addNewContainer()
                            .withName("task")
                            .withImage("busybox:1.36.1")
                            .withCommand("sh","-c", command)
                        .endContainer()
                    .endSpec()
                    .build();
             client.pods().inNamespace(namespace).resource(pod).create();
            client.pods().inNamespace(namespace).withName(podName).waitUntilCondition(p -> {
                return p.getStatus() != null && ("Succeeded".equals(p.getStatus().getPhase()) || "Failed".equals(p.getStatus().getPhase()));
            }, 30, TimeUnit.SECONDS);
            logs = client.pods().inNamespace(namespace).withName(podName).getLog();
            end = Instant.now();
            client.pods().inNamespace(namespace).withName(podName).delete();
        } catch (Exception e) {
            logs = "ERROR: " + e.getMessage();
            end = Instant.now();
        }

        TaskExecution te = new TaskExecution(start, end, logs);
        task.getTaskExecutions().add(te);
        repo.save(task);
        return te;
    }
}
