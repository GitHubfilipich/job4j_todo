package ru.job4j.todo.service;

import org.springframework.stereotype.Controller;
import ru.job4j.todo.dto.TaskDTO;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.repository.Store;

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
                .map(SimpleTaskService::taskToTaskDto);
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
    public boolean update(TaskDTO task) {
        return store.update(taskDtoToTask(task));
    }

    @Override
    public boolean save(TaskDTO task) {
        return store.save(taskDtoToTask(task));
    }

    private List<TaskDTO> taskCollectionToTaskDtoCollection(Collection<Task> taskCollection) {
        return taskCollection.stream()
                .map(SimpleTaskService::taskToTaskDto)
                .toList();
    }

    private static TaskDTO taskToTaskDto(Task task) {
        return new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone());
    }

    private static Task taskDtoToTask(TaskDTO task) {
        return new Task(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone());
    }
}
