package com.example.aitest.todo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.aitest.todo.domain.Todo;

public interface TodoRepository extends MongoRepository<Todo, String> {
}
