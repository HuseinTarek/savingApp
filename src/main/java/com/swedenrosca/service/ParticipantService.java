package com.swedenrosca.service;

import com.swedenrosca.model.*;
import com.swedenrosca.repository.*;
import org.hibernate.*;
import java.util.List;

public class ParticipantService {
    private final SessionFactory sessionFactory;
    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.sessionFactory = SingletonSessionFactory.getSessionFactory();
        this.participantRepository = participantRepository;
    }

    public void addParticipant(Participant participant) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            participantRepository.save(session, participant);
            session.getTransaction().commit();
        }
    }

    public void updateParticipant(Participant participant) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            participantRepository.update(session, participant);
            session.getTransaction().commit();
        }
    }

    public void deleteParticipant(Participant participant) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            participantRepository.delete(session, participant);
            session.getTransaction().commit();
        }
    }

    public Participant getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return participantRepository.getById(session, id);
        }
    }

    public List<Participant> getByGroup(Group group) {
        try (Session session = sessionFactory.openSession()) {
            return participantRepository.getByGroup(session, group);
        }
    }

    public List<Participant> getByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            return participantRepository.getByUser(session, user);
        }
    }

    public List<Participant> getAllParticipants() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Participant", Participant.class).getResultList();
        }
    }

    public void removeParticipantFromGroup(Group group, User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            participantRepository.deleteByGroupAndUser(session, group, user);
            session.getTransaction().commit();
        }
    }

    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            participantRepository.deleteAll(session);
            session.getTransaction().commit();
        }
    }

    public Participant getByGroupAndTurnOrder(Group group, int turnOrder) {
        try (Session session = sessionFactory.openSession()) {
            return participantRepository.getByGroupAndTurnOrder(session, group, turnOrder);
        }
    }

    public int countParticipantsByGroup(Long groupId) {
        try (Session session = sessionFactory.openSession()) {
            return participantRepository.countParticipantsByGroup(session, groupId);
        }
    }
} 