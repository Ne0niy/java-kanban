package ru.practicum.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.model.SubTask;
import ru.practicum.model.enums.TaskType;
import ru.practicum.service.TaskManager;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        try {
            switch (method) {
                case "GET" -> {
                    if (pathParts.length == 3) {
                        handleGetSubtaskById(exchange, pathParts[2]);
                    } else if (pathParts.length == 2) {
                        handleGetAllSubtasks(exchange);
                    } else {
                        sendNotFound(exchange);
                    }
                }
                case "POST" -> handlePostSubtask(exchange);
                case "DELETE" -> {
                    if (pathParts.length == 3) {
                        handleDeleteSubtaskById(exchange, pathParts[2]);
                    } else {
                        sendNotFound(exchange);
                    }
                }
                default -> sendText(exchange, "Метод не поддерживается", 400);
            }
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) {
        try {
            String response = gson.toJson(taskManager.getAllTasksByType(TaskType.SUBTASK));
            sendText(exchange, response, 200);
        } catch (Exception e) {
            e.printStackTrace();
            sendNotFound(exchange);
        }

    }

    private void handleGetSubtaskById(HttpExchange exchange, String taskIdStr) {
        try {
            int subTaskId = Integer.parseInt(taskIdStr);
            SubTask subTask = (SubTask) taskManager.getTaskByIdAndType(subTaskId, TaskType.SUBTASK);
            if (subTask == null) {
                sendNotFound(exchange);
            } else {
                String response = gson.toJson(subTask);
                sendText(exchange, response, 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendNotFound(exchange);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) {
        try {
            InputStreamReader inputReader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            SubTask task = gson.fromJson(inputReader, SubTask.class);
            taskManager.addTask(task);
            sendText(exchange, "Задача добавлена", 201);
        } catch (Exception e) {
            e.printStackTrace();
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteSubtaskById(HttpExchange exchange, String taskIdStr) {
        try {
            int subTaskId = Integer.parseInt(taskIdStr);
            taskManager.deleteTaskByIdAndType(subTaskId, TaskType.SUBTASK);
            sendText(exchange, "Задача удалена", 204);
        } catch (Exception e) {
            e.printStackTrace();
            sendNotFound(exchange);
        }
    }

}