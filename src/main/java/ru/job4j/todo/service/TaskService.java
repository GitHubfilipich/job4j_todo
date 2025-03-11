package ru.job4j.todo.service;

import ru.job4j.todo.dto.TaskDTO;
import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Optional;

public interface TaskService {
    Collection<TaskDTO> findAll();

    Collection<TaskDTO> findDone();

    Collection<TaskDTO> findNew();

    Optional<TaskDTO> findById(int id);

    boolean setDoneById(int id);

    boolean deleteById(int id);

    boolean update(Task task);

    boolean save(Task task);
}
