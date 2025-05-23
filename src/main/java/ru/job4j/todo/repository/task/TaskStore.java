package ru.job4j.todo.repository.task;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@AllArgsConstructor
public class TaskStore implements Store {

    private final CrudRepository crudRepository;

    @Override
    public Collection<Task> findAll() {
        try {
            return crudRepository.query("SELECT DISTINCT t from Task t LEFT JOIN FETCH t.priority LEFT JOIN FETCH t.categories ORDER BY t.created", Task.class);
        } catch (Exception e) {
            log.error("Ошибка получения заданий", e);
        }
        return List.of();
    }

    @Override
    public Collection<Task> findDone() {
        try {
            return crudRepository.query("SELECT DISTINCT t from Task t LEFT JOIN FETCH t.priority LEFT JOIN FETCH t.categories WHERE done = true ORDER BY t.created", Task.class);
        } catch (Exception e) {
            log.error("Ошибка получения заданий", e);
        }
        return List.of();
    }

    @Override
    public Collection<Task> findNew() {
        try {
            return crudRepository.query("SELECT DISTINCT t from Task t LEFT JOIN FETCH t.priority LEFT JOIN FETCH t.categories WHERE done = false ORDER BY t.created", Task.class);
        } catch (Exception e) {
            log.error("Ошибка получения заданий", e);
        }
        return List.of();
    }

    @Override
    public Optional<Task> findById(int id) {
        try {
            return crudRepository.optional("SELECT DISTINCT t from Task t LEFT JOIN FETCH t.priority LEFT JOIN FETCH t.categories WHERE t.id = :id", Task.class,
                    Map.of("id", id));
        } catch (Exception e) {
            log.error("Ошибка получения заданий", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean setDoneById(int id) {
        try {
            return crudRepository.updateQuery("UPDATE Task SET done = true WHERE id = :id", Map.of("id", id));
        } catch (Exception e) {
            log.error("Ошибка обновления заданий", e);
        }
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        try {
            return crudRepository.updateQuery("DELETE Task WHERE id = :id", Map.of("id", id));
        } catch (Exception e) {
            log.error("Ошибка удаления заданий", e);
        }
        return false;
    }

    @Override
    public boolean update(Task task) {
        try {
            crudRepository.run(session -> session.update(task));
            return true;
        } catch (Exception e) {
            log.error("Ошибка обновления заданий", e);
        }
        return false;
    }

    @Override
    public boolean save(Task task) {
        try {
            crudRepository.run(session -> session.persist(task));
            return true;
        } catch (Exception e) {
            log.error("Ошибка сохранения заданий", e);
        }
        return false;
    }
}
