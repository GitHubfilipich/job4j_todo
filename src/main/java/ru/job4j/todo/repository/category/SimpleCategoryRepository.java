package ru.job4j.todo.repository.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

@Repository
@AllArgsConstructor
@Slf4j
public class SimpleCategoryRepository implements CategoryRepository {
    private final CrudRepository crudRepository;

    @Override
    public Collection<Category> findAll() {
        try {
            return crudRepository.query("from Category ORDER BY name", Category.class);
        } catch (Exception e) {
            log.error("Ошибка получения категорий", e);
        }
        return List.of();
    }
}
