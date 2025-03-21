package ru.job4j.todo.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.todo.model.Task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class TaskStoreTest {

    public static SessionFactory sf;
    private static Store store;
    private static List<Task> tasks;

    @BeforeAll
    public static void setUp(@Autowired SessionFactory awSf) {
        sf = awSf;
        store = new TaskStore(sf);
        tasks = List.of(new Task(0, "task1", "descr1", LocalDateTime.now(), false),
                new Task(0, "task2", "descr2", LocalDateTime.now(), false),
                new Task(0, "task3", "descr3", LocalDateTime.now(), true),
                new Task(0, "task4", "descr4", LocalDateTime.now(), true));
    }

    @AfterEach
    public void clear() {
        for (Task task : store.findAll()) {
            store.deleteById(task.getId());
        }
    }

    /**
     * Проверяет сценарий возврата данных всех заданий методом {@code findAll}
     */
    @Test
    public void whenFindAllThenGetAllTasksData() {
        addTasks();

        var actualTasks = store.findAll();

        assertThat(actualTasks).usingRecursiveComparison()
                .withComparatorForType(
                        Comparator.comparing(o -> o.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class)
                .ignoringCollectionOrder()
                .isEqualTo(tasks);
    }

    private void addTasks() {
        for (Task task : tasks) {
            store.save(task);
        }
    }

    /**
     * Проверяет сценарий возврата данных выполненных заданий методом {@code findDone}
     */
    @Test
    void whenFindDoneThenGetDoneTasksData() {
        addTasks();
        var expectedTasks = tasks.stream().filter(Task::isDone).toList();

        var actualTasks = store.findDone();

        assertThat(actualTasks).usingRecursiveComparison()
                .withComparatorForType(
                        Comparator.comparing(o -> o.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class)
                .ignoringCollectionOrder()
                .isEqualTo(expectedTasks);
    }

    /**
     * Проверяет сценарий возврата данных новых заданий методом {@code findNew}
     */
    @Test
    void whenFindNewThenGetNewTasksData() {
        addTasks();
        var expectedTasks = tasks.stream().filter(task -> !task.isDone()).toList();

        var actualTasks = store.findNew();

        assertThat(actualTasks).usingRecursiveComparison()
                .withComparatorForType(
                        Comparator.comparing(o -> o.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class)
                .ignoringCollectionOrder()
                .isEqualTo(expectedTasks);
    }

    /**
     * Проверяет успешный сценарий возврата данных задания методом {@code findById}
     */
    @Test
    void whenFindByIdSuccessfulThenGetTaskData() {
        addTasks();
        var expectedTask = tasks.get(tasks.size() - 1);

        var actualTask = store.findById(expectedTask.getId());

        assertThat(actualTask).isNotEmpty();
        assertThat(actualTask.get()).usingRecursiveComparison()
                .withComparatorForType(
                        Comparator.comparing(o -> o.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class)
                .isEqualTo(expectedTask);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных задания методом {@code findById}
     */
    @Test
    void whenFindByIdUnSuccessfulThenGetEmpty() {
        addTasks();
        var id = -1;

        var actualTask = store.findById(id);

        assertThat(actualTask).isEmpty();
    }

    /**
     * Проверяет успешный сценарий выполнения задания методом {@code setDoneById}
     */
    @Test
    void whenSetDoneByIdSuccessfulThenGetTrue() {
        addTasks();
        var task = tasks.stream().filter(t -> !t.isDone()).findFirst().orElse(new Task());
        task.setDone(true);

        var wasSetDone = store.setDoneById(task.getId());
        var actualTask = store.findById(task.getId());

        assertThat(wasSetDone).isTrue();
        assertThat(actualTask).isNotEmpty();
        assertThat(actualTask.get()).usingRecursiveComparison()
                .withComparatorForType(
                        Comparator.comparing(o -> o.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class)
                .isEqualTo(task);
    }

    /**
     * Проверяет неуспешный сценарий выполнения задания методом {@code setDoneById}
     */
    @Test
    void whenSetDoneByIdUnSuccessfulThenGetFalse() {
        addTasks();
        var id = -1;

        var wasSetDone = store.setDoneById(id);
        var actualTasks = store.findAll();

        assertThat(wasSetDone).isFalse();
        assertThat(actualTasks).usingRecursiveComparison()
                .withComparatorForType(
                        Comparator.comparing(o -> o.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class)
                .ignoringCollectionOrder()
                .isEqualTo(tasks);
    }

    /**
     * Проверяет успешный сценарий удаления задания методом {@code deleteById}
     */
    @Test
    void whenDeleteByIdSuccessfulThenGetTrue() {
        addTasks();
        var id = tasks.get(tasks.size() - 1).getId();
        var expectedTasks = tasks.stream().filter(task -> task.getId() != id).toList();

        var wasDeleted = store.deleteById(id);
        var actualTasks = store.findAll();

        assertThat(wasDeleted).isTrue();
        assertThat(actualTasks).usingRecursiveComparison()
                .withComparatorForType(
                        Comparator.comparing(o -> o.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class)
                .ignoringCollectionOrder()
                .isEqualTo(expectedTasks);
    }

    /**
     * Проверяет неуспешный сценарий удаления задания методом {@code deleteById}
     */
    @Test
    void whenDeleteByIdUnSuccessfulThenGetFalse() {
        addTasks();
        var id = -1;

        var wasDeleted = store.deleteById(id);
        var actualTasks = store.findAll();

        assertThat(wasDeleted).isFalse();
        assertThat(actualTasks).usingRecursiveComparison()
                .withComparatorForType(
                        Comparator.comparing(o -> o.truncatedTo(ChronoUnit.SECONDS)),
                        LocalDateTime.class)
                .ignoringCollectionOrder()
                .isEqualTo(tasks);
    }

    /**
     * Проверяет успешный сценарий обновления задания методом {@code update}
     */
    @Test
    void whenUpdateSuccessfulThenGetTrue() {
        addTasks();
        var task = tasks.get(tasks.size() - 1);
        task.setTitle(task.getTitle() + " NEW");
        task.setDescription(task.getDescription() + " NEW");

        var wasUpdated = store.update(task);
        var actualTask = store.findById(task.getId());

        assertThat(wasUpdated).isTrue();
        assertThat(actualTask).isNotEmpty();
        assertThat(actualTask.get()).usingRecursiveComparison()
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
        addTasks();
        var task = tasks.get(tasks.size() - 1);
        var id = -1;
        task.setId(id);

        var wasUpdated = store.update(task);
        var actualTask = store.findById(id);

        assertThat(wasUpdated).isFalse();
        assertThat(actualTask).isEmpty();
    }

    /**
     * Проверяет успешный сценарий сохранения задания методом {@code save}
     */
    @Test
    void whenSaveSuccessfulThenGetTrue() {
        addTasks();
        var task = new Task(0, "NEW test1", "NEW descr1", LocalDateTime.now(), true);

        var wasSaved = store.save(task);
        var actualTask = store.findById(task.getId());

        assertThat(wasSaved).isTrue();
        assertThat(actualTask).isNotEmpty();
        assertThat(actualTask.get()).usingRecursiveComparison()
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
        var spySession = spy(sf.openSession());
        doThrow(new RuntimeException()).when(spySession).save(any());
        var spySf = spy(sf);
        when(spySf.openSession()).thenReturn(spySession);
        var testStore = new TaskStore(spySf);
        var task = new Task();

        var wasSaved = testStore.save(task);

        assertThat(wasSaved).isFalse();
    }
}