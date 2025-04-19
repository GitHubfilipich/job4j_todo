package ru.job4j.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.todo.dto.TimeZoneDTO;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.user.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegistrationPage(Model model, HttpSession session) {
        var user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("timeZones", getTimeZones());
        return "users/register";
    }

    private List<TimeZoneDTO> getTimeZones() {
        return ZoneId.getAvailableZoneIds().stream()
                .map(id -> {
                    ZoneId zoneId = ZoneId.of(id);
                    return new TimeZoneDTO(id, id + " : (UTC " + ZonedDateTime.now(zoneId).getOffset().getId() + ") "
                            + TimeZone.getTimeZone(zoneId).getDisplayName());
                })
                .sorted(Comparator.comparing(TimeZoneDTO::id))
                .toList();
    }

    @PostMapping("/register")
    public String register(Model model, @ModelAttribute User user, HttpSession session) {
        var isSaved = userService.save(user);
        if (!isSaved) {
            model.addAttribute("message", "Пользователь с таким логином уже существует");
            model.addAttribute("user", null);
            return "errors/404";
        }
        session.setAttribute("user", user);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "users/login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, HttpServletRequest request) {
        var userOptional = userService.findByLoginAndPassword(user.getLogin(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Логин или пароль введены неверно");
            return "users/login";
        }
        var session = request.getSession();
        session.setAttribute("user", userOptional.get());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }
}
