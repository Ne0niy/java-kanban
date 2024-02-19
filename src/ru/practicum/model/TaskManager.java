package ru.practicum.model;

import ru.practicum.model.enums.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class TaskManager {


    int idCounter = 1;

    public int idCounter() {
        return idCounter++;
    }

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public List<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
    }

    public void deleteAllEpicTasks() {
        epicTasks.clear();
    }

    //NEW v2 // "Нужно еще удалить подзадачи из эпика" - я сохранил предидущий метод, но сделал новый, общий
    public void deleteAllSubAndEpicTasks() {
        subTasks.clear();
        epicTasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public EpicTask getEpicTaskById(int id) {
        return epicTasks.get(id);
    }

    public Task addTask(Task task) {
        task.setId(idCounter());
        tasks.put(task.getId() ,task);
        return task;
    }

    public SubTask addSubTask(SubTask subTask) {
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

    public EpicTask addEpicTask(EpicTask epicTask) {
        epicTask.setId(idCounter());
        epicTasks.put(epicTask.getId(), epicTask);
        updateEpicTaskStatus(epicTask.getId());
        return epicTask;
    }

    public void updateTask(Task task) {
        int id = task.getId();
        tasks.computeIfPresent(id, (key, value) -> task);
    }

    public void updateSubTask(SubTask subTask) {
        int id = subTask.getId();
        subTasks.computeIfPresent(id, (key, value) -> subTask);
        updateEpicTaskStatus(subTask.getEpicId());
    }

    public void updateEpicTask(EpicTask epicTask) {
        int id = epicTask.getId();
        epicTasks.computeIfPresent(id, (key, value) -> epicTask);
        updateEpicTaskStatus(epicTask.getId());
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    //NEW v2 // Я удалил deleteSubTaskById(int id) и сделал removeSubtask(int id)
    //Если я правельно понял, то так мы будем удалять Subtask из мапы что находится в TaskManager
    //и не будет затрагивать список в EpicTask, а removeAllSubtasks я не делал, ведь уже есть deleteAllSubTasks()
    public void removeSubtask(int id) {
        if (subTasks.containsKey(id)) {
            subTasks.remove(id, subTasks.get(id));
        }
    }
    public void deleteEpicTaskById(int id) {
        if (epicTasks.containsKey(id)){
            EpicTask remove = epicTasks.remove(id);
            remove.getSubTasksIds().forEach(subTasks :: remove);
        }
    }

    public List<SubTask> getSubTasksByEpicId(int id) {
        if (epicTasks.containsKey(id)){
            EpicTask epicTask = epicTasks.get(id);
            return epicTask.getSubTasksIds().stream()
                    .map(subTasks :: get)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
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
