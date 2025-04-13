package ru.job4j.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private int id;
    private String title;
    private String description;
    private LocalDateTime created;
    private boolean done;
    private int userId;
    private String userName;
    private int priorityId;
    private String priority;
    private List<Integer> categoriesId;
    private String categoriesName;
}
