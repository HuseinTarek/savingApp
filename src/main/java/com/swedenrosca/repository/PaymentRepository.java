package com.swedenrosca.repository;

import com.swedenrosca.model.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentRepository {
    private final SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();


    public PaymentRepository() {
    }

    public List<Payment> getByGroup(Group group) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group = :group", Payment.class);
        query.setParameter("group", group);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getByPayer(Participant payer) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.payer = :payer", Payment.class);
        query.setParameter("payer", payer);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getByGroupPayer(Group group, User payer) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group = :group AND p.payer = :payer", Payment.class);
        query.setParameter("group", group);
        query.setParameter("payer", payer);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getByGroupPayerPaymentStatus(Group group, User payer, PaymentStatus status) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group = :group AND p.payer = :payer AND p.status = :status", Payment.class);
        query.setParameter("group", group);
        query.setParameter("payer", payer);
        query.setParameter("status", status);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getLatePaymentByGroupPayerDate(Group group, User payer, LocalDateTime date) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.status != :paidStatus AND p.dueDate < :date AND p.group = :group AND p.payer = :payer", Payment.class);
        query.setParameter("paidStatus", PaymentStatus.PAID);
        query.setParameter("date", date);
        query.setParameter("group", group);
        query.setParameter("payer", payer);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getByStatus(Group group, PaymentStatus status) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group = :group AND p.status = :status", Payment.class);
        query.setParameter("group", group);
        query.setParameter("status", status);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getPaidForGroup(Group group) {
        return getByStatus(group, PaymentStatus.PAID);
    }

    public List<Payment> getPendingPaymentForGroup(Group group) {
        return getByStatus(group, PaymentStatus.PENDING);
    }

    public List<Payment> getMissedForGroup(User payer, PaymentStatus status) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.payer = :payer AND p.status = :status", Payment.class);
        query.setParameter("payer", payer);
        query.setParameter("status", status);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getUnpaidPaymentsByUser(User payer, PaymentStatus status) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.payer = :payer AND p.status = :status", Payment.class);
        query.setParameter("payer", payer);
        query.setParameter("status", status);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getUnpaidPaymentsByUser(User payer) {
        return getUnpaidPaymentsByUser(payer, PaymentStatus.PENDING);
    }

    public Long countLatePaymentsByUser(User payer) {
        Session session = sessionFactory.openSession();
        Query<Long> query = session.createQuery("SELECT COUNT(p) FROM Payment p WHERE p.payer = :payer AND p.status = :status", Long.class);
        query.setParameter("payer", payer);
        query.setParameter("status", PaymentStatus.LATE);
        Long result = query.getSingleResult();
        session.close();
        return result;
    }

    public List<Payment> getByRecipient(User recipient) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.participant = :recipient", Payment.class);
        query.setParameter("recipient", recipient);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getByGroupRecipient(Group group, User recipient) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group = :group AND p.participant = :recipient", Payment.class);
        query.setParameter("group", group);
        query.setParameter("recipient", recipient);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public Payment save(Payment payment) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.persist(payment);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace(); 
        } finally {
            session.close();
        }
        return payment;
    }

    public Payment update(Payment payment) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Payment updated = session.merge(payment);
        tx.commit();
        session.close();
        return updated;
    }

    public void delete(Payment payment) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.remove(session.contains(payment) ? payment : session.merge(payment));
        tx.commit();
        session.close();
    }

    public Payment getById(Long id) {
        Session session = sessionFactory.openSession();
        Payment payment = session.get(Payment.class, id);
        session.close();
        return payment;
    }

    public List<Payment> listAll() {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment", Payment.class);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getByGroupId(Long groupId) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group.id = :groupId", Payment.class);
        query.setParameter("groupId", groupId);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getUnpaidAfterDate(Group group, LocalDateTime date) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group = :group AND p.dueDate > :date AND p.status != :status", Payment.class);
        query.setParameter("group", group);
        query.setParameter("date", date);
        query.setParameter("status", PaymentStatus.PAID);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getLateUnpaidBeforeDate(Group group, LocalDateTime now) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group = :group AND p.dueDate < :now AND p.status = :status", Payment.class);
        query.setParameter("group", group);
        query.setParameter("now", now);
        query.setParameter("status", PaymentStatus.PENDING);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getByPaymentType(PaymentBy type) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.paymentBy = :type", Payment.class);
        query.setParameter("type", type);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public BigDecimal getTotalAmountByType(PaymentBy type) {
        Session session = sessionFactory.openSession();
        Query<BigDecimal> query = session.createQuery("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentBy = :type", BigDecimal.class);
        query.setParameter("type", type);
        BigDecimal result = query.getSingleResult();
        session.close();
        return result;
    }

    public List<Payment> getUpcomingPayments(Group group, LocalDateTime now) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.group = :group AND p.dueDate > :now", Payment.class);
        query.setParameter("group", group);
        query.setParameter("now", now);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.createdAt BETWEEN :start AND :end", Payment.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public boolean areAllGroupPaymentsCompleted(Group group) {
        Session session = sessionFactory.openSession();
        Query<Long> query = session.createQuery("SELECT COUNT(p) FROM Payment p WHERE p.group = :group AND p.status != :status", Long.class);
        query.setParameter("group", group);
        query.setParameter("status", PaymentStatus.PAID);
        boolean result = query.getSingleResult() == 0;
        session.close();
        return result;
    }

    public void markAsLate(Payment payment) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        payment.setStatus(PaymentStatus.LATE);
        session.merge(payment);
        tx.commit();
    }


    public void markAsMissed(Payment payment) {
        Session session = SingletonSessionFactory.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        payment.setStatus(PaymentStatus.MISSED);
        session.merge(payment);
        tx.commit();
    }

    public void markAsPending(Payment payment) {
        Session session = SingletonSessionFactory.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        payment.setStatus(PaymentStatus.PENDING);
        session.merge(payment);
        tx.commit();
    }

    public List<Payment> findByRecipient(Session session, User recipient) {
        Query<Payment> query = session.createQuery(
                "FROM Payment p WHERE p.participant = :recipient", Payment.class);
        query.setParameter("recipient", recipient);
        return query.getResultList();
    }

    public List<Payment> getLatePayments() {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment p WHERE p.status = :status", Payment.class);
        query.setParameter("status", PaymentStatus.LATE);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getAll() {
        Session session = sessionFactory.openSession();
        Query<Payment> query = session.createQuery("FROM Payment", Payment.class);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getMonthlyPayments() {
        Session session = sessionFactory.openSession();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        java.time.LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        Query<Payment> query = session.createQuery(
            "FROM Payment p WHERE p.createdAt >= :start AND p.createdAt < :end", Payment.class);
        query.setParameter("start", startOfMonth);
        query.setParameter("end", endOfMonth);
        List<Payment> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Payment> getByParticipantAndMonth(Long participantId, int month) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Payment p WHERE p.participant.id = :participantId AND p.month = :month", 
                Payment.class)
                .setParameter("participantId", participantId)
                .setParameter("month", month)
                .getResultList();
        }
    }

    public void deleteAll() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.createQuery("DELETE FROM Payment").executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<Payment> getByParticipant(Long participantId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Payment p WHERE p.participant.id = :participantId", Payment.class)
                    .setParameter("participantId", participantId)
                    .list();
        }
    }

    public Payment getByParticipantAndRound(Long participantId, Long roundId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Payment p WHERE p.participant.id = :participantId AND p.round.id = :roundId", Payment.class)
                    .setParameter("participantId", participantId)
                    .setParameter("roundId", roundId)
                    .uniqueResult();
        }
    }

}
