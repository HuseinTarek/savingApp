package com.swedenrosca.service;

import com.swedenrosca.model.PaymentPlan;
import com.swedenrosca.repository.*;
import org.hibernate.*;
import java.util.List;

public class PaymentPlanService {
    private final SessionFactory sessionFactory;
    private final PaymentPlanRepository paymentPlanRepository;

    public PaymentPlanService(SessionFactory sessionFactory, PaymentPlanRepository paymentPlanRepository) {
        this.sessionFactory = sessionFactory;
        this.paymentPlanRepository = paymentPlanRepository;
    }

    public void createPaymentPlan(PaymentPlan plan) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            paymentPlanRepository.save(session, plan);
            session.getTransaction().commit();
        }
    }

    public void updatePaymentPlan(PaymentPlan plan) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            paymentPlanRepository.update(session, plan);
            session.getTransaction().commit();
        }
    }

    public void deletePaymentPlan(PaymentPlan plan) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            paymentPlanRepository.delete(session, plan);
            session.getTransaction().commit();
        }
    }

    public PaymentPlan getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return paymentPlanRepository.getById(session, id);
        }
    }

    public List<PaymentPlan> getAllPaymentPlans() {
        try (Session session = sessionFactory.openSession()) {
            return paymentPlanRepository.getAll(session);
        }
    }

    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            paymentPlanRepository.deleteAll(session);
            session.getTransaction().commit();
        }
    }

    public PaymentPlan createPaymentPlan(int monthlyPayment, int monthsCount) {
        try (Session session = sessionFactory.openSession()) {
            PaymentPlan plan = new PaymentPlan(monthsCount, java.math.BigDecimal.valueOf(monthlyPayment));
            paymentPlanRepository.save(session, plan);
            return plan;
        }
    }

    public List<PaymentPlan> getPlansByCreator(String creator) {
        try (Session session = sessionFactory.openSession()) {
            return paymentPlanRepository.findByCreatedBy(session, creator);
        }
    }
} 