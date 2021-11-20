package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    private static class Entry<T> {
        private final T value;
        private Entry<T> next;

        Entry(T value) {
            this(value, null);
        }

        Entry(T value, Entry<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    private int stackSize;
    private Entry<T> head;

    public StackImpl() {
        stackSize = 0;
    }

    @Override

    public void push(T element) {
        Entry<T> next = head;
        head = new Entry<>(element);
        head.next = next;
        stackSize++;
    }

    @Override
    public T pop() {
        if (head == null) {
            return null;
        }
        T result = head.value;
        head = head.next;
        stackSize--;
        return result;
    }

    @Override
    public T peek() {
        if (head == null) {
            return null;
        }

        return head.value;
    }

    @Override
    public int size() {
        return stackSize;
    }
}
