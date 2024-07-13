package ru.practicum.service;

import org.junit.jupiter.api.Test;
import ru.practicum.model.EpicTask;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;
import ru.practicum.util.Managers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void whenLoadEmptyFile() throws IOException {
        File tempFile = File.createTempFile("temp", "csv");
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = taskManager.getAllTasksByType(TaskType.TASK);
        List<Task> subTasks = taskManager.getAllTasksByType(TaskType.SUBTASK);
        List<Task> epicTasks = taskManager.getAllTasksByType(TaskType.EPIC_TASK);
        assertEquals(0, tasks.size());
        assertEquals(0, subTasks.size());
        assertEquals(0, epicTasks.size());
    }

    @Test
    void whenSaveAndLoadTasks() throws IOException {
        File tempFile = File.createTempFile("temp", ".csv");
        TaskManager taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), tempFile.getPath());

        taskManager.addTask(new Task("Уборка", "Протереть стол", TaskStatus.NEW, TaskType.TASK));
        taskManager.addTask(new Task("Закалка", "Удариться мизинцем об тумбу", TaskStatus.NEW, TaskType.TASK));

        EpicTask epic1 = (EpicTask) taskManager.addTask(new EpicTask("Эпик_1", "Сделать кофе", TaskStatus.NEW, TaskType.EPIC_TASK));
        EpicTask epic2 = (EpicTask) taskManager.addTask(new EpicTask("Эпик_2", "Выпить кофе", TaskStatus.NEW, TaskType.EPIC_TASK));

        taskManager.addTask(new SubTask("Включить чайник", "Налить кофе", TaskStatus.NEW, epic1.getId(), TaskType.SUBTASK));
        taskManager.addTask(new SubTask("Взять молоко", "Налить молоко", TaskStatus.NEW, epic1.getId(), TaskType.SUBTASK));
        taskManager.addTask(new SubTask("Взять чашку", "Выпить кофе", TaskStatus.NEW, epic2.getId(), TaskType.SUBTASK));

        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = newTaskManager.getAllTasksByType(TaskType.TASK);
        List<Task> subTasks = newTaskManager.getAllTasksByType(TaskType.SUBTASK);
        List<Task> epicTasks = newTaskManager.getAllTasksByType(TaskType.EPIC_TASK);

        assertEquals(2, tasks.size());
        assertEquals(3, subTasks.size());
        assertEquals(2, epicTasks.size());
    }

}