package com.swedenrosca.service;

import com.swedenrosca.model.*;
import com.swedenrosca.repository.*;
import org.hibernate.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

public class RoundService {
    private static final Logger logger = LoggerFactory.getLogger(RoundService.class);
    private final SessionFactory sessionFactory;
    private final RoundRepository roundRepository;
    private final GroupRepository groupRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public RoundService(RoundRepository roundRepository, 
                       GroupRepository groupRepository,
                       ParticipantRepository participantRepository,
                       UserRepository userRepository) {
        this.sessionFactory = SingletonSessionFactory.getSessionFactory();
        this.roundRepository = roundRepository;
        this.groupRepository = groupRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
    }

    public List<Round> getAllRounds() {
        try (Session session = sessionFactory.openSession()) {
            return roundRepository.getAll(session);
        }
    }

    public Round createRound(Round round) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            roundRepository.save(session, round);
            session.getTransaction().commit();
            return round;
        }
    }

    public List<Round> getGroupRounds(Long groupId) {
        try (Session session = sessionFactory.openSession()) {
            Group group = groupRepository.getById(session, groupId);
            return roundRepository.getByGroup(session, group);
        }
    }

    public List<Round> getUserRounds(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            User user = userRepository.getById(session, userId);
            List<Participant> userParticipants = participantRepository.getByUser(session, user);
            return userParticipants.stream()
                .map(participant -> roundRepository.getByGroup(session, participant.getGroup()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        }
    }

    public Round getRoundById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return roundRepository.getById(session, id);
        }
    }

    public void updateRound(Round round) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            roundRepository.update(session, round);
            session.getTransaction().commit();
        }
    }

    public String deleteRound(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return roundRepository.deleteRound(session, id);
        }
    }

    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            roundRepository.deleteAll(session);
            session.getTransaction().commit();
        }
    }

    public String createRound(Long groupId, int turnOrder, BigDecimal amount) {
        try (Session session = sessionFactory.openSession()) {
            return roundRepository.createRound(session, groupId, turnOrder, amount);
        }
    }

    public String updateRound(Long id, BigDecimal amount, Group group, int turnOrder, LocalDateTime startDate,
            LocalDateTime endDate) {
        try (Session session = sessionFactory.openSession()) {
            return roundRepository.updateRound(session, id, amount, group, turnOrder, startDate, endDate);
        }
    }
} 