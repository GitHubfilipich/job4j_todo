package ru.job4j.todo.store;

import lombok.AllArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
@AllArgsConstructor
public class TaskStore implements Store {
    private final SessionFactory sf;

    @Override
    public Collection<Task> findAll() {
        // TODO do real func
        return List.of(new Task(1, "task 1", "This is task 1", LocalDateTime.now(), true),
                new Task(1, "task 2", "This is task 2", LocalDateTime.now(), false));
    }
}
