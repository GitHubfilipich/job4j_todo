package ru.job4j.todo.repository.category;

import ru.job4j.todo.model.Category;

import java.util.Collection;

public interface CategoryRepository {
    Collection<Category> findAll();
}
