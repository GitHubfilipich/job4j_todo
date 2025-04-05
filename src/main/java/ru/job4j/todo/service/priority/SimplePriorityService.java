package ru.job4j.todo.service.priority;

import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.repository.priority.PriorityRepository;

import java.util.Optional;

@Service
public class SimplePriorityService implements PriorityService {

    private final PriorityRepository repository;

    public SimplePriorityService(PriorityRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Priority> findById(int id) {
        return repository.findById(id);
    }
}
