package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@AllArgsConstructor
public class TaskStore implements Store {
    private final SessionFactory sf;

    @Override
    public Collection<Task> findAll() {
        try (Session session = sf.openSession()) {
            return session.createQuery("from Task ORDER BY created", Task.class)
                    .list();
        } catch (Exception e) {
            log.error("Ошибка получения заданий", e);
            return List.of();
        }
    }

    @Override
    public Collection<Task> findDone() {
        try (Session session = sf.openSession()) {
            return session.createQuery("from Task WHERE done = true ORDER BY created", Task.class)
                    .list();
        } catch (Exception e) {
            log.error("Ошибка получения заданий", e);
            return List.of();
        }
    }

    @Override
    public Collection<Task> findNew() {
        try (Session session = sf.openSession()) {
            return session.createQuery("from Task WHERE done = false ORDER BY created", Task.class)
                    .list();
        } catch (Exception e) {
            log.error("Ошибка получения заданий", e);
            return List.of();
        }
    }

    @Override
    public Optional<Task> findById(int id) {
        try (Session session = sf.openSession()) {
            return Optional.ofNullable(session.get(Task.class, id));
        } catch (Exception e) {
            log.error("Ошибка получения заданий", e);
            return Optional.empty();
        }
    }

    @Override
    public boolean setDoneById(int id) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            int numUpdated = session.createQuery("UPDATE Task SET done = true WHERE id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            return numUpdated > 0;
        } catch (Exception e) {
            log.error("Ошибка обновления заданий", e);
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            int numDeleted = session.createQuery("DELETE Task WHERE id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            return numDeleted > 0;
        } catch (Exception e) {
            log.error("Ошибка удаления заданий", e);
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    public boolean update(Task task) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            int numUpdated = session.createQuery("UPDATE Task SET title = :title, description = :description WHERE id = :id")
                    .setParameter("title", task.getTitle())
                    .setParameter("description", task.getDescription())
                    .setParameter("id", task.getId())
                    .executeUpdate();
            return numUpdated > 0;
        } catch (Exception e) {
            log.error("Ошибка обновления заданий", e);
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    public boolean save(Task task) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            session.save(task);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            log.error("Ошибка сохранения заданий", e);
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return false;
    }
}
