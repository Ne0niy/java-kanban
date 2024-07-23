package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.exception.ManagerLoadException;
import ru.practicum.exception.ManagerSaveException;
import ru.practicum.model.EpicTask;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;
import ru.practicum.util.Managers;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskMangerTest<FileBackedTaskManager> {

    @BeforeEach
    void setUp() throws IOException {
        File tempFile = File.createTempFile("temp", "csv");
        taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), tempFile.getPath());
    }

    @Test
    void whenLoadEmptyFileThenEmptyTasks() throws IOException {
        File tempFile = File.createTempFile("temp", "csv");
        TaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = fileBackedTaskManager.getAllTasksByType(TaskType.TASK);
        List<Task> subTasks = fileBackedTaskManager.getAllTasksByType(TaskType.SUBTASK);
        List<Task> epicTasks = fileBackedTaskManager.getAllTasksByType(TaskType.EPIC_TASK);
        assertEquals(0, tasks.size());
        assertEquals(0, subTasks.size());
        assertEquals(0, epicTasks.size());
    }

    @Test
    void whenSaveAndLoadTasksThenNotEmptyTasks() throws IOException {
        File tempFile = File.createTempFile("temp", ".csv");
        TaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), tempFile.getPath());

        fileBackedTaskManager.addTask(new Task("Уборка", "Протереть стол", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now()));
        fileBackedTaskManager.addTask(new Task("Закалка", "Удариться мизинцем об тумбу", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now()));

        EpicTask epic1 = (EpicTask) fileBackedTaskManager.addTask(new EpicTask("Эпик_1", "Сделать кофе", TaskStatus.NEW, TaskType.EPIC_TASK));
        EpicTask epic2 = (EpicTask) fileBackedTaskManager.addTask(new EpicTask("Эпик_2", "Выпить кофе", TaskStatus.NEW, TaskType.EPIC_TASK));

        fileBackedTaskManager.addTask(new SubTask("Включить чайник", "Налить кофе", TaskStatus.NEW, epic1.getId(), TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now()));
        fileBackedTaskManager.addTask(new SubTask("Взять молоко", "Налить молоко", TaskStatus.NEW, epic1.getId(), TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now()));
        fileBackedTaskManager.addTask(new SubTask("Взять чашку", "Выпить кофе", TaskStatus.NEW, epic2.getId(), TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now()));

        TaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = newFileBackedTaskManager.getAllTasksByType(TaskType.TASK);
        List<Task> subTasks = newFileBackedTaskManager.getAllTasksByType(TaskType.SUBTASK);
        List<Task> epicTasks = newFileBackedTaskManager.getAllTasksByType(TaskType.EPIC_TASK);

        assertEquals(2, tasks.size());
        assertEquals(3, subTasks.size());
        assertEquals(2, epicTasks.size());
    }

    @Test
    void whenLoadTasksAndFileNotExists() {
        assertThrows(ManagerLoadException.class, () -> {
            FileBackedTaskManager.loadFromFile(new File("Path/notExistPath"));
        }, "Не удалось загрузить данные");
    }

    @Test
    void whenSaveTasksAndFileNotExists() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), "Path/notExistPath");
        assertThrows(ManagerSaveException.class, () -> {
            fileBackedTaskManager.addTask(new Task("Уборка", "Протереть стол", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now()));
        }, "Не удалось сохранить данные");
    }
}