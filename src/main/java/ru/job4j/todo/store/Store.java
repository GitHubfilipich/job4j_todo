package ru.job4j.todo.store;

import ru.job4j.todo.model.Task;

import java.util.Collection;

public interface Store {
    Collection<Task> findAll();
}
