package ru.practicum.model;

import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;

import java.util.Objects;

public class Task {
    protected int id;

    protected String name;

    protected String description;

    protected TaskStatus taskStatus;

    protected TaskType taskType;

    public Task(int id, String name, String description, TaskStatus taskStatus, TaskType taskType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.taskType = taskType;
    }

    public Task(String name, String description, TaskStatus taskStatus, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.taskType = taskType;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                '}';
    }
}
