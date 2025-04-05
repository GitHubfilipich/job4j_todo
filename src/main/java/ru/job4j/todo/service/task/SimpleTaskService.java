package ru.job4j.todo.service.task;

import org.springframework.stereotype.Controller;
import ru.job4j.todo.dto.TaskDTO;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.repository.task.Store;
import ru.job4j.todo.service.priority.PriorityService;
import ru.job4j.todo.service.user.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
public class SimpleTaskService implements TaskService {
    private final Store store;
    private final UserService userService;
    private final PriorityService priorityService;

    public SimpleTaskService(Store store, UserService userService, PriorityService priorityService) {
        this.store = store;
        this.userService = userService;
        this.priorityService = priorityService;
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
                .map(this::taskToTaskDto);
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
                .map(this::taskToTaskDto)
                .toList();
    }

    private TaskDTO taskToTaskDto(Task task) {
        Priority priority = task.getPriority();
        return new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone(),
                task.getUser().getId(), task.getUser().getName(), priority != null ? priority.getId() : 0,
                priority != null ? priority.getName() : "");
    }

    private Task taskDtoToTask(TaskDTO task) {
        return new Task(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone(),
                userService.findById(task.getUserId()).orElse(null),
                priorityService.findById(task.getPriorityId()).orElse(null));
    }
}
