package ru.job4j.todo.service.category;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.repository.category.CategoryRepository;

import java.util.Collection;

@Service
@AllArgsConstructor
public class SimpleCategoryService implements CategoryService {
    private final CategoryRepository repository;

    @Override
    public Collection<Category> findAll() {
        return repository.findAll();
    }
}
