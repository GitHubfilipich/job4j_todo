package ru.job4j.todo.service.user;

import org.springframework.stereotype.Service;
import ru.job4j.todo.model.User;
import ru.job4j.todo.repository.user.UserRepository;

import java.util.Optional;

@Service
public class SimpleUserService implements UserService {

    private final UserRepository repository;

    public SimpleUserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean save(User user) {
        return repository.save(user);
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        return repository.findByLoginAndPassword(login, password);
    }

    @Override
    public Optional<User> findById(int id) {
        return repository.findById(id);
    }
}
