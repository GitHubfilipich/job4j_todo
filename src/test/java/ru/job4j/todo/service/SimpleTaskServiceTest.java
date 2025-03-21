package ru.job4j.todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.todo.dto.TaskDTO;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.repository.Store;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimpleTaskServiceTest {

    private Store store;
    private TaskService service;

    @BeforeEach
    void setUp() {
        store = mock(Store.class);
        service = new SimpleTaskService(store);
    }

    /**
     * Проверяет сценарий возврата данных всех заданий методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetAllTasksData() {
        var tasks = List.of(new Task(1, "test1", "desc1", LocalDateTime.now(), true),
                new Task(2, "test2", "desc2", LocalDateTime.now(), false),
                new Task(3, "test3", "desc", LocalDateTime.now(), true));
        var taskDtos = tasks.stream()
                .map(task -> new TaskDTO(
                        task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone()))
                .toList();
        when(store.findAll()).thenReturn(tasks);

        var actualtaskDtos = service.findAll();

        assertThat(actualtaskDtos).containsExactlyInAnyOrderElementsOf(taskDtos);
    }

    /**
     * Проверяет сценарий возврата данных выполненных заданий методом {@code findDone}
     */
    @Test
    void whenFindDoneThenGetDoneTasksData() {
        var tasks = List.of(new Task(1, "test1", "desc1", LocalDateTime.now(), true),
                new Task(2, "test2", "desc2", LocalDateTime.now(), true),
                new Task(3, "test3", "desc", LocalDateTime.now(), true));
        var taskDtos = tasks.stream()
                .map(task -> new TaskDTO(
                        task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone()))
                .toList();
        when(store.findDone()).thenReturn(tasks);

        var actualtaskDtos = service.findDone();

        assertThat(actualtaskDtos).containsExactlyInAnyOrderElementsOf(taskDtos);
    }

    /**
     * Проверяет сценарий возврата данных новых заданий методом {@code findNew}
     */
    @Test
    void whenFindNewThenGetNewTasksData() {
        var tasks = List.of(new Task(1, "test1", "desc1", LocalDateTime.now(), false),
                new Task(2, "test2", "desc2", LocalDateTime.now(), false),
                new Task(3, "test3", "desc", LocalDateTime.now(), false));
        var taskDtos = tasks.stream()
                .map(task -> new TaskDTO(
                        task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone()))
                .toList();
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
        var task = new Task(id, "test1", "desc1", LocalDateTime.now(), false);
        var taskDto = new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone());
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
        var task = new Task(id, "test1", "desc1", LocalDateTime.now(), false);
        var taskDto = new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone());
        var taskArgCaptor = ArgumentCaptor.forClass(Task.class);
        when(store.update(taskArgCaptor.capture())).thenReturn(true);

        var actual = service.update(taskDto);
        var actualTask = taskArgCaptor.getValue();

        assertThat(actual).isTrue();
        assertThat(actualTask).usingRecursiveComparison()
                .withComparatorForType(
                        Comparator.comparing(o -> o.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class)
                .isEqualTo(task);
    }

    /**
     * Проверяет неуспешный сценарий обновления задания методом {@code update}
     */
    @Test
    void whenUpdateUnSuccessfulThenGetFalse() {
        var id = 1;
        var task = new Task(id, "test1", "desc1", LocalDateTime.now(), false);
        var taskDto = new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone());
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
        var task = new Task(id, "test1", "desc1", LocalDateTime.now(), false);
        var taskDto = new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone());
        var taskArgCaptor = ArgumentCaptor.forClass(Task.class);
        when(store.save(taskArgCaptor.capture())).thenReturn(true);

        var actual = service.save(taskDto);
        var actualTask = taskArgCaptor.getValue();

        assertThat(actual).isTrue();
        assertThat(actualTask).usingRecursiveComparison()
                .withComparatorForType(
                        Comparator.comparing(o -> o.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class)
                .isEqualTo(task);
    }

    /**
     * Проверяет неуспешный сценарий сохранения задания методом {@code save}
     */
    @Test
    void whenSaveUnSuccessfulThenGetFalse() {
        var id = 1;
        var task = new Task(id, "test1", "desc1", LocalDateTime.now(), false);
        var taskDto = new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getCreated(), task.isDone());
        when(store.save(any(Task.class))).thenReturn(false);

        var actual = service.save(taskDto);

        assertThat(actual).isFalse();
    }
}