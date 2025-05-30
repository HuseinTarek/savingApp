package com.swedenrosca.service;

import com.swedenrosca.model.*;
import com.swedenrosca.repository.*;
import org.hibernate.*;
import java.util.List;

public class UserService {
    private final SessionFactory sessionFactory;
    private final UserRepository userRepository;

    public UserService(SessionFactory sessionFactory, UserRepository userRepository) {
        this.sessionFactory = sessionFactory;
        this.userRepository = userRepository;
    }

    public void createUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            userRepository.save(session, user);
            session.getTransaction().commit();
        }
    }

    public void updateUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            userRepository.update(session, user);
            session.getTransaction().commit();
        }
    }

    public void deleteUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            userRepository.delete(session, user);
            session.getTransaction().commit();
        }
    }

    public User getUserById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(User.class, id);
        }
    }

    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            return userRepository.getAll(session);
        }
    }

    public User getUserByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return userRepository.getByUsername(session, username);
        }
    }

    public User getByUsernameAndPassword(String username, String password) {
        try (Session session = sessionFactory.openSession()) {
            return userRepository.getByUserNameAndPassword(session, username, password);
        }
    }

    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            userRepository.deleteAll(session);
            session.getTransaction().commit();
        }
    }

    public boolean existsByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return userRepository.existsByUsername(session, username);
        }
    }
} 