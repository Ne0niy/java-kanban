package ru.practicum.service;

import ru.practicum.model.Node;
import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> history = new CustomLinkedList<>();

    private final HashMap<Integer, Node<Task>> historyTaskMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (historyTaskMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        historyTaskMap.put(task.getId(), history.linkLast(task));
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void remove(int id) {
        history.removeNode(historyTaskMap.get(id));
        historyTaskMap.remove(id);
    }
    @Override
    public int size() {
        return historyTaskMap.size();
    }
}

class CustomLinkedList<T> {

    private Node<T> head;
    private Node<T> tail;

    public Node<T> linkLast(T element) {
        Node<T> newElement = new Node<>(element, null, null);
        if (head == null) {
            head = newElement;
        } else {
            tail.next = newElement;
            newElement.prev = tail;
        }
        tail = newElement;

        return newElement;
    }
 
    public List<T> getTasks() {
        List<T> tasks = new ArrayList<>();
        Node<T> stepNode = head;
        while (stepNode != null) {
            tasks.add(stepNode.element);
            stepNode = stepNode.next;
        }

        return tasks;
    }

    public void removeNode(Node<T> node) {

        if (node == head && node == tail) {
            head = null;
            tail = null;
            return;
        }

        if (node == head) {
            head = node.next;
            node.next.prev = null;
            node.next = null;
            return;
        }

        if (node == tail) {
            tail = node.prev;
            node.prev.next = null;
            node.prev = null;
            return;
        }
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.prev = null;
        node.next = null;
    }
}
