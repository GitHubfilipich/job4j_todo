package ru.job4j.todo.service;

import ru.job4j.todo.dto.TaskDTO;

import java.util.Collection;

public interface TaskService {
    Collection<TaskDTO> findAll();
}
