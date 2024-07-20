package ru.practicum.util;

import ru.practicum.service.*;

public class Managers {

    public static TaskManager getDefault(HistoryManager historyManager, String path) {
        return new FileBackedTaskManager(historyManager, path);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
