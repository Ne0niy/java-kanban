package ru.practicum.service;

import org.junit.jupiter.api.Test;
import ru.practicum.model.EpicTask;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class TaskMangerTest<T extends TaskManager> {

    protected T taskManager;

    @Test
    void whenGetAllTasks() {
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task("name2", "description2", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now());
        List<Task> expected = List.of(taskManager.addTask(task1), taskManager.addTask(task2));
        List<Task> actual = taskManager.getAllTasksByType(TaskType.TASK);
        assertEquals(actual, expected);
    }

    @Test
    void whenGetAllEpic() {
        EpicTask task1 = new EpicTask("name1", "description1", TaskStatus.NEW, TaskType.EPIC_TASK);
        EpicTask task2 = new EpicTask("name2", "description2", TaskStatus.NEW, TaskType.EPIC_TASK);
        List<Task> expected = List.of(taskManager.addTask(task1), taskManager.addTask(task2));
        List<Task> actual = taskManager.getAllTasksByType(TaskType.EPIC_TASK);
        assertEquals(actual, expected);
}

    @Test
    void whenDeleteAllTasks() {
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task("name2", "description2", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now());
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.deleteAllTasksByType(TaskType.TASK);
        List<Task> actual = taskManager.getAllTasksByType(TaskType.TASK);
        assertTrue(actual.isEmpty());
    }

    @Test
    void whenDeleteAllEpic() {
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
        SubTask task1 = new SubTask("name1", "description1", TaskStatus.NEW, 1, TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now());
        SubTask task2 = new SubTask("name2", "description2", TaskStatus.NEW, 1, TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now());
        EpicTask epic = new EpicTask("name1", "description1", TaskStatus.NEW, TaskType.EPIC_TASK);
        taskManager.addTask(epic);
        task1.setEpicId(epic.getId());
        task2.setEpicId(epic.getId());
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.deleteAllTasksByType(TaskType.SUBTASK);
        List<Task> actual = taskManager.getAllTasksByType(TaskType.SUBTASK);
        assertTrue(actual.isEmpty());
    }

    @Test
    void whenGetTaskByIdAndType() {
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task("name2", "description2", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now());
        Task expected1 = taskManager.addTask(task1);
        Task expected2 = taskManager.addTask(task2);
        Task actual1 = taskManager.getTaskByIdAndType(expected1.getId(), TaskType.TASK);
        Task actual2 = taskManager.getTaskByIdAndType(expected2.getId(), TaskType.TASK);
        assertEquals(actual1, expected1);
        assertEquals(actual2, expected2);
    }

    @Test
    void whenGetEpicByIdAndType() {
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
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task("name2", "description2", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now());
        Task saved1 = taskManager.addTask(task1);
        Task saved2 = taskManager.addTask(task2);
        taskManager.deleteTaskByIdAndType(saved1.getId(), TaskType.TASK);
        taskManager.deleteTaskByIdAndType(saved2.getId(), TaskType.TASK);
        List<Task> actual = taskManager.getAllTasksByType(TaskType.TASK);
        assertTrue(actual.isEmpty());
    }

    @Test
    void whenDeleteEpicByIdAndType() {
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
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now());
        Task oldTask = taskManager.addTask(task1);
        Task update = new Task(oldTask.getId(), "name2", "description2", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now());
        taskManager.updateTask(update);
        Task updateTask = taskManager.getTaskByIdAndType(oldTask.getId(), TaskType.TASK);
        assertEquals(update, updateTask);
    }

    @Test
    void whenUpdateEpic() {
        EpicTask task1 = new EpicTask("name1", "description1", TaskStatus.NEW, TaskType.EPIC_TASK);
        Task oldTask = taskManager.addTask(task1);
        EpicTask update = new EpicTask(
                oldTask.getId(),
                "name2",
                "description2",
                TaskStatus.NEW,
                TaskType.EPIC_TASK,
                Duration.ofMinutes(15),
                LocalDateTime.now());
        taskManager.updateTask(update);
        Task updateTask = taskManager.getTaskByIdAndType(oldTask.getId(), TaskType.EPIC_TASK);
        assertEquals(update, updateTask);
    }

    @Test
    void whenSubTaskStatusIsNewThenEpicStatusIsNew() {
        Task task = taskManager.addTask(new EpicTask("name1", "description1", TaskStatus.IN_PROGRESS, TaskType.EPIC_TASK));
        taskManager.addTask(new SubTask("name1", "description1", TaskStatus.NEW, task.getId(), TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now()));
        taskManager.addTask(new SubTask("name2", "description2", TaskStatus.NEW, task.getId(), TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now()));
        assertEquals(TaskStatus.NEW, taskManager.getTaskByIdAndType(task.getId(), TaskType.EPIC_TASK).getTaskStatus());
    }

    @Test
    void whenSubTaskStatusIsDoneThenEpicStatusIsDone() {
        Task task = taskManager.addTask(new EpicTask("name1", "description1", TaskStatus.NEW, TaskType.EPIC_TASK));
        taskManager.addTask(new SubTask("name1", "description1", TaskStatus.DONE, task.getId(), TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now()));
        taskManager.addTask(new SubTask("name2", "description2", TaskStatus.DONE, task.getId(), TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now()));
        assertEquals(TaskStatus.DONE, taskManager.getTaskByIdAndType(task.getId(), TaskType.EPIC_TASK).getTaskStatus());
    }

    @Test
    void whenSubTaskStatusIsDoneAndNewThenEpicStatusIsInProgress() {
        Task task = taskManager.addTask(new EpicTask("name1", "description1", TaskStatus.NEW, TaskType.EPIC_TASK));
        taskManager.addTask(new SubTask("name1", "description1", TaskStatus.DONE, task.getId(), TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now()));
        taskManager.addTask(new SubTask("name2", "description2", TaskStatus.NEW, task.getId(), TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now()));
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskByIdAndType(task.getId(), TaskType.EPIC_TASK).getTaskStatus());
    }

    @Test
    void whenSubTaskStatusIsInProgressThenEpicStatusIsInProgress() {
        Task task = taskManager.addTask(new EpicTask("name1", "description1", TaskStatus.NEW, TaskType.EPIC_TASK));
        taskManager.addTask(new SubTask("name1", "description1", TaskStatus.IN_PROGRESS, task.getId(), TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now()));
        taskManager.addTask(new SubTask("name2", "description2", TaskStatus.IN_PROGRESS, task.getId(), TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now()));
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskByIdAndType(task.getId(), TaskType.EPIC_TASK).getTaskStatus());
    }

    @Test
    void whenAddSubTaskToEpicTask() {
        Task epicTask = taskManager.addTask(new EpicTask("name1", "description1", TaskStatus.NEW, TaskType.EPIC_TASK));
        SubTask subTask = (SubTask) taskManager.addTask(new SubTask("name1", "description1", TaskStatus.IN_PROGRESS, epicTask.getId(), TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now()));
        assertEquals(epicTask.getId(), subTask.getEpicId());
    }

    @Test
    void whenAddTaskIsOverLap() {
        LocalDateTime startTime = LocalDateTime.now();
        taskManager.addTask(new Task("name1", "description1", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), startTime));
        taskManager.addTask(new Task("name2", "description2", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(100), startTime.minusMinutes(90)));
        taskManager.addTask(new Task("name3", "description3", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(100), startTime.plusMinutes(200)));
        assertEquals(taskManager.getPrioritizedTasks().size(), 2);
    }


}