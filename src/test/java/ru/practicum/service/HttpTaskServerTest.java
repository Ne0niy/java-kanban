package ru.practicum.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.EpicTask;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.adapter.DurationAdapter;
import ru.practicum.model.adapter.LocalDateTimeAdapter;
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
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private Task task;
    private SubTask subTask;
    private EpicTask epicTask;

    @BeforeEach
    void init() throws IOException {
        taskManager = new InMemoryTaskManger(new InMemoryHistoryManager());
        server = new HttpTaskServer(taskManager);
        task = taskManager.addTask(new Task("Уборка", "Протереть стол", TaskStatus.NEW, TaskType.TASK,
                Duration.ofMinutes(15), LocalDateTime.now()));
        epicTask = (EpicTask) taskManager.addTask(new EpicTask("Эпик_1", "Сделать кофе",
                TaskStatus.NEW, TaskType.EPIC_TASK));
        subTask = (SubTask) taskManager.addTask(new SubTask("Включить чайник", "Налить кофе", TaskStatus.NEW,
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
        List<Task> actual = gson.fromJson(responseTasks.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        List<Task> expected = taskManager.getAllTasksByType(TaskType.TASK);

        assertEquals(200, responseTasks.statusCode());
        assertEquals(expected.size(), 1);
        assertEquals(expected, actual);
    }

    @Test
    void whenGetAllSubTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/subtasks");

        HttpResponse<String> responseTasks = sendTaskGet(client, uriTasks);
        List<SubTask> actual = gson.fromJson(responseTasks.body(), new TypeToken<ArrayList<SubTask>>() {
        }.getType());
        List<Task> expected = taskManager.getAllTasksByType(TaskType.SUBTASK);

        assertEquals(200, responseTasks.statusCode());
        assertEquals(expected.size(), 1);
        assertEquals(expected, actual);
    }

    @Test
    void whenGetAllEpicTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/epics");

        HttpResponse<String> responseTasks = sendTaskGet(client, uriTasks);
        List<EpicTask> actual = gson.fromJson(responseTasks.body(), new TypeToken<ArrayList<EpicTask>>() {
        }.getType());
        List<Task> expected = taskManager.getAllTasksByType(TaskType.EPIC_TASK);

        assertEquals(200, responseTasks.statusCode());
        assertEquals(expected.size(), 1);
        assertEquals(expected, actual);
    }

    @Test
    void whenGetTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/" + task.getId());

        HttpResponse<String> responseTasks = sendTaskGet(client, uriTasks);
        Task actual = gson.fromJson(responseTasks.body(), new TypeToken<Task>() {
        }.getType());

        assertEquals(200, responseTasks.statusCode());
        assertEquals(task, actual);
    }

    @Test
    void whenGetSubTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/subtasks/" + subTask.getId());

        HttpResponse<String> responseTasks = sendTaskGet(client, uriTasks);
        SubTask actual = gson.fromJson(responseTasks.body(), new TypeToken<SubTask>() {
        }.getType());

        assertEquals(200, responseTasks.statusCode());
        assertEquals(subTask, actual);
    }

    @Test
    void whenGetEpicTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/epics/" + epicTask.getId());

        HttpResponse<String> responseTasks = sendTaskGet(client, uriTasks);
        EpicTask actual = gson.fromJson(responseTasks.body(), new TypeToken<EpicTask>() {
        }.getType());

        assertEquals(200, responseTasks.statusCode());
        assertEquals(epicTask, actual);
    }

    @Test
    void whenAddNewTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks");
        Task taskToPost = new Task("Стирка", "Много вещей", TaskStatus.NEW, TaskType.TASK,
                Duration.ofMinutes(15), LocalDateTime.now());

        HttpResponse<String> responsePostTask = sendPostTask(client, uriTasks, taskToPost);
        List<Task> actual = taskManager.getAllTasksByType(TaskType.TASK);

        assertEquals(201, responsePostTask.statusCode());
        assertEquals(2, actual.size());
    }

    @Test
    void whenAddNewSubTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/subtasks");
        SubTask taskToPost = new SubTask("Стирка", "Много вещей", TaskStatus.NEW, epicTask.getId(),
                TaskType.SUBTASK, Duration.ofMinutes(15), LocalDateTime.now());

        HttpResponse<String> responsePostTask = sendPostTask(client, uriTasks, taskToPost);
        List<Task> actual = taskManager.getAllTasksByType(TaskType.SUBTASK);

        assertEquals(201, responsePostTask.statusCode());
        assertEquals(2, actual.size());
    }

    @Test
    void whenAddNewEpicTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/epics");
        EpicTask taskToPost = new EpicTask("Стирка", "Много вещей", TaskStatus.NEW,
                TaskType.EPIC_TASK, Duration.ofMinutes(15), LocalDateTime.now());

        HttpResponse<String> responsePostTask = sendPostTask(client, uriTasks, taskToPost);
        List<Task> actual = taskManager.getAllTasksByType(TaskType.EPIC_TASK);

        assertEquals(201, responsePostTask.statusCode());
        assertEquals(2, actual.size());
    }

    @Test
    void whenDeleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/" + task.getId());

        HttpResponse<String> responsePostTask = sendDeleteTask(client, uriTasks);
        List<Task> expected = taskManager.getAllTasksByType(TaskType.TASK);

        assertEquals(204, responsePostTask.statusCode());
        assertEquals(expected.size(), 0);
    }

    @Test
    void whenDeleteSubTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/subtasks/" + subTask.getId());

        HttpResponse<String> responsePostTask = sendDeleteTask(client, uriTasks);
        List<Task> expected = taskManager.getAllTasksByType(TaskType.SUBTASK);

        assertEquals(204, responsePostTask.statusCode());
        assertEquals(expected.size(), 0);
    }

    @Test
    void whenDeleteEpicTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Task epic = taskManager.addTask(new EpicTask("Стирка", "Много вещей", TaskStatus.NEW,
                TaskType.EPIC_TASK));
        URI uriTasks = URI.create("http://localhost:8080/epics/" + epic.getId());

        HttpResponse<String> responsePostTask = sendDeleteTask(client, uriTasks);
        List<Task> expected = taskManager.getAllTasksByType(TaskType.EPIC_TASK);

        assertEquals(204, responsePostTask.statusCode());
        assertEquals(expected.size(), 1);
    }

    @Test
    void whenGetHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/history/");
        taskManager.getTaskByIdAndType(task.getId(), TaskType.TASK);
        taskManager.getTaskByIdAndType(subTask.getId(), TaskType.SUBTASK);

        HttpResponse<String> responseTasks = sendTaskGet(client, uriTasks);
        List<Task> actual = gson.fromJson(responseTasks.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        assertEquals(200, responseTasks.statusCode());
        assertEquals(2, actual.size());
    }

    @Test
    void whenGetPrioritized() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/prioritized/");

        HttpResponse<String> responseTasks = sendTaskGet(client, uriTasks);
        List<Task> actual = gson.fromJson(responseTasks.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        assertEquals(200, responseTasks.statusCode());
        assertEquals(1, actual.size());
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