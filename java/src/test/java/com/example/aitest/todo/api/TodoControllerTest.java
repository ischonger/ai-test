package com.example.aitest.todo.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.aitest.todo.domain.Todo;
import com.example.aitest.todo.service.TodoNotFoundException;
import com.example.aitest.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TodoController.class)
@Import(GlobalExceptionHandler.class)
class TodoControllerTest {

    private static final Instant NOW = Instant.parse("2026-05-27T10:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TodoService service;

    @Test
    void list_returnsAllTodos() throws Exception {
        when(service.findAll()).thenReturn(List.of(
                new Todo("1", "first", false, NOW, NOW),
                new Todo("2", "second", true, NOW, NOW)));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].title").value("first"))
                .andExpect(jsonPath("$[1].completed").value(true));
    }

    @Test
    void get_returnsTodo() throws Exception {
        when(service.findById("1")).thenReturn(new Todo("1", "first", false, NOW, NOW));

        mockMvc.perform(get("/api/todos/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("first"));
    }

    @Test
    void get_returns404_whenMissing() throws Exception {
        when(service.findById("missing")).thenThrow(new TodoNotFoundException("missing"));

        mockMvc.perform(get("/api/todos/{id}", "missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("missing")));
    }

    @Test
    void create_returns201WithLocation() throws Exception {
        when(service.create("buy milk"))
                .thenReturn(new Todo("abc", "buy milk", false, NOW, NOW));

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateTodoRequest("buy milk"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/todos/abc")))
                .andExpect(jsonPath("$.id").value("abc"))
                .andExpect(jsonPath("$.title").value("buy milk"));
    }

    @Test
    void create_returns400_whenTitleBlank() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("title"));
    }

    @Test
    void update_returnsUpdatedTodo() throws Exception {
        when(service.update(eq("1"), eq("done thing"), eq(true)))
                .thenReturn(new Todo("1", "done thing", true, NOW, NOW));

        mockMvc.perform(put("/api/todos/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateTodoRequest("done thing", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("done thing"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void update_returns404_whenMissing() throws Exception {
        when(service.update(eq("missing"), any(), anyBoolean()))
                .thenThrow(new TodoNotFoundException("missing"));

        mockMvc.perform(put("/api/todos/{id}", "missing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateTodoRequest("x", false))))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/todos/{id}", "1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(service, times(1)).delete("1");
    }

    @Test
    void delete_returns404_whenMissing() throws Exception {
        doThrow(new TodoNotFoundException("missing")).when(service).delete("missing");

        mockMvc.perform(delete("/api/todos/{id}", "missing"))
                .andExpect(status().isNotFound());
    }
}
