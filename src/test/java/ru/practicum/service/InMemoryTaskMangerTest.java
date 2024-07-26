package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.EpicTask;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.enums.TaskStatus;
import ru.practicum.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskMangerTest extends TaskMangerTest<InMemoryTaskManger> {

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManger(new InMemoryHistoryManager());
    }

}