package ru.practicum;


import ru.practicum.model.EpicTask;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;
import ru.practicum.service.FileBackedTaskManager;
import ru.practicum.service.TaskManager;
import ru.practicum.util.Managers;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), "./resource/file.csv");

        taskManager.addTask(new Task("Уборка", "Протереть стол", TaskStatus.NEW, TaskType.TASK));
        taskManager.addTask(new Task("Закалка", "Удариться мизинцем об тумбу", TaskStatus.NEW, TaskType.TASK));

        EpicTask epic1 = (EpicTask) taskManager.addTask(new EpicTask("Эпик_1", "Сделать кофе", TaskStatus.NEW, TaskType.EPIC_TASK));
        EpicTask epic2 = (EpicTask) taskManager.addTask(new EpicTask("Эпик_2", "Выпить кофе", TaskStatus.NEW, TaskType.EPIC_TASK));

        taskManager.addTask(new SubTask("Включить чайник", "Налить кофе", TaskStatus.NEW, epic1.getId(), TaskType.SUBTASK));
        taskManager.addTask(new SubTask("Взять молоко", "Налить молоко", TaskStatus.NEW, epic1.getId(), TaskType.SUBTASK));
        taskManager.addTask(new SubTask("Взять чашку", "Выпить кофе", TaskStatus.NEW, epic2.getId(), TaskType.SUBTASK));

        System.out.println("_________________________________________________________");
        System.out.println(taskManager.getAllTasksByType(TaskType.TASK));
        System.out.println(taskManager.getAllTasksByType(TaskType.EPIC_TASK));
        System.out.println(taskManager.getAllTasksByType(TaskType.SUBTASK));

        System.out.println("__________________________________________________________");
        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(new File("./resource/file.csv"));
        System.out.println(taskManager1.getAllTasksByType(TaskType.TASK));
        System.out.println(taskManager1.getAllTasksByType(TaskType.EPIC_TASK));
        System.out.println(taskManager1.getAllTasksByType(TaskType.SUBTASK));

        System.out.println("__________________________________________________________");
        taskManager1.addTask(new Task("Уборка", "Протереть стол", TaskStatus.NEW, TaskType.TASK));
        System.out.println(taskManager1.getAllTasksByType(TaskType.TASK));

    }

}
