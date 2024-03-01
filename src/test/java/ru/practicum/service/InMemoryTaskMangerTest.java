package ru.practicum.service;

import org.junit.jupiter.api.Test;
import ru.practicum.model.EpicTask;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskMangerTest {

    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void whenGetAllTasks() {
        TaskManager taskManager = new InMemoryTaskManger(historyManager);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, TaskType.TASK);
        Task task2 = new Task("name2", "description2", TaskStatus.NEW, TaskType.TASK);
        List<Task> expected = List.of(taskManager.addTask(task1), taskManager.addTask(task2));
        List<Task> actual = taskManager.getAllTasksByType(TaskType.TASK);
        assertEquals(actual, expected);
    }

    @Test
    void whenGetAllEpic() {
        TaskManager taskManager = new InMemoryTaskManger(historyManager);
        EpicTask task1 = new EpicTask("name1", "description1", TaskStatus.NEW, TaskType.EPIC_TASK);
        EpicTask task2 = new EpicTask("name2", "description2", TaskStatus.NEW, TaskType.EPIC_TASK);
        List<Task> expected = List.of(taskManager.addTask(task1), taskManager.addTask(task2));
        List<Task> actual = taskManager.getAllTasksByType(TaskType.EPIC_TASK);
        assertEquals(actual, expected);
}

    @Test
    void whenDeleteAllTasks() {
        TaskManager taskManager = new InMemoryTaskManger(historyManager);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, TaskType.TASK);
        Task task2 = new Task("name2", "description2", TaskStatus.NEW, TaskType.TASK);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.deleteAllTasksByType(TaskType.TASK);
        List<Task> actual = taskManager.getAllTasksByType(TaskType.TASK);
        assertTrue(actual.isEmpty());
    }

    @Test
    void whenDeleteAllEpic() {
        TaskManager taskManager = new InMemoryTaskManger(historyManager);
        EpicTask task1 = new EpicTask("name1", "description1", TaskStatus.NEW, TaskType.EPIC_TASK);
        EpicTask task2 = new EpicTask("name2", "description2", TaskStatus.NEW, TaskType.EPIC_TASK);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.deleteAllTasksByType(TaskType.EPIC_TASK);
        List<Task> actual = taskManager.getAllTasksByType(TaskType.EPIC_TASK);
        assertTrue(actual.isEmpty());
    }

    @Test
    void whenDeleteAllSubTask() {
        TaskManager taskManager = new InMemoryTaskManger(historyManager);
        SubTask task1 = new SubTask("name1", "description1", TaskStatus.NEW, 1, TaskType.SUBTASK);
        SubTask task2 = new SubTask("name2", "description2", TaskStatus.NEW, 1, TaskType.SUBTASK);
        EpicTask epic = new EpicTask("name1", "description1", TaskStatus.NEW, TaskType.EPIC_TASK);
        taskManager.addTask(epic);
        epic.addSubTaskId(task1.getId());
        epic.addSubTaskId(task2.getId());
        taskManager.deleteAllTasksByType(TaskType.SUBTASK);
        List<Task> actual = taskManager.getAllTasksByType(TaskType.SUBTASK);
        assertTrue(actual.isEmpty());
    }

    @Test
    void whenGetTaskByIdAndType() {
        TaskManager taskManager = new InMemoryTaskManger(historyManager);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, TaskType.TASK);
        Task task2 = new Task("name2", "description2", TaskStatus.NEW, TaskType.TASK);
        Task expected1 = taskManager.addTask(task1);
        Task expected2 = taskManager.addTask(task2);
        Task actual1 = taskManager.getTaskByIdAndType(expected1.getId(), TaskType.TASK);
        Task actual2 = taskManager.getTaskByIdAndType(expected2.getId(), TaskType.TASK);
        assertEquals(actual1, expected1);
        assertEquals(actual2, expected2);
    }

    @Test
    void whenGetEpicByIdAndType() {
        TaskManager taskManager = new InMemoryTaskManger(historyManager);
        EpicTask task1 = new EpicTask("name1", "description1", TaskStatus.NEW, TaskType.EPIC_TASK);
        EpicTask task2 = new EpicTask("name2", "description2", TaskStatus.NEW, TaskType.EPIC_TASK);
        Task expected1 = taskManager.addTask(task1);
        Task expected2 = taskManager.addTask(task2);
        Task actual1 = taskManager.getTaskByIdAndType(expected1.getId(), TaskType.EPIC_TASK);
        Task actual2 = taskManager.getTaskByIdAndType(expected2.getId(), TaskType.EPIC_TASK);
        assertEquals(actual1, expected1);
        assertEquals(actual2, expected2);
    }

    @Test
    void whenDeleteTaskByIdAndType() {
        TaskManager taskManager = new InMemoryTaskManger(historyManager);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, TaskType.TASK);
        Task task2 = new Task("name2", "description2", TaskStatus.NEW, TaskType.TASK);
        Task saved1 = taskManager.addTask(task1);
        Task saved2 = taskManager.addTask(task2);
        taskManager.deleteTaskByIdAndType(saved1.getId(), TaskType.TASK);
        taskManager.deleteTaskByIdAndType(saved2.getId(), TaskType.TASK);
        List<Task> actual = taskManager.getAllTasksByType(TaskType.TASK);
        assertTrue(actual.isEmpty());
    }

    @Test
    void whenDeleteEpicByIdAndType() {
        TaskManager taskManager = new InMemoryTaskManger(historyManager);
        EpicTask task1 = new EpicTask("name1", "description1", TaskStatus.NEW, TaskType.EPIC_TASK);
        EpicTask task2 = new EpicTask("name2", "description2", TaskStatus.NEW, TaskType.EPIC_TASK);
        Task saved1 = taskManager.addTask(task1);
        Task saved2 = taskManager.addTask(task2);
        taskManager.deleteTaskByIdAndType(saved1.getId(), TaskType.EPIC_TASK);
        taskManager.deleteTaskByIdAndType(saved2.getId(), TaskType.EPIC_TASK);
        List<Task> actual = taskManager.getAllTasksByType(TaskType.EPIC_TASK);
        assertTrue(actual.isEmpty());
    }

    @Test
    void whenUpdateTask() {
        TaskManager taskManager = new InMemoryTaskManger(historyManager);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, TaskType.TASK);
        Task oldTask = taskManager.addTask(task1);
        Task update = new Task(oldTask.getId(), "name2", "description2", TaskStatus.NEW, TaskType.TASK);
        taskManager.updateTask(update);
        Task updateTask = taskManager.getTaskByIdAndType(oldTask.getId(), TaskType.TASK);
        assertEquals(update, updateTask);
    }

    @Test
    void whenUpdateEpic() {
        TaskManager taskManager = new InMemoryTaskManger(historyManager);
        EpicTask task1 = new EpicTask("name1", "description1", TaskStatus.NEW, TaskType.EPIC_TASK);
        Task oldTask = taskManager.addTask(task1);
        EpicTask update = new EpicTask(
                oldTask.getId(),
                "name2",
                "description2",
                TaskStatus.NEW,
                TaskType.EPIC_TASK);
        taskManager.updateTask(update);
        Task updateTask = taskManager.getTaskByIdAndType(oldTask.getId(), TaskType.EPIC_TASK);
        assertEquals(update, updateTask);
    }
}