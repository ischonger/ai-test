package com.example.aitest.todo.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.aitest.todo.domain.Todo;
import com.example.aitest.todo.service.TodoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public List<TodoDto> list() {
        return service.findAll().stream().map(TodoDto::from).toList();
    }

    @GetMapping("/{id}")
    public TodoDto get(@PathVariable String id) {
        return TodoDto.from(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<TodoDto> create(@Valid @RequestBody CreateTodoRequest request) {
        Todo created = service.create(request.title());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(TodoDto.from(created));
    }

    @PutMapping("/{id}")
    public TodoDto update(@PathVariable String id, @Valid @RequestBody UpdateTodoRequest request) {
        Todo updated = service.update(id, request.title(), request.completed());
        return TodoDto.from(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
