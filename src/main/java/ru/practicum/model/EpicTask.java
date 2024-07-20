package ru.practicum.model;

import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EpicTask extends Task {

    private final List<Integer> subTasksIds = new ArrayList<>();

    public EpicTask(int id, String name, String description, TaskStatus taskStatus, TaskType taskType) {
        super(id, name, description, taskStatus, taskType);
    }

    public EpicTask(String name, String description, TaskStatus taskStatus, TaskType taskType) {
        super(name, description, taskStatus, taskType);
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void addSubTaskId(int subId){
        subTasksIds.add(subId);
    }

    public void clearSubTasks() {
        subTasksIds.clear();
    }

    public boolean removeSubtaskIdIfExist(Integer id) {
        for (int i = 0; i < subTasksIds.size(); i++) {
            if (subTasksIds.get(i).equals(id)) {
                subTasksIds.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTasksIds=" + subTasksIds +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                '}';
    }
}
