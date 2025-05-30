package com.swedenrosca.repository;

import com.swedenrosca.model.Participant;
import com.swedenrosca.model.Round;
import com.swedenrosca.model.Group;
import com.swedenrosca.model.User;
import com.swedenrosca.model.RoundStatus;
import org.hibernate.*;
import org.hibernate.query.Query;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RoundRepository {


    public Round getById(Session session, Long id) {
        return session.get(Round.class, id);
    }

    public List<Round> getAll(Session session) {
        Query<Round> query = session.createQuery("FROM Round", Round.class);
        return query.getResultList();
    }

    public List<Round> getByGroup(Session session, Group group) {
        Query<Round> query = session.createQuery("FROM Round WHERE group = :group", Round.class);
        query.setParameter("group", group);
        return query.getResultList();
    }

    public List<Round> getByWinnerUser(Session session, Participant winner) {
        Query<Round> query = session.createQuery("FROM Round r WHERE r.winner = :winner", Round.class);
        query.setParameter("winner", winner);
        return query.getResultList();
    }

    public void save(Session session, Round round) {
        session.persist(round);
    }

    public void update(Session session, Round round) {
        session.merge(round);
    }

    public void delete(Session session, Round round) {
        session.remove(session.contains(round) ? round : session.merge(round));
    }

    public void deleteAll(Session session) {
        session.createMutationQuery("DELETE FROM Round")
            .executeUpdate();
    }

    public List<Round> getActiveRounds(Session session) {
        Query<Round> query = session.createQuery("FROM Round r WHERE r.status = 'ACTIVE'", Round.class);
        return query.getResultList();
    }

    public List<Round> getByStatus(Session session, RoundStatus status) {
        Query<Round> query = session.createQuery("FROM Round r WHERE r.status = :status", Round.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    public List<Round> getFutureRoundsByGroup(Session session, Long groupId) {
        String hql = "FROM Round r WHERE r.group.id = :groupId AND r.startDate > :now";
        return session.createQuery(hql, Round.class)
            .setParameter("groupId", groupId)
            .setParameter("now", LocalDateTime.now())
            .list();
    }

    public List<Round> getAllRoundsByGroupId(Session session, Long groupId) {
        String hql = "FROM Round r WHERE r.group.id = :groupId ORDER BY r.roundNumber";
        return session.createQuery(hql, Round.class)
            .setParameter("groupId", groupId)
            .list();
    }

    public String createRound(Session session, Long groupId, int turnOrder, BigDecimal amount) {
      Query<Round> query = session.createQuery("INSERT INTO Round (groupId, turnOrder, amount) VALUES (:groupId, :turnOrder, :amount)", Round.class);
      query.setParameter("groupId", groupId);
      query.setParameter("turnOrder", turnOrder);
      query.setParameter("amount", amount);
      query.executeUpdate();
      return "Round created successfully";
    }

    public List<Round> getGroupRounds(Session session, Long groupId) {
        Query<Round> query = session.createQuery("FROM Round r WHERE r.group.id = :groupId ORDER BY r.roundNumber", Round.class);
        query.setParameter("groupId", groupId);
        return query.getResultList();
    }
    

    public Round getRoundById(Session session, Long id) {
        Query<Round> query = session.createQuery("FROM Round r WHERE r.id = :id", Round.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    public String updateRound(Session session, Long id, BigDecimal amount, Group group, int turnOrder,
      LocalDateTime startDate, LocalDateTime endDate) {
        Query<Round> query = session.createQuery("UPDATE Round r SET r.amount = :amount, r.group = :group, r.turnOrder = :turnOrder, r.startDate = :startDate, r.endDate = :endDate WHERE r.id = :id", Round.class);
        query.setParameter("id", id);
        query.setParameter("amount", amount);
        query.setParameter("group", group);
        query.setParameter("turnOrder", turnOrder);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.executeUpdate();
        return "Round updated successfully";
    }

    public String deleteRound(Session session, Long id) {
        Query<Round> query = session.createQuery("DELETE FROM Round r WHERE r.id = :id", Round.class);
        query.setParameter("id", id);
        query.executeUpdate();
        return "Round deleted successfully";
    }
}
