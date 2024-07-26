package ru.practicum.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.EpicTask;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {

    public final static int PORT = 8080;
    private HttpTaskServer server;
    private TaskManager taskManager;
    private final Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    void init() throws IOException {
        taskManager = new InMemoryTaskManger(new InMemoryHistoryManager());
        server = new HttpTaskServer(taskManager);
        taskManager.addTask(new Task("Уборка", "Протереть стол", TaskStatus.NEW, TaskType.TASK,
                Duration.ofMinutes(15), LocalDateTime.now()));
        EpicTask epicTask = (EpicTask) taskManager.addTask(new EpicTask("Эпик_1", "Сделать кофе",
                TaskStatus.NEW, TaskType.EPIC_TASK));
        taskManager.addTask(new SubTask("Включить чайник", "Налить кофе", TaskStatus.NEW,
                epicTask.getId(), TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now()));
    }

    @BeforeEach
    void start() {
        System.out.println("Starting server on port " + PORT);
        server.start();
    }

    @AfterEach
    void stop() {
        server.stop();
        System.out.println("Server stopped");
    }

    @Test
    void whenGetAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks");

        HttpResponse<String> responseTasks = sendTaskGet(client, uriTasks);
        List<Task> taskList = gson.fromJson(responseTasks.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        List<Task> actual = taskManager.getAllTasksByType(TaskType.TASK);

        assertEquals(200, responseTasks.statusCode());
        assertEquals(actual.size(), 1);
        assertEquals(actual, taskList);
    }

    @Test
    void whenAddNewTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks");
        Task taskToPost = new Task("Стирка", "Много вещей", TaskStatus.NEW, TaskType.TASK,
                Duration.ofMinutes(15), LocalDateTime.now());

        HttpResponse<String> responsePostTask = sendPostTask(client, uriTasks, taskToPost);
        HttpResponse<String> responseTasks = sendTaskGet(client, uriTasks);
        List<Task> taskList = gson.fromJson(responseTasks.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        List<Task> actual = taskManager.getAllTasksByType(TaskType.TASK);

        assertEquals(201, responsePostTask.statusCode());
        assertEquals(actual.size(), 2);
        assertEquals(actual, taskList);
    }

    @Test
    void whenDeleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/1");

        HttpResponse<String> responsePostTask = sendDeleteTask(client, uriTasks);
        URI uriAllTasks = URI.create("http://localhost:8080/tasks");
        HttpResponse<String> responseTasks = sendTaskGet(client, uriAllTasks);
        List<Task> taskList = gson.fromJson(responseTasks.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        List<Task> actual = taskManager.getAllTasksByType(TaskType.TASK);

        assertEquals(204, responsePostTask.statusCode());
        assertEquals(actual.size(), 0);
        assertEquals(actual, taskList);
    }

    private HttpResponse<String> sendTaskGet(HttpClient client, URI uriTasks) throws IOException, InterruptedException {
        HttpRequest requestTasks = HttpRequest.newBuilder().uri(uriTasks).GET().build();
        return client.send(requestTasks, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPostTask(HttpClient client, URI uriTasks, Task taskToPost) throws IOException,
            InterruptedException {
        String taskToPostGson = gson.toJson(taskToPost);
        HttpRequest postTask = HttpRequest.newBuilder()
                .uri(uriTasks)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskToPostGson))
                .build();
        return client.send(postTask, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteTask(HttpClient client, URI uriTasks) throws IOException,
            InterruptedException {
        HttpRequest deleteTask = HttpRequest.newBuilder()
                .uri(uriTasks)
                .DELETE()
                .build();
        return client.send(deleteTask, HttpResponse.BodyHandlers.ofString());
    }

}