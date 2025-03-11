package ru.job4j.todo.service;

import org.springframework.stereotype.Controller;
import ru.job4j.todo.dto.TaskDTO;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.store.Store;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
public class SimpleTaskService implements TaskService {
    private final Store store;

    public SimpleTaskService(Store store) {
        this.store = store;
    }

    @Override
    public Collection<TaskDTO> findAll() {
        return taskCollectionToTaskDtoCollection(store.findAll());
    }

    @Override
    public Collection<TaskDTO> findDone() {
        return taskCollectionToTaskDtoCollection(store.findDone());
    }

    @Override
    public Collection<TaskDTO> findNew() {
        return taskCollectionToTaskDtoCollection(store.findNew());
    }

    @Override
    public Optional<TaskDTO> findById(int id) {
        return store.findById(id)
                .map(task -> new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone()));
    }

    @Override
    public boolean setDoneById(int id) {
        return store.setDoneById(id);
    }

    @Override
    public boolean deleteById(int id) {
        return store.deleteById(id);
    }

    @Override
    public boolean update(Task task) {
        return store.update(task);
    }

    @Override
    public boolean save(Task task) {
        return store.save(task);
    }

    private List<TaskDTO> taskCollectionToTaskDtoCollection(Collection<Task> taskCollection) {
        return taskCollection.stream()
                .map(task -> new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone()))
                .toList();
    }
}
