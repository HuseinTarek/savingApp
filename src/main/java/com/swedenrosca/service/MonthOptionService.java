package com.swedenrosca.service;

import com.swedenrosca.model.MonthOption;
import com.swedenrosca.repository.MonthOptionRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.List;

public class MonthOptionService {
    private final SessionFactory sessionFactory;
    private final MonthOptionRepository monthOptionRepository;

    public MonthOptionService(SessionFactory sessionFactory, MonthOptionRepository monthOptionRepository) {
        this.sessionFactory = sessionFactory;
        this.monthOptionRepository = monthOptionRepository;
    }

    public List<MonthOption> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return monthOptionRepository.getAll(session);
        }
    }

    public void save(MonthOption option) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            monthOptionRepository.save(session, option);
            session.getTransaction().commit();
        }
    }

    public void update(MonthOption option) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            monthOptionRepository.update(session, option);
            session.getTransaction().commit();
        }
    }

    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            monthOptionRepository.deleteById(session, id);
            session.getTransaction().commit();
        }
    }

    public MonthOption findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return monthOptionRepository.findById(session, id);
        }
    }

    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            monthOptionRepository.deleteAll(session);
            session.getTransaction().commit();
        }
    }
} 