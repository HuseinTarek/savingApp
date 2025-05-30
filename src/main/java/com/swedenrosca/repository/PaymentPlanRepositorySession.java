package com.swedenrosca.repository;

import com.swedenrosca.model.PaymentPlan;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class PaymentPlanRepositorySession {
    public PaymentPlan getById(Session session, Long id) {
        return session.get(PaymentPlan.class, id);
    }

    public List<PaymentPlan> getAll(Session session) {
        return session.createQuery("FROM PaymentPlan", PaymentPlan.class).getResultList();
    }

    public void save(Session session, PaymentPlan plan) {
        session.persist(plan);
    }

    public void update(Session session, PaymentPlan plan) {
        session.merge(plan);
    }

    public void delete(Session session, PaymentPlan plan) {
        session.remove(session.contains(plan) ? plan : session.merge(plan));
    }
} 