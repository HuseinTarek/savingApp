package com.swedenrosca.service;

import com.swedenrosca.model.*;
import com.swedenrosca.repository.*;
import org.hibernate.*;
import java.util.List;
import java.util.Collection;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final SessionFactory sessionFactory;
    private final PaymentRepository paymentRepository;
    private final GroupRepository groupRepository;
    private final ParticipantRepository participantRepository;

    public PaymentService(PaymentRepository paymentRepository,
                         GroupRepository groupRepository,
                         ParticipantRepository participantRepository) {
        this.sessionFactory = SingletonSessionFactory.getSessionFactory();
        this.paymentRepository = paymentRepository;
        this.groupRepository = groupRepository;
        this.participantRepository = participantRepository;
    }

    public List<Payment> getByGroupId(Long groupId) {
        try (Session session = sessionFactory.openSession()) {
            Group group = groupRepository.getById(session, groupId);
            return paymentRepository.getByGroup(session, group);
        }
    }

    public Payment getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return paymentRepository.getById(session, id);
        }
    }

    public void save(Payment payment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            paymentRepository.save(session, payment);
            session.getTransaction().commit();
        }
    }

    public void update(Payment payment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            paymentRepository.update(session, payment);
            session.getTransaction().commit();
        }
    }

    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Payment payment = paymentRepository.getById(session, id);
            if (payment != null) {
                paymentRepository.delete(session, payment);
            }
            session.getTransaction().commit();
        }
    }

    public List<Payment> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return paymentRepository.getAll(session);
        }
    }

    public void createPayment(Payment payment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            paymentRepository.save(session, payment);
            session.getTransaction().commit();
        }
    }

    public void updatePayment(Payment payment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            paymentRepository.update(session, payment);
            session.getTransaction().commit();
        }
    }

    public void deletePayment(Payment payment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            paymentRepository.delete(session, payment);
            session.getTransaction().commit();
        }
    }

    public List<Payment> getByGroup(Group group) {
        try (Session session = sessionFactory.openSession()) {
            return paymentRepository.getByGroup(session, group);
        }
    }

    public List<Payment> getByParticipant(Long participantId) {
        try (Session session = sessionFactory.openSession()) {
            return paymentRepository.getByParticipant(session, participantId);
        }
    }

    public List<Payment> getByStatus(Group group, PaymentStatus status) {
        try (Session session = sessionFactory.openSession()) {
            return paymentRepository.getByStatus(session, group, status);
        }
    }

    public List<Payment> getByRound(Round round) {
        try (Session session = sessionFactory.openSession()) {
            return paymentRepository.getByRound(session, round);
        }
    }

    public List<Payment> getAllPayments() {
        try (Session session = sessionFactory.openSession()) {
            return paymentRepository.listAll(session);
        }
    }

    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            paymentRepository.deleteAll(session);
            session.getTransaction().commit();
        }
    }

    public Collection<? extends Payment> getByGroupAndParticipant(Group group, Participant participant) {
        try (Session session = sessionFactory.openSession()) {
            return paymentRepository.getByGroupAndParticipant(session, group, participant);
        }
    }
} 