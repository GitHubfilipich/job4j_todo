package ru.job4j.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private int id;
    private String title;
    private String description;
    private LocalDateTime created;
    private boolean done;
}
