package ru.practicum.model;

import ru.practicum.model.enums.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EpicTask extends Task {

    private final List<Integer> subTasksIds = new ArrayList<>();


    public EpicTask(int id, String name, String description, TaskStatus taskStatus) {
        super(id, name, description, taskStatus);
    }

    public EpicTask(String name, String description, TaskStatus taskStatus) {
        super(name, description, taskStatus);
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

    public void removeSubtaskIdIfExist(Integer id) {
        for (int i = 0; i < subTasksIds.size(); i++) {
            if (subTasksIds.get(i).equals(id)) {
                subTasksIds.remove(i);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EpicTask epicTask = (EpicTask) o;
        return Objects.equals(subTasksIds, epicTask.subTasksIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksIds);
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTasksIds=" + subTasksIds +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
