package com.swedenrosca.service;

import com.swedenrosca.model.MonthlyPayment;
import com.swedenrosca.model.Group;
import com.swedenrosca.repository.MonthlyPaymentRepository;
import com.swedenrosca.repository.SingletonSessionFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.List;

public class MonthlyPaymentService {
    private final SessionFactory sessionFactory;
    private final MonthlyPaymentRepository monthlyPaymentRepository;

    public MonthlyPaymentService(MonthlyPaymentRepository monthlyPaymentRepository) {
        this.sessionFactory = SingletonSessionFactory.getSessionFactory();
        this.monthlyPaymentRepository = monthlyPaymentRepository;
    }

    public void createMonthlyPayment(MonthlyPayment payment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            monthlyPaymentRepository.save(session, payment);
            session.getTransaction().commit();
        }
    }

    public void updateMonthlyPayment(MonthlyPayment payment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            monthlyPaymentRepository.update(session, payment);
            session.getTransaction().commit();
        }
    }

    public void deleteMonthlyPayment(MonthlyPayment payment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            monthlyPaymentRepository.delete(session, payment);
            session.getTransaction().commit();
        }
    }

    public MonthlyPayment getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return monthlyPaymentRepository.getById(session, id);
        }
    }

    public List<MonthlyPayment> getByGroup(Group group) {
        try (Session session = sessionFactory.openSession()) {
            return monthlyPaymentRepository.getByGroupId(session, group.getId());
        }
    }

    public List<MonthlyPayment> getAllMonthlyPayments() {
        try (Session session = sessionFactory.openSession()) {
            return monthlyPaymentRepository.getAll(session);
        }
    }

    public List<MonthlyPayment> getByGroupId(Long groupId) {
        try (Session session = sessionFactory.openSession()) {
            return monthlyPaymentRepository.getByGroupId(session, groupId);
        }
    }

    public List<MonthlyPayment> getByGroupIdAndMonth(Long groupId, int month) {
        try (Session session = sessionFactory.openSession()) {
            return monthlyPaymentRepository.getByGroupIdAndMonth(session, groupId, month);
        }
    }

    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            monthlyPaymentRepository.deleteAll(session);
            session.getTransaction().commit();
        }
    }
} 