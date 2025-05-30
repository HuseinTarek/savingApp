package com.swedenrosca.service;

import com.swedenrosca.model.PaymentOption;
import com.swedenrosca.repository.PaymentOptionRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

public class PaymentOptionService {
    private final SessionFactory sessionFactory;
    private final PaymentOptionRepository paymentOptionRepository;

    public PaymentOptionService(SessionFactory sessionFactory, PaymentOptionRepository paymentOptionRepository) {
        this.sessionFactory = sessionFactory;
        this.paymentOptionRepository = paymentOptionRepository;
    }

    public List<PaymentOption> getAllMonthlyPayments() {
        try (Session session = sessionFactory.openSession()) {
            return paymentOptionRepository.getAllMonthlyPayments(session);
        }
    }

    public void save(PaymentOption option) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            paymentOptionRepository.save(session, option);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void update(PaymentOption option) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            paymentOptionRepository.update(session, option);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void deleteById(Long id) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            paymentOptionRepository.deleteById(session, id);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public PaymentOption findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return paymentOptionRepository.findById(session, id);
        }
    }

    public void deleteAll() {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            paymentOptionRepository.deleteAll(session);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public List<PaymentOption> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return paymentOptionRepository.getAll(session);
        }
    }
} 