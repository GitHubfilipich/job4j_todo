package ru.job4j.todo.store;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class TaskStore implements Store {
    private final SessionFactory sf;

    @Override
    public Collection<Task> findAll() {
        try (Session session = sf.openSession()) {
            return session.createQuery("from Task ORDER BY created", Task.class)
                    .list();
        }
    }

    @Override
    public Collection<Task> findDone() {
        try (Session session = sf.openSession()) {
            return session.createQuery("from Task WHERE done = true ORDER BY created", Task.class)
                    .list();
        }
    }

    @Override
    public Collection<Task> findNew() {
        try (Session session = sf.openSession()) {
            return session.createQuery("from Task WHERE done = false ORDER BY created", Task.class)
                    .list();
        }
    }

    @Override
    public Optional<Task> findById(int id) {
        try (Session session = sf.openSession()) {
            return Optional.ofNullable(session.get(Task.class, id));
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
            int numUpdated = session.createQuery("UPDATE Task SET title = :title, description = :description"
                            + ", created = :created, done = :done WHERE id = :id")
                    .setParameter("title", task.getTitle())
                    .setParameter("description", task.getDescription())
                    .setParameter("created", task.getCreated())
                    .setParameter("done", task.isDone())
                    .setParameter("id", task.getId())
                    .executeUpdate();
            return numUpdated > 0;
        } catch (Exception e) {
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
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return false;
    }
}
