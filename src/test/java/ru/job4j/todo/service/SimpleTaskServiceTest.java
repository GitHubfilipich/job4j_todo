package ru.job4j.todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.todo.dto.TaskDTO;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.repository.task.Store;
import ru.job4j.todo.service.category.CategoryService;
import ru.job4j.todo.service.priority.PriorityService;
import ru.job4j.todo.service.task.SimpleTaskService;
import ru.job4j.todo.service.task.TaskService;
import ru.job4j.todo.service.user.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimpleTaskServiceTest {

    private Store store;
    private UserService userService;
    private TaskService service;
    private PriorityService priorityService;
    private CategoryService categoryService;
    private List<Task> tasks;
    private List<TaskDTO> taskDtos;

    @BeforeEach
    void setUp() {
        store = mock(Store.class);
        userService = mock(UserService.class);
        priorityService = mock(PriorityService.class);
        categoryService = mock(CategoryService.class);
        service = new SimpleTaskService(store, userService, priorityService, categoryService);
    }

    /**
     * Проверяет сценарий возврата данных всех заданий методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetAllTasksData() {
        addTasksAndDTO();
        when(store.findAll()).thenReturn(tasks);

        var actualtaskDtos = service.findAll();

        assertThat(actualtaskDtos).containsExactlyInAnyOrderElementsOf(taskDtos);
    }

    private void addTasksAndDTO() {
        var categories = List.of(new Category(1, "test1"), new Category(2, "test2"));
        tasks = List.of(new Task(1, "test1", "desc1", LocalDateTime.now(), true, new User(), new Priority(), List.of()),
                new Task(2, "test2", "desc2", LocalDateTime.now(), false, new User(), new Priority(), List.of(categories.get(0))),
                new Task(3, "test3", "desc", LocalDateTime.now(), true, new User(), new Priority(), List.of(categories.get(0), categories.get(1))));
        taskDtos = tasks.stream()
                .map(task -> new TaskDTO(
                        task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone(),
                        task.getUser().getId(), task.getUser().getName(), task.getPriority().getId(), task.getPriority().getName(),
                        task.getCategories().stream().map(Category::getId).toList(),
                        task.getCategories().stream()
                                .map(Category::getName)
                                .sorted()
                                .collect(Collectors.joining(", "))))
                .toList();
    }

    /**
     * Проверяет сценарий возврата данных выполненных заданий методом {@code findDone}
     */
    @Test
    void whenFindDoneThenGetDoneTasksData() {
        addTasksAndDTO();
        when(store.findDone()).thenReturn(tasks);

        var actualtaskDtos = service.findDone();

        assertThat(actualtaskDtos).containsExactlyInAnyOrderElementsOf(taskDtos);
    }

    /**
     * Проверяет сценарий возврата данных новых заданий методом {@code findNew}
     */
    @Test
    void whenFindNewThenGetNewTasksData() {
        addTasksAndDTO();
        when(store.findNew()).thenReturn(tasks);

        var actualtaskDtos = service.findNew();

        assertThat(actualtaskDtos).containsExactlyInAnyOrderElementsOf(taskDtos);
    }

    /**
     * Проверяет успешный сценарий возврата данных задания методом {@code findById}
     */
    @Test
    void whenFindByIdSuccessfulThenGetTaskData() {
        var id = 1;
        var task = new Task(id, "test1", "desc1", LocalDateTime.now(), false, new User(),
                new Priority(), List.of(new  Category(1, "test1")));
        var taskDto = new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone(),
                task.getUser().getId(), task.getUser().getName(), task.getPriority().getId(), task.getPriority().getName(),
                List.of(1), "test1");
        var intArgCaptor = ArgumentCaptor.forClass(Integer.class);
        when(store.findById(intArgCaptor.capture())).thenReturn(Optional.of(task));

        var actualtaskDto = service.findById(id);
        var actualId = intArgCaptor.getValue();

        assertThat(actualtaskDto).isNotEmpty();
        assertThat(actualtaskDto.get()).isEqualTo(taskDto);
        assertThat(actualId).isEqualTo(id);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных задания методом {@code findById}
     */
    @Test
    void whenFindByIdUnSuccessfulThenGetEmpty() {
        var id = 1;
        when(store.findById(any(Integer.class))).thenReturn(Optional.empty());

        var actualtaskDto = service.findById(id);

        assertThat(actualtaskDto).isEmpty();
    }

    /**
     * Проверяет успешный сценарий выполнения задания методом {@code setDoneById}
     */
    @Test
    void whenSetDoneByIdSuccessfulThenGetTrue() {
        var id = 1;
        var intArgCaptor = ArgumentCaptor.forClass(Integer.class);
        when(store.setDoneById(intArgCaptor.capture())).thenReturn(true);

        var actual = service.setDoneById(id);
        var actualId = intArgCaptor.getValue();

        assertThat(actual).isTrue();
        assertThat(actualId).isEqualTo(id);
    }

    /**
     * Проверяет неуспешный сценарий выполнения задания методом {@code setDoneById}
     */
    @Test
    void whenSetDoneByIdUnSuccessfulThenGetFalse() {
        var id = 1;
        when(store.setDoneById(any(Integer.class))).thenReturn(false);

        var actual = service.setDoneById(id);

        assertThat(actual).isFalse();
    }

    /**
     * Проверяет успешный сценарий удаления задания методом {@code deleteById}
     */
    @Test
    void whenDeleteByIdSuccessfulThenGetTrue() {
        var id = 1;
        var intArgCaptor = ArgumentCaptor.forClass(Integer.class);
        when(store.deleteById(intArgCaptor.capture())).thenReturn(true);

        var actual = service.deleteById(id);
        var actualId = intArgCaptor.getValue();

        assertThat(actual).isTrue();
        assertThat(actualId).isEqualTo(id);
    }

    /**
     * Проверяет неуспешный сценарий удаления задания методом {@code deleteById}
     */
    @Test
    void whenDeleteByIdUnSuccessfulThenGetFalse() {
        var id = 1;
        when(store.deleteById(any(Integer.class))).thenReturn(false);

        var actual = service.deleteById(id);

        assertThat(actual).isFalse();
    }

    /**
     * Проверяет успешный сценарий обновления задания методом {@code update}
     */
    @Test
    void whenUpdateSuccessfulThenGetTrue() {
        var id = 1;
        var user = new User();
        user.setId(id);
        var idPriority = 2;
        var priority = new Priority();
        priority.setId(idPriority);
        var category = new Category(1, "test1");
        var task = new Task(id, "test1", "desc1", LocalDateTime.now(), false, user, priority, List.of(category));
        var taskDto = new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone(),
                task.getUser().getId(), task.getUser().getName(), task.getPriority().getId(), task.getPriority().getName(),
                List.of(category.getId()), category.getName());
        var intArgCaptor = ArgumentCaptor.forClass(Integer.class);
        when(userService.findById(intArgCaptor.capture())).thenReturn(Optional.of(user));
        var intArgCaptorPriority = ArgumentCaptor.forClass(Integer.class);
        when(priorityService.findById(intArgCaptorPriority.capture())).thenReturn(Optional.of(priority));
        var taskArgCaptor = ArgumentCaptor.forClass(Task.class);
        when(store.update(taskArgCaptor.capture())).thenReturn(true);
        when(categoryService.findAll()).thenReturn(List.of(category));

        var actual = service.update(taskDto);
        var actualTask = taskArgCaptor.getValue();
        var actualId = intArgCaptor.getValue();
        var actualIdPriority = intArgCaptorPriority.getValue();

        assertThat(actual).isTrue();
        assertThat(actualTask).usingRecursiveComparison()
                .withComparatorForType(
                        Comparator.comparing(o -> o.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class)
                .isEqualTo(task);
        assertThat(actualId).isEqualTo(id);
        assertThat(actualIdPriority).isEqualTo(idPriority);
    }

    /**
     * Проверяет неуспешный сценарий обновления задания методом {@code update}
     */
    @Test
    void whenUpdateUnSuccessfulThenGetFalse() {
        var id = 1;
        var category = new Category(id, "test1");
        var task = new Task(id, "test1", "desc1", LocalDateTime.now(), false,
                new User(), new Priority(), List.of(category));
        var taskDto = new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone(),
                task.getUser().getId(), task.getUser().getName(), task.getPriority().getId(), task.getPriority().getName(),
                List.of(id), category.getName());
        when(store.update(any(Task.class))).thenReturn(false);

        var actual = service.update(taskDto);

        assertThat(actual).isFalse();
    }

    /**
     * Проверяет успешный сценарий сохранения задания методом {@code save}
     */
    @Test
    void whenSaveSuccessfulThenGetTrue() {
        var id = 1;
        var user = new User();
        user.setId(id);
        var idPriority = 2;
        var priority = new Priority();
        priority.setId(idPriority);
        var category = new Category(1, "test1");
        var task = new Task(id, "test1", "desc1", LocalDateTime.now(), false, user, priority, List.of(category));
        var taskDto = new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone(),
                task.getUser().getId(), task.getUser().getName(), task.getPriority().getId(), task.getPriority().getName(),
                List.of(category.getId()), category.getName());
        var intArgCaptor = ArgumentCaptor.forClass(Integer.class);
        when(userService.findById(intArgCaptor.capture())).thenReturn(Optional.of(user));
        var intArgCaptorPriority = ArgumentCaptor.forClass(Integer.class);
        when(priorityService.findById(intArgCaptorPriority.capture())).thenReturn(Optional.of(priority));
        var taskArgCaptor = ArgumentCaptor.forClass(Task.class);
        when(store.save(taskArgCaptor.capture())).thenReturn(true);
        when(categoryService.findAll()).thenReturn(List.of(category));

        var actual = service.save(taskDto);
        var actualTask = taskArgCaptor.getValue();
        var actualId = intArgCaptor.getValue();
        var actualIdPriority = intArgCaptorPriority.getValue();

        assertThat(actual).isTrue();
        assertThat(actualTask).usingRecursiveComparison()
                .withComparatorForType(
                        Comparator.comparing(o -> o.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class)
                .isEqualTo(task);
        assertThat(actualId).isEqualTo(id);
        assertThat(actualIdPriority).isEqualTo(idPriority);
    }

    /**
     * Проверяет неуспешный сценарий сохранения задания методом {@code save}
     */
    @Test
    void whenSaveUnSuccessfulThenGetFalse() {
        var id = 1;
        var category = new Category(id, "test1");
        var task = new Task(id, "test1", "desc1", LocalDateTime.now(), false, new User(), new Priority(), List.of(category));
        var taskDto = new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone(),
                task.getUser().getId(), task.getUser().getName(), task.getPriority().getId(), task.getPriority().getName(),
                List.of(category.getId()), category.getName());
        when(store.save(any(Task.class))).thenReturn(false);

        var actual = service.save(taskDto);

        assertThat(actual).isFalse();
    }
}