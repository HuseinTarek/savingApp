package com.swedenrosca.repository;

import com.swedenrosca.model.PaymentPlan;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;

public class PaymentPlanRepository {

    public PaymentPlanRepository() {
    }

    /**
     * Save new PaymentPlan to the database
     */
    public void save(Session session, PaymentPlan plan) {
        session.persist(plan);
    }

    /**
     *  Find PaymentPlan by its primary key
     */
    public PaymentPlan getById(Session session, Long id) {
        return session.get(PaymentPlan.class, id);
    }

    /**
     *  Retrieve all PaymentPlan records
     */
    public List<PaymentPlan> getAll(Session session) {
        return session.createQuery("FROM PaymentPlan", PaymentPlan.class).getResultList();
    }

    /**
     *  Find plans created by a specific user (creator)
     */
    public List<PaymentPlan> findByCreatedBy(Session session, String createdBy) {
        Query<PaymentPlan> query = session.createQuery(
                "FROM PaymentPlan p WHERE p.createdBy = :creator", PaymentPlan.class
        );
        query.setParameter("creator", createdBy);
        return query.getResultList();
    }

    /**
     * Update an existing PaymentPlan
     */
    public void update(Session session, PaymentPlan plan) {
        session.merge(plan);
    }

    public void getByMonthlyPayment(Session session, Long id) {
        Query<PaymentPlan> query = session.createQuery(
                "FROM PaymentPlan p WHERE p.monthlyPayment.id = :id", PaymentPlan.class
        );
        query.setParameter("id", id);
        query.getResultList();
    }

    public void getByMonthsCount(Session session, Integer count) {
        Query<PaymentPlan> query = session.createQuery(
                "FROM PaymentPlan p WHERE p.monthsCount = :count", PaymentPlan.class
        );
        query.setParameter("count", count);
        query.getResultList();
    }

    /**
     * English comment: Delete a PaymentPlan by its id
     */
    public void delete(Session session, PaymentPlan plan) {
        session.remove(session.contains(plan) ? plan : session.merge(plan));
    }

    public List<PaymentPlan> findByPaymentAndMonths(Session session, BigDecimal monthlyContribution, int monthsCount) {
        Query<PaymentPlan> query = session.createQuery(
                "FROM PaymentPlan WHERE monthlyPayment = :monthlyContribution AND monthsCount = :monthsCount",
                PaymentPlan.class
        );
        query.setParameter("monthlyContribution", monthlyContribution);
        query.setParameter("monthsCount", monthsCount);
        return query.getResultList();
    }

    public void deleteAll(Session session) {
        session.createQuery("DELETE FROM PaymentPlan").executeUpdate();
    }

}
