package com.example.aitest.todo.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.aitest.todo.domain.Todo;
import com.example.aitest.todo.repository.TodoRepository;

@Service
public class TodoService {

    private final TodoRepository repository;
    private final Clock clock;

    public TodoService(TodoRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public List<Todo> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Todo findById(String id) {
        return repository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
    }

    public Todo create(String title) {
        Instant now = Instant.now(clock);
        Todo todo = new Todo(null, title.trim(), false, now, now);
        return repository.save(todo);
    }

    public Todo update(String id, String title, boolean completed) {
        Todo existing = findById(id);
        existing.setTitle(title.trim());
        existing.setCompleted(completed);
        existing.setUpdatedAt(Instant.now(clock));
        return repository.save(existing);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
