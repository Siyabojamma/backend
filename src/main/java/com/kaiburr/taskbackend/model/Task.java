
package com.kaiburr.taskbackend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "tasks")
public class Task {
    @Id
    private String id;
    private String name;
    private String owner;
    private String command;
    private List<TaskExecution> taskExecutions = new ArrayList<>();

    public Task() {}

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public String getOwner() { return owner; }
    public void setOwner(String o) { this.owner = o; }
    public String getCommand() { return command; }
    public void setCommand(String c) { this.command = c; }
    public List<TaskExecution> getTaskExecutions() { return taskExecutions; }
    public void setTaskExecutions(List<TaskExecution> l) { this.taskExecutions = l; }
}
