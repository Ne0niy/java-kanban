package ru.practicum.service;

import org.junit.jupiter.api.Test;
import ru.practicum.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    @Test
    void whenEmptyHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void whenAddTask() {
        Task task = new Task(1, "Задача-1");
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(task, history.get(0));
    }

    @Test
    void whenAddCopyTask() {
        Task task = new Task(1, "Задача-1");
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void whenAddUpdatedTask() {
        Task task = new Task(1, "Задача-1");
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        task.setName("Задача-новая");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
        assertEquals(task.getName(), history.get(0).getName());
    }

    @Test
    void whenRemove() {
        Task task1 = new Task(1, "Задача-1");
        Task task2 = new Task(2, "Задача-2");
        Task task3 = new Task(3, "Задача-3");
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(2, historyManager.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }
}
