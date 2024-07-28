package ru.practicum.service;

import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.enums.TaskType;
import java.util.List;


public interface TaskManager {

     List<Task> getAllTasksByType(TaskType taskType);

     List<Task> getPrioritizedTasks();

     void deleteAllTasksByType(TaskType taskType);

     Task getTaskByIdAndType(int id, TaskType taskType);

     Task addTask(Task task);

     void updateTask(Task task);

     void deleteTaskByIdAndType(int id, TaskType taskType);

     void removeSubTaskInEpicTask(SubTask subTask);

     List<SubTask> getSubTasksByEpicId(int id);

     List<Task> getHistory();

     boolean isTaskOverlap(Task task);
}
