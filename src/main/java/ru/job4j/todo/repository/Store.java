package ru.job4j.todo.repository;

import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Optional;

public interface Store {
    Collection<Task> findAll();

    Collection<Task> findDone();

    Collection<Task> findNew();

    Optional<Task> findById(int id);

    boolean setDoneById(int id);

    boolean deleteById(int id);

    boolean update(Task task);

    boolean save(Task task);
}
