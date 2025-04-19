package ru.job4j.todo.service.task;

import ru.job4j.todo.dto.TaskDTO;
import ru.job4j.todo.model.User;

import java.util.Collection;
import java.util.Optional;

public interface TaskService {
    Collection<TaskDTO> findAll(User user);

    Collection<TaskDTO> findDone(User user);

    Collection<TaskDTO> findNew(User user);

    Optional<TaskDTO> findById(int id, User user);

    boolean setDoneById(int id);

    boolean deleteById(int id);

    boolean update(TaskDTO task);

    boolean save(TaskDTO task);
}
