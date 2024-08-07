package ru.practicum.service;

import ru.practicum.model.EpicTask;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManger implements TaskManager {


    int idCounter = 1;


    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();

    private final Set<Task> prioritizedTasks = new TreeSet<>();

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
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void deleteAllTasksByType(TaskType taskType) {
        switch (taskType) {
            case TASK:
                deleteAllTasks();
                break;
            case SUBTASK:
                deleteAllSubTasks();
                break;
            case EPIC_TASK:
                deleteAllEpicTasks();
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
                prioritizedTasks.remove(tasks.get(id));
                tasks.remove(id);
                historyManager.remove(id);
                break;
            case SUBTASK:
                prioritizedTasks.remove(subTasks.get(id));
                removeSubtask(id);
                historyManager.remove(id);
                break;
            case EPIC_TASK:
                prioritizedTasks.remove(epicTasks.get(id));
                removeEpicTaskById(id);
                historyManager.remove(id);
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
                tasks.put(task.getId(), task);
                addTaskByPriority(task);
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
                prioritizedTasks.remove(task);
                addTaskByPriority(task);
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
        for (EpicTask epicTask : epicTasks.values()) {
            if (epicTask.removeSubtaskIdIfExist(id)) {
                historyManager.remove(id);
                SubTask removed = subTasks.remove(id);
                prioritizedTasks.remove(removed);
                break;
            }
        }
    }


    @Override
    public List<SubTask> getSubTasksByEpicId(int id) {
        if (epicTasks.containsKey(id)) {
            EpicTask epicTask = epicTasks.get(id);
            return epicTask.getSubTasksIds().stream()
                    .map(subTasks::get)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public boolean isTaskOverlap(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();
        return prioritizedTasks.stream()
                .filter(prioritizedTask -> prioritizedTask.getStartTime() != null)
                .anyMatch(prioritizedTask -> !endTime.isBefore(prioritizedTask.getStartTime())
                        && !prioritizedTask.getEndTime().isBefore(startTime));
    }

    private void deleteAllSubTasks() {
        for (EpicTask epic : epicTasks.values()) {
            List<Integer> subTasksIds = epic.getSubTasksIds();
            subTasksIds.forEach(subTaskId -> {
                historyManager.remove(subTaskId);
                prioritizedTasks.remove(subTasks.get(subTaskId));
                subTasks.remove(subTaskId);
            });
            epic.clearSubTasks();
            int id = epic.getId();
            updateEpicTaskStatus(id);
            updateEpicStartTime(id);
            updateEpicEndTime(id);
            updateEpicDuration(id);
        }
    }

    private void deleteAllTasks() {
        tasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        });
        tasks.clear();
    }

    private void deleteAllEpicTasks() {
        Iterator<Integer> subTasksIterator = subTasks.keySet().iterator();
        while (subTasksIterator.hasNext()) {
            Integer subTaskId = subTasksIterator.next();
            historyManager.remove(subTaskId);
            subTasksIterator.remove();
        }

        Iterator<Integer> epicTasksIterator = epicTasks.keySet().iterator();
        while (epicTasksIterator.hasNext()) {
            Integer epicTaskId = epicTasksIterator.next();
            historyManager.remove(epicTaskId);
            prioritizedTasks.remove(epicTasks.get(epicTaskId));
            epicTasksIterator.remove();
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
            updateEpicStartTime(epicId);
            updateEpicEndTime(epicId);
            updateEpicDuration(epicId);
            addTaskByPriority(subTask);
            return subTask;
        }
        System.out.println("Указан некоректный эпик-айди");
        return null;
    }


    private EpicTask addEpicTask(EpicTask epicTask) {
        epicTask.setId(idCounter());
        epicTasks.put(epicTask.getId(), epicTask);
        int id = epicTask.getId();
        updateEpicTaskStatus(id);
        updateEpicStartTime(id);
        updateEpicEndTime(id);
        updateEpicDuration(id);
        addTaskByPriority(epicTask);
        return epicTask;
    }


    private void updateSubTask(SubTask subTask) {
        int id = subTask.getId();
        subTasks.computeIfPresent(id, (key, value) -> subTask);
        updateEpicTaskStatus(id);
        updateEpicStartTime(id);
        updateEpicEndTime(id);
        updateEpicDuration(id);
        prioritizedTasks.remove(subTask);
        addTaskByPriority(subTask);
    }


    private void updateEpicTask(EpicTask epicTask) {
        int id = epicTask.getId();
        epicTasks.computeIfPresent(id, (key, value) -> epicTask);
        updateEpicTaskStatus(id);
        updateEpicStartTime(id);
        updateEpicEndTime(id);
        updateEpicDuration(id);
        prioritizedTasks.remove(epicTask);
        addTaskByPriority(epicTask);
    }


    private void removeSubtask(int id) {
        if (subTasks.containsKey(id)) {
            SubTask remove = subTasks.remove(id);
            removeSubTaskInEpicTask(id);
            int epicId = remove.getEpicId();
            updateEpicTaskStatus(epicId);
            updateEpicStartTime(epicId);
            updateEpicEndTime(epicId);
            updateEpicDuration(epicId);
        }
    }


    private void removeEpicTaskById(int id) {
        if (epicTasks.containsKey(id)) {
            List<Integer> subTasksIds = epicTasks.get(id).getSubTasksIds();
            for (int subId : subTasksIds) {
                removeSubtask(subId);
            }
            epicTasks.get(id).clearSubTasks();
            epicTasks.remove(id);
        }
    }


    private void updateEpicTaskStatus(int epicId) {
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

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public Map<Integer, EpicTask> getEpicTasks() {
        return epicTasks;
    }

    public void setNextId(int nextId) {
        this.idCounter = nextId;
    }

    protected void updateEpicStartTime(int epicId) {
        EpicTask epicTask = epicTasks.get(epicId);
        List<Integer> subTasksIds = epicTask.getSubTasksIds();
        epicTask.setStartTime(calculateEpicStartTime(subTasksIds));
    }

    private LocalDateTime calculateEpicStartTime(List<Integer> subTasksIds) {
         return subTasksIds.stream()
                .map(id -> subTasks.get(id).getStartTime())
                .min(Comparator.naturalOrder()).orElse(null);
    }

    protected void updateEpicEndTime(int epicId) {
        EpicTask epicTask = epicTasks.get(epicId);
        List<Integer> subTasksIds = epicTask.getSubTasksIds();
        epicTask.setEndTime(calculateEpicEndTime(subTasksIds));
    }

    private LocalDateTime calculateEpicEndTime(List<Integer> subTasksIds) {
        return subTasksIds.stream()
                .map(id -> subTasks.get(id).getEndTime())
                .max(Comparator.naturalOrder()).orElse(null);
    }

    protected void updateEpicDuration(int epicId) {
        EpicTask epicTask = epicTasks.get(epicId);
        List<Integer> subTasksIds = epicTask.getSubTasksIds();
        epicTask.setDuration(calculateEpicDuration(subTasksIds));
    }

    private Duration calculateEpicDuration(List<Integer> subTasksIds) {
        return subTasksIds.stream()
                .map(subTasks::get)
                .map(Task::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    private void addTaskByPriority(Task task) {
        if (task.getStartTime() != null && !isTaskOverlap(task)) {
            prioritizedTasks.add(task);
        }
    }
}


