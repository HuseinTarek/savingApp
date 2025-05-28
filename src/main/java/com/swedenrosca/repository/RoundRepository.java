package com.swedenrosca.repository;

import com.swedenrosca.model.Participant;
import com.swedenrosca.model.Round;
import com.swedenrosca.model.Group;
import com.swedenrosca.model.User;
import com.swedenrosca.model.RoundStatus;
import org.hibernate.query.Query;
import org.hibernate.*;
import java.util.List;
import java.time.LocalDateTime;

public class RoundRepository {

    private final SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();

    public Round getById(Long id) {
        Session session = sessionFactory.openSession();
        Round round = session.get(Round.class, id);
        session.close();
        return round;
    }

    public List<Round> getAll() {
        Session session = sessionFactory.openSession();
        Query<Round> query = session.createQuery("FROM Round", Round.class);
        List<Round> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Round> getByGroup(Group group) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query<Round> query = session.createQuery(
                "FROM Round r WHERE r.group = :group", Round.class
        );
        query.setParameter("group", group);
        List<Round> rounds = query.getResultList();

        session.getTransaction().commit();
        session.close();
        return rounds;
    }

    public List<Round> getByWinnerUser(Participant winner) {
        Session session = sessionFactory.openSession();
        Query<Round> query = session.createQuery("FROM Round r WHERE r.winner = :winner", Round.class);
        query.setParameter("winner", winner);
        List<Round> result = query.getResultList();
        session.close();
        return result;
    }

    public Round save(Round round) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.merge(round);
        tx.commit();
        session.close();
        return round;
    }

    public Round update(Round round) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Round updated = session.merge(round);
        tx.commit();
        session.close();
        return updated;
    }

    public void delete(Round round) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.remove(session.contains(round) ? round : session.merge(round));
        tx.commit();
        session.close();
    }

    public void deleteAll() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.createQuery("DELETE FROM Round").executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<Round> getActiveRounds() {
        Session session = sessionFactory.openSession();
        Query<Round> query = session.createQuery("FROM Round r WHERE r.status = 'ACTIVE'", Round.class);
        List<Round> result = query.getResultList();
        session.close();
        return result;
    }

    public List<Round> getByStatus(RoundStatus status) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query<Round> query = session.createQuery(
                "FROM Round r WHERE r.status = :status", Round.class
        );
        query.setParameter("status", status);
        List<Round> rounds = query.getResultList();

        session.getTransaction().commit();
        session.close();
        return rounds;
    }

    public List<Round> getFutureRoundsByGroup(Long groupId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                String hql = "FROM Round r WHERE r.group.id = :groupId AND r.startDate > :now";
                List<Round> rounds = session.createQuery(hql, Round.class)
                    .setParameter("groupId", groupId)
                    .setParameter("now", LocalDateTime.now())
                    .list();
                transaction.commit();
                return rounds;
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public List<Round> getAllRoundsByGroupId(Long groupId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Round r WHERE r.group.id = :groupId ORDER BY r.roundNumber";
            return session.createQuery(hql, Round.class)
                .setParameter("groupId", groupId)
                .list();
        }
    }
}
