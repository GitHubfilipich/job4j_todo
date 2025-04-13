package ru.job4j.todo.controller;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.todo.dto.TaskDTO;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.category.CategoryService;
import ru.job4j.todo.service.priority.PriorityService;
import ru.job4j.todo.service.task.TaskService;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskControllerTest {

    private TaskController taskController;
    private TaskService taskService;
    private PriorityService priorityService;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        taskService = mock(TaskService.class);
        priorityService = mock(PriorityService.class);
        categoryService = mock(CategoryService.class);
        taskController = new TaskController(taskService, priorityService, categoryService);
    }

    /**
     * Проверяет сценарий возврата страницы всех заданий методом {@code getAll}
     */
    @Test
    void whenGetAllThenGetPageWithTasks() {
        var tasks = getTaskDTOS();
        when(taskService.findAll()).thenReturn(tasks);
        var model = new ConcurrentModel();

        var actual = taskController.getAll(model);
        var actualTasks = model.getAttribute("tasks");

        assertThat(actual).isEqualTo("list");
        assertThat(actualTasks)
                .asInstanceOf(InstanceOfAssertFactories.collection(TaskDTO.class))
                        .containsExactlyInAnyOrderElementsOf(tasks);
    }

    private List<TaskDTO> getTaskDTOS() {
        return List.of(new TaskDTO(1, "Test1", "Descr1", LocalDateTime.now(), false, 1, "Ivan", 1, "priority1", List.of(), ""),
                new TaskDTO(2, "Test2", "Descr2", LocalDateTime.now(), true, 2, "Petr", 2, "priority2", List.of(1), "test1"),
                new TaskDTO(3, "Test3", "Descr3", LocalDateTime.now(), false, 3, "Pavel", 3, "priority3", List.of(1, 2), "test1, test2"));
    }

    /**
     * Проверяет сценарий возврата страницы всех выполненных заданий методом {@code getDone}
     */
    @Test
    void whenGetDoneThenGetPageWithDoneTasks() {
        var tasks = getTaskDTOS();
        when(taskService.findDone()).thenReturn(tasks);
        var model = new ConcurrentModel();

        var actual = taskController.getDone(model);
        var actualTasks = model.getAttribute("tasks");

        assertThat(actual).isEqualTo("list");
        assertThat(actualTasks).asInstanceOf(InstanceOfAssertFactories.collection(TaskDTO.class))
                .containsExactlyInAnyOrderElementsOf(tasks);
    }

    /**
     * Проверяет сценарий возврата страницы всех новых заданий методом {@code getNew}
     */
    @Test
    void whenGetNewThenGetPageWithNewTasks() {
        var tasks = getTaskDTOS();
        when(taskService.findNew()).thenReturn(tasks);
        var model = new ConcurrentModel();

        var actual = taskController.getNew(model);
        var actualTasks = model.getAttribute("tasks");

        assertThat(actual).isEqualTo("list");
        assertThat(actualTasks).asInstanceOf(InstanceOfAssertFactories.collection(TaskDTO.class))
                .containsExactlyInAnyOrderElementsOf(tasks);
    }

    /**
     * Проверяет сценарий возврата страницы нового задания методом {@code addTask}
     */
    @Test
    void whenAddTaskThenGetPageWithNewTask() {
        var user = new User();
        var task = new TaskDTO();
        task.setUserName(user.getName());
        task.setUserId(user.getId());
        var model = new ConcurrentModel();

        var actual = taskController.addTask(model, user);
        var actualTask = model.getAttribute("task");
        var actualMode = model.getAttribute("mode");

        assertThat(actual).isEqualTo("task");
        assertThat(actualTask).isEqualTo(task);
        assertThat(actualMode).isEqualTo("taskNew");
    }

    /**
     * Проверяет успешный сценарий возврата страницы задания методом {@code getTask}
     */
    @Test
    void whenGetTaskSuccessfulThenGetPageWithTask() {
        var id = 1;
        var task = new TaskDTO(id, "Test1", "Descr1", LocalDateTime.now(), false, 1, "Ivan", 1, "priority1", List.of(1), "test1");
        var intArgCaptor = ArgumentCaptor.forClass(Integer.class);
        when(taskService.findById(intArgCaptor.capture())).thenReturn(Optional.of(task));
        var model = new ConcurrentModel();

        var actual = taskController.getTask(model, id);
        var actualTask = model.getAttribute("task");
        var actualMode = model.getAttribute("mode");
        var actualId = intArgCaptor.getValue();

        assertThat(actual).isEqualTo("task");
        assertThat(actualTask).isEqualTo(task);
        assertThat(actualMode).isEqualTo("taskExist");
        assertThat(actualId).isEqualTo(id);
    }

    /**
     * Проверяет неуспешный сценарий возврата страницы задания методом {@code getTask}
     */
    @Test
    void whenGetTaskUnSuccessfulThenGetErrorPage() {
        var id = 1;
        when(taskService.findById(any(Integer.class))).thenReturn(Optional.empty());
        var model = new ConcurrentModel();

        var actual = taskController.getTask(model, id);
        var actualMessage = model.getAttribute("message");

        assertThat(actual).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Не удалось найти задание");
    }

    /**
     * Проверяет успешный сценарий выполнения задания методом {@code doTask}
     */
    @Test
    void whenDoTaskSuccessfulThenDoTaskAndGetPageWithTasks() {
        var id = 1;
        var intArgCaptor = ArgumentCaptor.forClass(Integer.class);
        when(taskService.setDoneById(intArgCaptor.capture())).thenReturn(true);
        var model = new ConcurrentModel();

        var actual = taskController.doTask(model, id);
        var actualId = intArgCaptor.getValue();

        assertThat(actual).isEqualTo("redirect:/");
        assertThat(actualId).isEqualTo(id);
    }

    /**
     * Проверяет неуспешный сценарий выполнения задания методом {@code doTask}
     */
    @Test
    void whenDoTaskUnSuccessfulThenGetErrorPage() {
        var id = 1;
        when(taskService.setDoneById(any(Integer.class))).thenReturn(false);
        var model = new ConcurrentModel();

        var actual = taskController.doTask(model, id);
        var actualMessage = model.getAttribute("message");

        assertThat(actual).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Не удалось установить задание как выполненное");
    }

    /**
     * Проверяет успешный сценарий получения страницы редактирования задания методом {@code editTask}
     */
    @Test
    void whenEditTaskSuccessfulThenGetPageWithTask() {
        var id = 1;
        var task = new TaskDTO(id, "Test1", "Descr1", LocalDateTime.now(), false, 1, "Ivan", 1, "priority1", List.of(1), "test1");
        var intArgCaptor = ArgumentCaptor.forClass(Integer.class);
        when(taskService.findById(intArgCaptor.capture())).thenReturn(Optional.of(task));
        var model = new ConcurrentModel();

        var actual = taskController.editTask(model, id);
        var actualTask = model.getAttribute("task");
        var actualMode = model.getAttribute("mode");
        var actualId = intArgCaptor.getValue();

        assertThat(actual).isEqualTo("task");
        assertThat(actualTask).isEqualTo(task);
        assertThat(actualMode).isEqualTo("taskEdit");
        assertThat(actualId).isEqualTo(id);
    }

    /**
     * Проверяет неуспешный сценарий получения страницы редактирования задания методом {@code editTask}
     */
    @Test
    void whenEditTaskUnSuccessfulThenGetErrorPage() {
        var id = 1;
        when(taskService.findById(any(Integer.class))).thenReturn(Optional.empty());
        var model = new ConcurrentModel();

        var actual = taskController.editTask(model, id);
        var actualMessage = model.getAttribute("message");

        assertThat(actual).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Не удалось найти задание");
    }

    /**
     * Проверяет успешный сценарий удаления задания методом {@code deleteTask}
     */
    @Test
    void whenDeleteTaskSuccessfulThenDeleteTaskAndGetPageWithTasks() {
        var id = 1;
        var intArgCaptor = ArgumentCaptor.forClass(Integer.class);
        when(taskService.deleteById(intArgCaptor.capture())).thenReturn(true);
        var model = new ConcurrentModel();

        var actual = taskController.deleteTask(model, id);
        var actualId = intArgCaptor.getValue();

        assertThat(actual).isEqualTo("redirect:/");
        assertThat(actualId).isEqualTo(id);
    }

    /**
     * Проверяет неуспешный сценарий удаления задания методом {@code deleteTask}
     */
    @Test
    void whenDeleteTaskUnSuccessfulThenGetErrorPage() {
        var id = 1;
        when(taskService.deleteById(any(Integer.class))).thenReturn(false);
        var model = new ConcurrentModel();

        var actual = taskController.deleteTask(model, id);
        var actualMessage = model.getAttribute("message");

        assertThat(actual).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Не удалось удалить задание");
    }

    /**
     * Проверяет успешный сценарий сохранения нового задания методом {@code saveOrUpdate}
     */
    @Test
    void whenSaveSuccessfulThenSaveTaskAndGetPageWithTasks() {
        var task = new TaskDTO(1, "test1", "descr1", LocalDateTime.now(), false, 1, "Ivan", 1, "priority1", List.of(1), "test1");
        var taskArgCaptor = ArgumentCaptor.forClass(TaskDTO.class);
        when(taskService.save(taskArgCaptor.capture())).thenReturn(true);
        var model = new ConcurrentModel();
        var mode = "taskNew";

        var actual = taskController.saveOrUpdate(task, mode, model);
        var actualTask = taskArgCaptor.getValue();

        assertThat(actual).isEqualTo("redirect:/");
        assertThat(actualTask).isEqualTo(task);
    }

    /**
     * Проверяет неуспешный сценарий сохранения нового задания методом {@code saveOrUpdate}
     */
    @Test
    void whenSaveUnSuccessfulThenGetErrorPage() {
        when(taskService.save(any(TaskDTO.class))).thenReturn(false);
        var model = new ConcurrentModel();
        var mode = "taskNew";

        var actual = taskController.saveOrUpdate(new TaskDTO(), mode, model);
        var actualMessage = model.getAttribute("message");

        assertThat(actual).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Не удалось сохранить задание");
    }

    /**
     * Проверяет успешный сценарий обновления задания методом {@code saveOrUpdate}
     */
    @Test
    void whenUpdateSuccessfulThenUpdateTaskAndGetPageWithTasks() {
        var task = new TaskDTO(1, "test1", "descr1", LocalDateTime.now(), false, 1, "Ivan", 1, "priority1", List.of(1), "test1");
        var taskArgCaptor = ArgumentCaptor.forClass(TaskDTO.class);
        when(taskService.update(taskArgCaptor.capture())).thenReturn(true);
        var model = new ConcurrentModel();
        var mode = "taskEdit";

        var actual = taskController.saveOrUpdate(task, mode, model);
        var actualTask = taskArgCaptor.getValue();

        assertThat(actual).isEqualTo("redirect:/");
        assertThat(actualTask).isEqualTo(task);
    }

    /**
     * Проверяет неуспешный сценарий обновления задания методом {@code saveOrUpdate}
     */
    @Test
    void whenUpdateUnSuccessfulThenGetErrorPage() {
        when(taskService.update(any(TaskDTO.class))).thenReturn(false);
        var model = new ConcurrentModel();
        var mode = "taskEdit";

        var actual = taskController.saveOrUpdate(new TaskDTO(), mode, model);
        var actualMessage = model.getAttribute("message");

        assertThat(actual).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Не удалось обновить задание");
    }
}