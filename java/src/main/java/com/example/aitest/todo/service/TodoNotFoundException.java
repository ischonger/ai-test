package com.example.aitest.todo.service;

public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(String id) {
        super("Todo not found: " + id);
    }
}
