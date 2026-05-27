package com.example.aitest.todo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Sort;

import com.example.aitest.todo.domain.Todo;

@DataMongoTest
class TodoRepositoryTest {

    @Autowired
    private TodoRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void savesAndFindsTodo() {
        Instant now = Instant.parse("2026-05-27T10:00:00Z");
        Todo saved = repository.save(new Todo(null, "buy milk", false, now, now));

        assertThat(saved.getId()).isNotBlank();
        Optional<Todo> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("buy milk");
        assertThat(found.get().isCompleted()).isFalse();
        assertThat(found.get().getCreatedAt()).isEqualTo(now);
    }

    @Test
    void findAllSortedByCreatedAtDesc() {
        Instant earlier = Instant.parse("2026-05-27T09:00:00Z");
        Instant later = Instant.parse("2026-05-27T10:00:00Z");
        repository.save(new Todo(null, "old", false, earlier, earlier));
        repository.save(new Todo(null, "new", false, later, later));

        List<Todo> result = repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        assertThat(result).extracting(Todo::getTitle).containsExactly("new", "old");
    }

    @Test
    void existsAndDeletes() {
        Instant now = Instant.parse("2026-05-27T10:00:00Z");
        Todo saved = repository.save(new Todo(null, "x", false, now, now));

        assertThat(repository.existsById(saved.getId())).isTrue();
        repository.deleteById(saved.getId());
        assertThat(repository.existsById(saved.getId())).isFalse();
    }
}
