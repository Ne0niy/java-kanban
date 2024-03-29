package ru.practicum.service;

import ru.practicum.model.EpicTask;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryTaskManger implements TaskManager {



    int idCounter = 1;


    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();

    private final HistoryManager historyManager;

    public InMemoryTaskManger(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int idCounter() {
        return idCounter++;
    }


    @Override
    public List<Task> getAllTasksByType(TaskType taskType) {
        switch (taskType) {
            case TASK:
                return new ArrayList<>(tasks.values());
            case SUBTASK:
                return new ArrayList<>(subTasks.values());
            case EPIC_TASK:
                return new ArrayList<>(epicTasks.values());
            default:
                System.out.println("TaskType не найден");
                return new ArrayList<>();
        }
    }

    @Override
    public void deleteAllTasksByType(TaskType taskType) {
        switch (taskType) {
            case TASK:
                tasks.clear();
                break;
            case SUBTASK:
                deleteAllSubTasks();
                break;
            case EPIC_TASK:
                subTasks.clear();
                epicTasks.clear();
                break;
            default:
                System.out.println("TaskType не найден");
        }
    }

    @Override
    public Task getTaskByIdAndType(int id, TaskType taskType) {
        switch (taskType) {
            case TASK:
                if (!tasks.containsKey(id)) {
                    return null;
                }
                Task task = tasks.get(id);
                historyManager.add(task);
                return task;
            case SUBTASK:
                if (!subTasks.containsKey(id)) {
                    return null;
                }
                SubTask subTask = subTasks.get(id);
                historyManager.add(subTask);
                return subTask;
            case EPIC_TASK:
                if (!epicTasks.containsKey(id)) {
                    return null;
                }
                EpicTask epicTask = epicTasks.get(id);
                historyManager.add(epicTask);
                return epicTask;
            default:
                System.out.println("TaskType не найден");
                return null;
        }
    }

    @Override
    public void deleteTaskByIdAndType(int id, TaskType taskType) {
        switch (taskType) {
            case TASK:
                tasks.remove(id);
                break;
            case SUBTASK:
                removeSubtask(id);
                break;
            case EPIC_TASK:
                removeEpicTaskById(id);
                break;
            default:
                System.out.println("TaskType не найден");
        }
    }

    @Override
    public Task addTask(Task task) {
        switch (task.getTaskType()) {
            case TASK:
                task.setId(idCounter());
                tasks.put(task.getId() ,task);
                return task;
            case SUBTASK:
                return addSubTask((SubTask) task);
            case EPIC_TASK:
                return addEpicTask((EpicTask) task);
            default:
                return null;
        }
    }

    @Override
    public void updateTask(Task task) {
        switch (task.getTaskType()) {
            case TASK:
                tasks.computeIfPresent(task.getId(), (key, value) -> task);
                break;
            case SUBTASK:
                updateSubTask((SubTask) task);
                break;
            case EPIC_TASK:
                updateEpicTask((EpicTask) task);
                break;
            default:
                System.out.println("Task не найден");
        }
    }

    @Override
    public void removeSubTaskInEpicTask(int id) {
        for (EpicTask epicTask : epicTasks.values()){
            epicTask.removeSubtaskIdIfExist(id);
        }
    }


    @Override
    public List<SubTask> getSubTasksByEpicId(int id) {
        if (epicTasks.containsKey(id)){
            EpicTask epicTask = epicTasks.get(id);
            return epicTask.getSubTasksIds().stream()
                    .map(subTasks :: get)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void deleteAllSubTasks() {
        for (EpicTask epic : epicTasks.values()) {
            List<Integer> subTasksIds = epic.getSubTasksIds();
            subTasksIds.forEach(subTasks::remove);
            epic.clearSubTasks();
            updateEpicTaskStatus(epic.getId());
        }
    }


    private SubTask addSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        if (epicTasks.containsKey(epicId)) {
            EpicTask epicTask = epicTasks.get(epicId);
            subTask.setId(idCounter());
            epicTask.addSubTaskId(subTask.getId());
            subTasks.put(subTask.getId(), subTask);
            updateEpicTaskStatus(epicId);
            return subTask;
        }
        System.out.println("Указан некоректный эпик-айди");
        return null;
    }


    private EpicTask addEpicTask(EpicTask epicTask) {
        epicTask.setId(idCounter());
        epicTasks.put(epicTask.getId(), epicTask);
        updateEpicTaskStatus(epicTask.getId());
        return epicTask;
    }



    private void updateSubTask(SubTask subTask) {
        int id = subTask.getId();
        subTasks.computeIfPresent(id, (key, value) -> subTask);
        updateEpicTaskStatus(subTask.getEpicId());
    }


    private void updateEpicTask(EpicTask epicTask) {
        int id = epicTask.getId();
        epicTasks.computeIfPresent(id, (key, value) -> epicTask);
        updateEpicTaskStatus(epicTask.getId());
    }


    private void removeSubtask(int id) {
        if (subTasks.containsKey(id)) {
            SubTask remove = subTasks.remove(id);
            removeSubTaskInEpicTask(id);
            updateEpicTaskStatus(remove.getEpicId());
        }
    }


    private void removeEpicTaskById(int id) {
        if (epicTasks.containsKey(id)) {
            List<Integer> subTasksIds = epicTasks.get(id).getSubTasksIds();
            for (int subId : subTasksIds){
                removeSubtask(subId);
            }
            epicTasks.get(id).clearSubTasks();
            epicTasks.remove(id);
        }
    }





    private void updateEpicTaskStatus (int epicId) {
        EpicTask epicTask = epicTasks.get(epicId);
        List<Integer> subTasksIds = epicTask.getSubTasksIds();
        epicTask.setTaskStatus(getEpicTaskStatusBySubTaskStatuses(subTasksIds));
    }

    private TaskStatus getEpicTaskStatusBySubTaskStatuses(List<Integer> subTasksIds) {
        if (subTasksIds.isEmpty()) {
            return TaskStatus.NEW;
        }
        boolean isNew = true;
        boolean isDone = true;
        for (Integer subTaskId : subTasksIds) {
            TaskStatus taskType = subTasks.get(subTaskId).getTaskStatus();
            if (taskType != TaskStatus.NEW) {
                isNew = false;
            }
            if (taskType != TaskStatus.DONE) {
                isDone = false;
            }
        }
        if (isNew) {
            return TaskStatus.NEW;
        }
        if (isDone) {
            return TaskStatus.DONE;
        }
        return TaskStatus.IN_PROGRESS;
    }
}


