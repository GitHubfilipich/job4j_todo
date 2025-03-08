package ru.job4j.todo.dto;

import java.time.LocalDateTime;

public record TaskDTO(int id, String title, LocalDateTime created, boolean done) {

}
