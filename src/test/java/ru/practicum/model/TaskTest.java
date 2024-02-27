package ru.practicum.model;

import org.junit.jupiter.api.Test;
import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void whenTaskEquals() {
        Task task = new Task(1, "Task1", "task1", TaskStatus.NEW, TaskType.TASK);
        Task task2 = new Task(1, "Task2", "task2", TaskStatus.NEW, TaskType.TASK);
        assertEquals(task, task2);

    }

    @Test
    void whenEpicTaskEquals() {
        EpicTask task = new EpicTask(1, "Task1", "task1", TaskStatus.NEW, TaskType.EPIC_TASK);
        EpicTask task2 = new EpicTask(1, "Task2", "task2", TaskStatus.NEW, TaskType.EPIC_TASK);
        assertEquals(task, task2);
    }

    @Test
    void whenSubTaskEquals() {
        SubTask task = new SubTask(1, "Task1", "task1", TaskStatus.NEW, 1, TaskType.SUBTASK);
        SubTask task2 = new SubTask(1, "Task2", "task2", TaskStatus.NEW, 1, TaskType.SUBTASK);
        assertEquals(task, task2);
    }

    @Test
    void whenTaskNotEquals() {
        Task task = new Task(1, "Task1", "task1", TaskStatus.NEW, TaskType.TASK);
        Task task2 = new Task(2, "Task2", "task2", TaskStatus.NEW, TaskType.TASK);
        assertNotEquals(task, task2);

    }

    @Test
    void whenEpicTaskNotEquals() {
        EpicTask task = new EpicTask(1, "Task1", "task1", TaskStatus.NEW, TaskType.EPIC_TASK);
        EpicTask task2 = new EpicTask(2, "Task2", "task2", TaskStatus.NEW, TaskType.EPIC_TASK);
        assertNotEquals(task, task2);
    }

    @Test
    void whenSubTaskNotEquals() {
        SubTask task = new SubTask(1, "Task1", "task1", TaskStatus.NEW, 1, TaskType.SUBTASK);
        SubTask task2 = new SubTask(2, "Task2", "task2", TaskStatus.NEW, 1, TaskType.SUBTASK);
        assertNotEquals(task, task2);
    }
}