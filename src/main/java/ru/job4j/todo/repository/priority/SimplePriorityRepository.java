package ru.job4j.todo.repository.priority;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@AllArgsConstructor
public class SimplePriorityRepository implements PriorityRepository {

    private final CrudRepository crudRepository;

    @Override
    public Optional<Priority> findById(int id) {
        try {
            return crudRepository.optional(session -> session.get(Priority.class, id));
        } catch (Exception e) {
            log.error("Ошибка получения приоритетов", e);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Priority> findAll() {
        try {
            return crudRepository.query("from Priority ORDER BY name", Priority.class);
        } catch (Exception e) {
            log.error("Ошибка получения приоритетов", e);
        }
        return List.of();
    }
}
