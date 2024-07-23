package ru.practicum.service;

import ru.practicum.exception.ManagerLoadException;
import ru.practicum.exception.ManagerSaveException;
import ru.practicum.model.EpicTask;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;
import ru.practicum.util.Managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManger {

    private File file;

    public FileBackedTaskManager(HistoryManager historyManager, String path) {
        super(historyManager);
        file = new File(path);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fromFile = new FileBackedTaskManager(Managers.getDefaultHistory(), file.getPath());
        StringBuilder stringFile = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fromFile.getFile()))) {
            while (reader.ready()) {
                stringFile.append(reader.readLine());
                stringFile.append(System.lineSeparator());
            }
            String[] lines = stringFile.toString().split(System.lineSeparator());
            List<Task> tasks = new ArrayList<>();

            for (int i = 1; i < lines.length; i++) {
                tasks.add(fromFile.fromString(lines[i]));
            }

            for (Task task : tasks) {
                switch (task.getTaskType()) {
                    case TASK -> fromFile.getTasks().put(task.getId(), task);
                    case SUBTASK -> fromFile.getSubTasks().put(task.getId(), (SubTask) task);
                    case EPIC_TASK -> fromFile.getEpicTasks().put(task.getId(), (EpicTask) task);
                }
            }
            fromFile.setNextId(tasks.size() + 1);

            fromFile.getSubTasks().values().forEach(subTask -> {
                int epicId = subTask.getEpicId();
                EpicTask epicTask = fromFile.getEpicTasks().get(epicId);
                epicTask.addSubTaskId(subTask.getId());
            });

            fromFile.getEpicTasks().values().forEach(epicTask -> {
                fromFile.updateEpicStartTime(epicTask.getId());
                fromFile.updateEpicDuration(epicTask.getId());
                fromFile.updateEpicEndTime(epicTask.getId());
            });

        } catch (IOException e) {
            throw new ManagerLoadException("Не удалось загрузить данные");
        }
        return fromFile;
    }

    @Override
    public void deleteAllTasksByType(TaskType taskType) {
        super.deleteAllTasksByType(taskType);
        save();
    }

    @Override
    public void deleteTaskByIdAndType(int id, TaskType taskType) {
        super.deleteTaskByIdAndType(id, taskType);
        save();
    }

    @Override
    public Task addTask(Task task) {
        Task newTask = super.addTask(task);
        save();
        return newTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeSubTaskInEpicTask(int id) {
        super.removeSubTaskInEpicTask(id);
        save();
    }

    private File getFile() {
        return file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : getTasks().values()) {
                writer.write(taskToString(task));
                writer.newLine();
            }
            for (SubTask subTask : getSubTasks().values()) {
                writer.write(taskToString(subTask));
                writer.newLine();
            }
            for (EpicTask epicTask : getEpicTasks().values()) {
                writer.write(taskToString(epicTask));
                writer.newLine();
            }
            writer.newLine();
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные");
        }
    }

    private String taskToString(Task task) {
        return task.toCSV();
    }

    private Task fromString(String str) {
        try {
            String[] element = str.split(",");
            int id = Integer.parseInt(element[0]);
            TaskType taskType = TaskType.valueOf(element[1]);
            String name = element[2];
            TaskStatus taskStatus = TaskStatus.valueOf(element[3]);
            String desc = element[4];
            Duration duration = Duration.ofMinutes(Long.parseLong(element[5]));
            LocalDateTime startTime = LocalDateTime.parse(element[6]);
            switch (taskType) {
                case TASK -> {
                    return new Task(id, name, desc, taskStatus, taskType, duration, startTime);
                }
                case SUBTASK -> {
                    int epicId = Integer.parseInt(element[7]);
                    return new SubTask(id, name, desc, taskStatus, epicId, taskType, duration, startTime);
                }
                case EPIC_TASK -> {
                    return new EpicTask(id, name, desc, taskStatus, taskType, duration, startTime);
                }
                default -> {
                    return null;
                }
            }
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
