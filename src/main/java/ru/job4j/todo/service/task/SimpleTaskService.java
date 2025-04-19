package ru.job4j.todo.service.task;

import org.springframework.stereotype.Controller;
import ru.job4j.todo.dto.TaskDTO;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.repository.task.Store;
import ru.job4j.todo.service.category.CategoryService;
import ru.job4j.todo.service.priority.PriorityService;
import ru.job4j.todo.service.user.UserService;

import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class SimpleTaskService implements TaskService {
    private final Store store;
    private final UserService userService;
    private final PriorityService priorityService;
    private final CategoryService categoryService;

    public SimpleTaskService(Store store, UserService userService, PriorityService priorityService, CategoryService categoryService) {
        this.store = store;
        this.userService = userService;
        this.priorityService = priorityService;
        this.categoryService = categoryService;
    }

    @Override
    public Collection<TaskDTO> findAll(User user) {
        return taskCollectionToTaskDtoCollection(store.findAll(), user);
    }

    @Override
    public Collection<TaskDTO> findDone(User user) {
        return taskCollectionToTaskDtoCollection(store.findDone(), user);
    }

    @Override
    public Collection<TaskDTO> findNew(User user) {
        return taskCollectionToTaskDtoCollection(store.findNew(), user);
    }

    @Override
    public Optional<TaskDTO> findById(int id, User user) {
        return store.findById(id)
                .map(task -> this.taskToTaskDto(task, user));
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

    private List<TaskDTO> taskCollectionToTaskDtoCollection(Collection<Task> taskCollection, User user) {
        return taskCollection.stream()
                .map(task -> taskToTaskDto(task, user))
                .toList();
    }

    private TaskDTO taskToTaskDto(Task task, User user) {
        Priority priority = task.getPriority();
        return new TaskDTO(task.getId(), task.getTitle(), task.getDescription(),
                task.getCreated().atZone(ZoneId.of("UTC")).
                        withZoneSameInstant(user.getTimezone() != null ? ZoneId.of(user.getTimezone()) : TimeZone.getDefault().toZoneId())
                        .toLocalDateTime(),
                task.isDone(), task.getUser().getId(), task.getUser().getName(),
                priority != null ? priority.getId() : 0, priority != null ? priority.getName() : "",
                task.getCategories().stream().map(Category::getId).toList(),
                 task.getCategories().stream()
                         .map(Category::getName)
                         .sorted()
                         .collect(Collectors.joining(", ")));
    }

    private Task taskDtoToTask(TaskDTO task) {
        Map<Integer, Category> categoryMap = categoryService.findAll().stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));
        return new Task(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone(),
                userService.findById(task.getUserId()).orElse(null),
                priorityService.findById(task.getPriorityId()).orElse(null),
                task.getCategoriesId().stream()
                        .map(categoryMap::get)
                        .filter(Objects::nonNull).toList());
    }
}
