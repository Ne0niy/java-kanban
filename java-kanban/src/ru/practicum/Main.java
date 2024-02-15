package ru.practicum;

import ru.practicum.model.EpicTask;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskManager;
import ru.practicum.model.enums.TaskStatus;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        taskManager.addTask(new Task("Уборка", "Протереть стол", TaskStatus.NEW));
        taskManager.addTask(new Task("Закалка", "Удариться мизинцем об тумбу", TaskStatus.NEW));

        taskManager.addEpicTask(new EpicTask("Эпик_1", "Сделать кофе", TaskStatus.NEW));
        taskManager.addEpicTask(new EpicTask("Эпик_2", "Выпить кофе", TaskStatus.NEW));

        taskManager.addSubTask(new SubTask("Включить чайник", "Налить кофе", TaskStatus.NEW, 3));
        taskManager.addSubTask(new SubTask("Взять молоко", "Налить молоко", TaskStatus.NEW, 3));
        taskManager.addSubTask(new SubTask("Взять чашку", "Выпить кофе", TaskStatus.NEW, 4));

        System.out.println("");
        System.out.println(taskManager.getAllEpicTasks());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());

        taskManager.getTaskById(1).setTaskStatus(TaskStatus.DONE);
        taskManager.getTaskById(2).setTaskStatus(TaskStatus.IN_PROGRESS);

        taskManager.getSubTaskById(5).setTaskStatus(TaskStatus.DONE);
        taskManager.getSubTaskById(6).setTaskStatus(TaskStatus.DONE);
        taskManager.updateEpicTask(taskManager.getEpicTaskById(3));

        taskManager.getSubTaskById(7).setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpicTask(taskManager.getEpicTaskById(4));

        System.out.println("");
        System.out.println(taskManager.getAllEpicTasks());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());

        taskManager.deleteTaskById(1);
        taskManager.deleteEpicTaskById(3);

        System.out.println("");
        System.out.println(taskManager.getAllEpicTasks());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
    }
}
