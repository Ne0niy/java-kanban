package ru.practicum.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.handler.EpicHandler;
import ru.practicum.handler.HistoryHandler;
import ru.practicum.handler.PrioritizedHandler;
import ru.practicum.handler.SubtasksHandler;
import ru.practicum.handler.TaskHandler;
import ru.practicum.model.adapter.DurationAdapter;
import ru.practicum.model.adapter.LocalDateTimeAdapter;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    public final int port = 8080;
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        this(FileBackedTaskManager.loadFromFile(new File("./resource/file.csv")));
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        Gson gson = getGson();
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/tasks", new TaskHandler(taskManager, gson));
        server.createContext("/epics", new EpicHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public void start() {
        System.out.println("Starting server on port " + port);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped");
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }
}