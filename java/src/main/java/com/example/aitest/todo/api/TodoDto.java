package com.example.aitest.todo.api;

import java.time.Instant;

import com.example.aitest.todo.domain.Todo;

public record TodoDto(
        String id,
        String title,
        boolean completed,
        Instant createdAt,
        Instant updatedAt) {

    public static TodoDto from(Todo todo) {
        return new TodoDto(
                todo.getId(),
                todo.getTitle(),
                todo.isCompleted(),
                todo.getCreatedAt(),
                todo.getUpdatedAt());
    }
}
