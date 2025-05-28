package com.swedenrosca.repository;

import com.swedenrosca.model.PaymentOption;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PaymentOptionRepository {
    private final SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();

    // PaymentOptionRepository
    public List<PaymentOption> getAllMonthlyPayments() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM PaymentOption", PaymentOption.class
            ).getResultList();
        }
    }
    public void save(PaymentOption option) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(option);
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
            session.merge(option);
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
            PaymentOption opt = session.get(PaymentOption.class, id);
            if (opt != null) {
                session.remove(opt);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public PaymentOption findById(Long id) {
        Session session = sessionFactory.openSession();
        Query<PaymentOption> query = session.createQuery(
                "FROM PaymentOption p WHERE p.id = :id", PaymentOption.class
        );
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM PaymentOption").executeUpdate();
            session.getTransaction().commit();
        }
    }
}
