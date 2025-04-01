package ru.job4j.todo.repository.user;

import ru.job4j.todo.model.User;

import java.util.Optional;

public interface UserRepository {

    boolean save(User user);

    Optional<User> findByLoginAndPassword(String login, String password);

    Optional<User> findById(int userId);
}
