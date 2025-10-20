
package com.kaiburr.taskbackend.model;

import java.time.Instant;

public class TaskExecution {
    private Instant startTime;
    private Instant endTime;
    private String output;

    public TaskExecution() {}

    public TaskExecution(Instant startTime, Instant endTime, String output) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.output = output;
    }

    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant s) { this.startTime = s; }
    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant e) { this.endTime = e; }
    public String getOutput() { return output; }
    public void setOutput(String o) { this.output = o; }
}
