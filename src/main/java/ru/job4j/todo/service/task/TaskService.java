package ru.job4j.todo.service.task;

import ru.job4j.todo.dto.TaskDTO;

import java.util.Collection;
import java.util.Optional;

public interface TaskService {
    Collection<TaskDTO> findAll();

    Collection<TaskDTO> findDone();

    Collection<TaskDTO> findNew();

    Optional<TaskDTO> findById(int id);

    boolean setDoneById(int id);

    boolean deleteById(int id);

    boolean update(TaskDTO task);

    boolean save(TaskDTO task);
}
