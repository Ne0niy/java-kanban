package ru.practicum.model;

import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;

import java.util.Objects;

public class SubTask extends Task {

    private int epicId;

    public SubTask(int id, String name, String description, TaskStatus taskStatus, int epicId, TaskType taskType) {
        super(id, name, description, taskStatus, taskType);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus taskStatus, int epicId, TaskType taskType) {
        super(name, description, taskStatus, taskType);
        this.epicId = epicId;
    }

    @Override
    public String toCSV() {
        return id + "," + taskType + "," + name + "," + taskStatus + "," + description + "," + epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }


    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                '}';
    }
}
