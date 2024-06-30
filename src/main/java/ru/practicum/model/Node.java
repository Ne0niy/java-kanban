package ru.practicum.model;


public class Node<T> {
        public T element;
        public Node<T> next;
        public Node<T> prev;

        public Node(T element, Node<T> next, Node<T> prev) {
            this.element = element;
            this.next = next;
            this.prev = prev;
        }
}
