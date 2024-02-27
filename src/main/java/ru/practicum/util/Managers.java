package ru.practicum.util;

import ru.practicum.service.HistoryManager;
import ru.practicum.service.InMemoryHistoryManager;
import ru.practicum.service.InMemoryTaskManger;
import ru.practicum.service.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManger();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
