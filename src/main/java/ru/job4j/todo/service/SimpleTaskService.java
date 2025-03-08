package ru.job4j.todo.service;

import org.springframework.stereotype.Controller;
import ru.job4j.todo.dto.TaskDTO;
import ru.job4j.todo.store.Store;

import java.util.Collection;

@Controller
public class SimpleTaskService implements TaskService {
    private final Store taskStore;

    public SimpleTaskService(Store taskStore) {
        this.taskStore = taskStore;
    }

    @Override
    public Collection<TaskDTO> findAll() {
        return taskStore.findAll()
                .stream()
                .map(task -> new TaskDTO(task.getId(), task.getTitle(), task.getCreated(), task.isDone()))
                .toList();
    }
}
