package ru.job4j.todo.repository.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;
import ru.job4j.todo.repository.CrudRepository;

import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class SimpleUserRepository implements UserRepository {

    private final CrudRepository crudRepository;

    @Override
    public boolean save(User user) {
        try {
            crudRepository.run(session -> session.persist(user));
            return true;
        } catch (Exception e) {
            log.error("Ошибка сохранения пользователя", e);
        }
        return false;
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        try {
            return crudRepository.optional("from User WHERE login = :login AND password = :password",
                    User.class, Map.of("login", login, "password", password));
        } catch (Exception e) {
            log.error("Ошибка получения пользователя", e);
        }
        return Optional.empty();
    }
}
