package com.example.aitest.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.example.aitest.todo.domain.Todo;
import com.example.aitest.todo.repository.TodoRepository;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    private static final Instant FIXED_NOW = Instant.parse("2026-05-27T10:00:00Z");

    @Mock
    private TodoRepository repository;

    private TodoService service;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        service = new TodoService(repository, fixedClock);
    }

    @Test
    void findAll_returnsAllSortedByCreatedAtDesc() {
        Todo a = new Todo("1", "a", false, FIXED_NOW, FIXED_NOW);
        when(repository.findAll(any(Sort.class))).thenReturn(List.of(a));

        List<Todo> result = service.findAll();

        assertThat(result).containsExactly(a);
        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(repository).findAll(sortCaptor.capture());
        assertThat(sortCaptor.getValue().getOrderFor("createdAt")).isNotNull();
        assertThat(sortCaptor.getValue().getOrderFor("createdAt").isDescending()).isTrue();
    }

    @Test
    void findById_returnsTodo_whenPresent() {
        Todo todo = new Todo("1", "buy milk", false, FIXED_NOW, FIXED_NOW);
        when(repository.findById("1")).thenReturn(Optional.of(todo));

        assertThat(service.findById("1")).isSameAs(todo);
    }

    @Test
    void findById_throws_whenMissing() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("missing"))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("missing");
    }

    @Test
    void create_trimsTitleAndSetsTimestamps() {
        when(repository.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        Todo result = service.create("  walk dog  ");

        assertThat(result.getTitle()).isEqualTo("walk dog");
        assertThat(result.isCompleted()).isFalse();
        assertThat(result.getCreatedAt()).isEqualTo(FIXED_NOW);
        assertThat(result.getUpdatedAt()).isEqualTo(FIXED_NOW);
    }

    @Test
    void update_changesTitleAndCompletedAndBumpsUpdatedAt() {
        Instant earlier = FIXED_NOW.minusSeconds(3600);
        Todo existing = new Todo("1", "old", false, earlier, earlier);
        when(repository.findById("1")).thenReturn(Optional.of(existing));
        when(repository.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        Todo result = service.update("1", "  new title ", true);

        assertThat(result.getTitle()).isEqualTo("new title");
        assertThat(result.isCompleted()).isTrue();
        assertThat(result.getCreatedAt()).isEqualTo(earlier);
        assertThat(result.getUpdatedAt()).isEqualTo(FIXED_NOW);
    }

    @Test
    void update_throws_whenMissing() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update("missing", "x", false))
                .isInstanceOf(TodoNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_removes_whenPresent() {
        when(repository.existsById("1")).thenReturn(true);

        service.delete("1");

        verify(repository, times(1)).deleteById("1");
    }

    @Test
    void delete_throws_whenMissing() {
        when(repository.existsById("missing")).thenReturn(false);

        assertThatThrownBy(() -> service.delete("missing"))
                .isInstanceOf(TodoNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }
}
