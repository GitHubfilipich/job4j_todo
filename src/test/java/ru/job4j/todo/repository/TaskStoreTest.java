package ru.job4j.todo.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.repository.task.Store;
import ru.job4j.todo.repository.task.TaskStore;
import ru.job4j.todo.repository.user.SimpleUserRepository;
import ru.job4j.todo.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class TaskStoreTest {

    public static SessionFactory sf;
    private static Store store;
    private static UserRepository userRepository;
    private static List<Task> tasks;
    private static List<User> users;
    private static List<Priority> priorities;

    @BeforeAll
    public static void setUp(@Autowired SessionFactory awSf) {
        sf = awSf;
        var crudRepository = new CrudRepository(sf);
        store = new TaskStore(crudRepository);
        userRepository = new SimpleUserRepository(crudRepository);
    }

    @AfterEach
    public void clear() {
        for (Task task : store.findAll()) {
            store.deleteById(task.getId());
        }
        try (Session session = sf.openSession()) {
            var tx = session.beginTransaction();
            var query = session.createQuery("DELETE User");
            query.executeUpdate();
            query = session.createQuery("DELETE Priority");
            query.executeUpdate();
            tx.commit();
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
        users = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            var user = new User();
            user.setName("test" + i);
            user.setLogin("login" + i);
            user.setPassword("password" + i);
            userRepository.save(user);
            users.add(user);
        }
        priorities = new ArrayList<>();
        try (Session session = sf.openSession()) {
            var tx = session.beginTransaction();
            for (int i = 0; i < 4; i++) {
                var priority = new Priority();
                priority.setName("test" + i);
                priority.setPosition(i);
                session.save(priority);
                priorities.add(priority);
            }
            tx.commit();
        }
        tasks = List.of(new Task(0, "task1", "descr1", LocalDateTime.now(), false, users.get(0), priorities.get(0)),
                new Task(0, "task2", "descr2", LocalDateTime.now(), false, users.get(1), priorities.get(1)),
                new Task(0, "task3", "descr3", LocalDateTime.now(), true, users.get(2), priorities.get(2)),
                new Task(0, "task4", "descr4", LocalDateTime.now(), true, users.get(3), priorities.get(3)));
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
        var user = new User();
        user.setName("test5");
        user.setLogin("login4");
        user.setPassword("password4");
        userRepository.save(user);
        var task = new Task(0, "NEW test1", "NEW descr1", LocalDateTime.now(), true, user, null);

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
        doThrow(new RuntimeException()).when(spySession).persist(any());
        var spySf = spy(sf);
        when(spySf.openSession()).thenReturn(spySession);
        var testStore = new TaskStore(new CrudRepository(spySf));
        var task = new Task();

        var wasSaved = testStore.save(task);

        assertThat(wasSaved).isFalse();
    }
}