package ru.practicum;

import ru.practicum.service.HttpTaskServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }

}
