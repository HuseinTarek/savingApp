package com.swedenrosca.repository;

import com.swedenrosca.model.PaymentPlan;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;

public class PaymentPlanRepository {
    private final SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();

    public PaymentPlanRepository() {
    }

    /**
     * Save new PaymentPlan to the database
     */
    public void save(PaymentPlan plan) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(plan);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    /**
     *  Find PaymentPlan by its primary key
     */
    public PaymentPlan findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(PaymentPlan.class, id);
        }
    }

    /**
     *  Retrieve all PaymentPlan records
     */
    public List<PaymentPlan> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<PaymentPlan> query = session.createQuery("FROM PaymentPlan", PaymentPlan.class);
            return query.list();
        }
    }

    /**
     *  Find plans created by a specific user (creator)
     */
    public List<PaymentPlan> findByCreatedBy(String createdBy) {
        Session session = sessionFactory.openSession();
            Query<PaymentPlan> query = session.createQuery(
                    "FROM PaymentPlan p WHERE p.createdBy = :creator", PaymentPlan.class
            );
            query.setParameter("creator", createdBy);
            return query.list();
        }

    /**
     * Update an existing PaymentPlan
     */
    public void update(PaymentPlan plan) {
        Transaction tx = null;
        Session session = sessionFactory.openSession();
            tx = session.beginTransaction();
            session.merge(plan);
            tx.commit();
    }

    public void getByMonthlyPayment(Long id) {
        Session session = sessionFactory.openSession();
        Query<PaymentPlan> query = session.createQuery(
                "FROM PaymentPlan p WHERE p.monthlyPayment.id = :id", PaymentPlan.class
        );
        query.setParameter("id", id);
        query.list();
    }

    public void getByMonthsCount(Integer count) {
        Session session = sessionFactory.openSession();
        Query<PaymentPlan> query = session.createQuery(
                "FROM PaymentPlan p WHERE p.monthsCount = :count", PaymentPlan.class
        );
        query.setParameter("count", count);
        query.list();
    }

    /**
     * English comment: Delete a PaymentPlan by its id
     */
    public void delete(Long id) {
        Transaction tx = null;
        Session session = sessionFactory.openSession();
            tx = session.beginTransaction();
            PaymentPlan plan = session.get(PaymentPlan.class, id);
            if (plan != null) {
                session.remove(plan);
            }
            tx.commit();
    }

    public List<PaymentPlan> findByPaymentAndMonths(BigDecimal monthlyPayment, int monthsCount) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query<PaymentPlan> query = session.createQuery(
                "FROM PaymentPlan p WHERE p.monthlyPayment = :payment AND p.monthsCount = :months",
                PaymentPlan.class
        );
        query.setParameter("payment", monthlyPayment);
        query.setParameter("months", monthsCount);

        List<PaymentPlan> plans = query.getResultList();

        session.getTransaction().commit();
        session.close();

        return plans;
    }

    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM PaymentPlan").executeUpdate();
            session.getTransaction().commit();
        }
    }

}
