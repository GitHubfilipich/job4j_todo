package ru.job4j.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.dto.TaskDTO;
import ru.job4j.todo.service.TaskService;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "list";
    }

    @GetMapping("/done")
    public String getDone(Model model) {
        model.addAttribute("tasks", taskService.findDone());
        model.addAttribute("mode", "listDone");
        return "list";
    }

    @GetMapping("/new")
    public String getNew(Model model) {
        model.addAttribute("tasks", taskService.findNew());
        model.addAttribute("mode", "listNew");
        return "list";
    }

    @GetMapping("/add")
    public String addTask(Model model) {
        TaskDTO taskDTO = new TaskDTO();
        model.addAttribute("task", taskDTO);
        model.addAttribute("mode", "taskNew");
        return "task";
    }

    @GetMapping("/task/{id}")
    public String getTask(Model model, @PathVariable int id) {
        var optionalTaskDTO = taskService.findById(id);
        if (optionalTaskDTO.isEmpty()) {
            model.addAttribute("message", "Не удалось найти задание");
            return "errors/404";
        }
        model.addAttribute("task", optionalTaskDTO.get());
        model.addAttribute("mode", "taskExist");
        return "task";
    }

    @GetMapping("/task/setDone/{id}")
    public String doTask(Model model, @PathVariable int id) {
        if (!taskService.setDoneById(id)) {
            model.addAttribute("message", "Не удалось установить задание как выполненное");
            return "errors/404";
        }
        return "redirect:/";
    }

    @GetMapping("/task/edit/{id}")
    public String editTask(Model model, @PathVariable int id) {
        Optional<TaskDTO> optionalTaskDTO = taskService.findById(id);
        if (optionalTaskDTO.isEmpty()) {
            model.addAttribute("message", "Не удалось найти задание");
            return "errors/404";
        }
        model.addAttribute("task", optionalTaskDTO.get());
        model.addAttribute("mode", "taskEdit");
        return "task";
    }

    @GetMapping("/task/delete/{id}")
    public String deleteTask(Model model, @PathVariable int id) {
        if (!taskService.deleteById(id)) {
            model.addAttribute("message", "Не удалось удалить задание");
            return "errors/404";
        }
        return "redirect:/";
    }

    @PostMapping("/task/update")
    public String saveOrUpdate(@ModelAttribute TaskDTO task, @RequestParam("mode") String mode, Model model) {
        if ("taskNew".equals(mode)) {
            task.setCreated(LocalDateTime.now());
            return save(task, model);
        }
        return update(task, model);
    }

    private String save(TaskDTO task, Model model) {
        if (!taskService.save(task)) {
            model.addAttribute("message", "Не удалось сохранить задание");
            return "errors/404";
        }
        return "redirect:/";
    }

    private String update(TaskDTO task, Model model) {
        if (!taskService.update(task)) {
            model.addAttribute("message", "Не удалось обновить задание");
            return "errors/404";
        }
        return "redirect:/";
    }
}
