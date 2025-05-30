package com.swedenrosca.repository;

import com.swedenrosca.model.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentRepository {


    public PaymentRepository() {
    }

    public List<Payment> getByGroup(Session session, Group group) {
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group = :group", Payment.class);
        query.setParameter("group", group);
        List<Payment> result = query.getResultList();
        return result;
    }

    public List<Payment> getByStatus(Session session, Group group, PaymentStatus status) {
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group = :group AND p.status = :status", Payment.class);
        query.setParameter("group", group);
        query.setParameter("status", status);
        List<Payment> result = query.getResultList();
        return result;
    }

    public List<Payment> getPaidForGroup(Session session, Group group) {
        return getByStatus(session, group, PaymentStatus.PAID);
    }

    public List<Payment> getPendingPaymentForGroup(Session session, Group group) {
        return getByStatus(session, group, PaymentStatus.PENDING);
    }

    public void save(Session session, Payment payment) {
      
            session.persist(payment);
    
    }

    public Payment update(Session session, Payment payment) {
        return (Payment) session.merge(payment);
    }
    

    public void delete(Session session, Payment payment) {
        session.remove(session.contains(payment) ? payment : session.merge(payment));
    }

    public Payment getById(Session session, Long id) {
        Payment payment = session.get(Payment.class, id);
        return payment;
    }

    public List<Payment> listAll(Session session) {
        Query<Payment> query = session.createQuery("FROM Payment", Payment.class);
        List<Payment> result = query.getResultList();
        return result;
    }

    public List<Payment> getByGroupId(Session session, Long groupId) {
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group.id = :groupId", Payment.class);
        query.setParameter("groupId", groupId);
        List<Payment> result = query.getResultList();
        return result;
    }

    public List<Payment> getUnpaidAfterDate(Session session, Group group, LocalDateTime date) {
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group = :group AND p.dueDate > :date AND p.status != :status", Payment.class);
        query.setParameter("group", group);
        query.setParameter("date", date);
        query.setParameter("status", PaymentStatus.PAID);
        List<Payment> result = query.getResultList();
        return result;
    }

    public List<Payment> getLateUnpaidBeforeDate(Session session, Group group, LocalDateTime now) {
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group = :group AND p.dueDate < :now AND p.status = :status", Payment.class);
        query.setParameter("group", group);
        query.setParameter("now", now);
        query.setParameter("status", PaymentStatus.PENDING);
        List<Payment> result = query.getResultList();
        return result;
    }

    public List<Payment> getByPaymentType(Session session, PaymentBy type) {
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.paymentBy = :type", Payment.class);
        query.setParameter("type", type);
        List<Payment> result = query.getResultList();
        return result;
    }

    public BigDecimal getTotalAmountByType(Session session, PaymentBy type) {
        Query<BigDecimal> query = session.createQuery("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentBy = :type", BigDecimal.class);
        query.setParameter("type", type);
        BigDecimal result = query.getSingleResult();
        return result;
    }

    public List<Payment> getUpcomingPayments(Session session, Group group, LocalDateTime now) {
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group = :group AND p.dueDate > :now", Payment.class);
        query.setParameter("group", group);
        query.setParameter("now", now);
        List<Payment> result = query.getResultList();
        return result;
    }

    public List<Payment> getPaymentsByDateRange(Session session,LocalDateTime start, LocalDateTime end) {
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.createdAt BETWEEN :start AND :end", Payment.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        List<Payment> result = query.getResultList();
        return result;
    }

    public boolean areAllGroupPaymentsCompleted(Session session, Group group) {
        Query<Long> query = session.createQuery("SELECT COUNT(p) FROM Payment p WHERE p.group = :group AND p.status != :status", Long.class);
        query.setParameter("group", group);
        query.setParameter("status", PaymentStatus.PAID);
        boolean result = query.getSingleResult() == 0;
        return result;
    }

    public void markAsLate(Session session, Payment payment) {
       
            payment.setStatus(PaymentStatus.LATE);
            session.merge(payment);
      
    }

    public void markAsMissed(Session session, Payment payment) {
       
            payment.setStatus(PaymentStatus.MISSED);
            session.merge(payment);
      
    }

    public void markAsPending(Session session, Payment payment) {
       
            payment.setStatus(PaymentStatus.PENDING);
            session.merge(payment);
      
    }

    public List<Payment> getLatePayments(Session session) {
       
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.status = :status", Payment.class);
        query.setParameter("status", PaymentStatus.LATE);
        List<Payment> result = query.getResultList();
        return result;
    }

    public List<Payment> getAll(Session session) {
        Query<Payment> query = session.createQuery("FROM Payment", Payment.class);
        List<Payment> result = query.getResultList();
        return result;
    }

    public List<Payment> getMonthlyPayments(Session session) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        java.time.LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        Query<Payment> query = session.createQuery(
            "FROM Payment p WHERE p.createdAt >= :start AND p.createdAt < :end", Payment.class);
        query.setParameter("start", startOfMonth);
        query.setParameter("end", endOfMonth);
        List<Payment> result = query.getResultList();
        return result;
    }

    public void deleteAll(Session session) {
        session.createMutationQuery("DELETE FROM Payment").executeUpdate();
    }

    public List<Payment> getByParticipant(Session session,Long userId) {
        Query<Payment> query = session.createQuery(
            "SELECT p FROM Payment p " +
            "JOIN p.group g " +
            "JOIN g.participants part " +
            "WHERE part.user.id = :userId", 
            Payment.class
        );
        query.setParameter("userId", userId);
        List<Payment> result = query.getResultList();
        return result;
    }

    public List<Payment> getByGroupAndParticipant(Session session,Group group, Participant participant) {
        Query<Payment> query = session.createQuery(
            "FROM Payment p WHERE p.group = :group AND p.creator = :user", Payment.class
        );
        query.setParameter("group", group);
        query.setParameter("user", participant.getUser());
        List<Payment> payments = query.getResultList();
        return payments;
    }

    public List<Payment> getByRound(Session session,Round round) {
        Query<Payment> query = session.createQuery(
            "FROM Payment p WHERE p.round = :round", Payment.class
        );
        query.setParameter("round", round);
        List<Payment> payments = query.getResultList();
        return payments;
    }
}
