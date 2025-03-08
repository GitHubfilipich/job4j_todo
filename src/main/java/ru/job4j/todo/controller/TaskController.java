package ru.job4j.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.service.TaskService;

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
        // TODO
        // model.addAttribute("tasks", taskService.findDone());
        model.addAttribute("status", "done");
        return "list";
    }

    @GetMapping("/new")
    public String getNew(Model model) {
        // TODO
        // model.addAttribute("tasks", taskService.findNew());
        model.addAttribute("status", "new");
        return "list";
    }

    @GetMapping("/add")
    public String addTask(Model model) {
        // TODO
        // model.addAttribute("task", taskService.???());
        // model.addAttribute("status", "new");
        return "list";
    }

    @GetMapping("/task/{id}")
    public String getTask(Model model, @PathVariable int id) {
        // TODO
        // model.addAttribute("task", taskService.findById(id));

        return "task";
    }

    @GetMapping("/task/setDone/{id}")
    public String doTask(Model model, @PathVariable int id) {
        // TODO
        // model.addAttribute("task", taskService.setDone(id));

        return "task";
    }

    @GetMapping("/task/edit/{id}")
    public String editTask(Model model, @PathVariable int id) {
        // TODO
        // model.addAttribute("task", taskService.findById(id));
        // model.addAttribute("mode", "edit");

        return "task";
    }

    @GetMapping("/task/delete/{id}")
    public String deleteTask(Model model, @PathVariable int id) {
        // TODO
        // model.addAttribute("tasks", taskService.deleteById(id));

        return "redirect:/list";
    }

    @PostMapping("/task/update")
    public String update(@ModelAttribute Task task, Model model) {
        // TODO
        // model.addAttribute("tasks", taskService.update(task));

        return "task";
    }
}
