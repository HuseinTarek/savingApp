package com.swedenrosca.repository;

import com.swedenrosca.model.Group;
import com.swedenrosca.model.GroupRole;
import com.swedenrosca.model.Participant;
import com.swedenrosca.model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class ParticipantRepository {


    public Participant getById(Session session, Long id) {
        return session.get(Participant.class, id);
    }

    public List<Participant> getByGroup(Session session, Group group) {
        Query<Participant> query = session.createQuery("FROM Participant WHERE group = :group", Participant.class);
        query.setParameter("group", group);
        return query.getResultList();
    }

    public List<Participant> getByUser(Session session, User user) {
        Query<Participant> query = session.createQuery("FROM Participant WHERE user = :user", Participant.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    public void save(Session session, Participant participant) {
        session.persist(participant);
    }

    public void update(Session session, Participant participant) {
        session.merge(participant);
    }

    public void delete(Session session, Participant participant) {
        session.remove(session.contains(participant) ? participant : session.merge(participant));
    }

    public long countByGroupId(Session session, Long groupId) {
        return session.createQuery(
            "SELECT COUNT(p) FROM Participant p WHERE p.group.id = :gid", Long.class)
          .setParameter("gid", groupId)
          .getSingleResult();
    }

    public int countParticipantsByGroup(Session session, Long groupId) {
        Long count = session.createQuery(
                "SELECT COUNT(p) FROM Participant p WHERE p.group.id = :groupId", Long.class
        ).setParameter("groupId", groupId).uniqueResult();
        return count.intValue();
    }

    public Participant getByGroupAndTurnOrder(Session session, Group group, int turnOrder) {
        return session.createQuery(
                "FROM Participant p WHERE p.group = :group AND p.turnOrder = :turnOrder", Participant.class
        ).setParameter("group", group).setParameter("turnOrder", turnOrder).uniqueResult();
    }

    public void deleteAll(Session session) {
        // First get all participants to ensure they are loaded
        List<Participant> participants = session.createQuery("FROM Participant", Participant.class).getResultList();
        
        // Delete each participant
        for (Participant participant : participants) {
            session.remove(participant);
        }
    }

    public void deleteByGroup(Session session, Group group) {
        session.createMutationQuery("DELETE FROM Participant WHERE group = :group")
                .setParameter("group", group).executeUpdate();
    }

    public void deleteByUser(Session session, User user) {
        session.createMutationQuery("DELETE FROM Participant WHERE user = :user")
                .setParameter("user", user).executeUpdate();
    }

    public void deleteByGroupAndUser(Session session, Group group, User user) {
        session.createMutationQuery("DELETE FROM Participant WHERE group = :group AND user = :user")
                .setParameter("group", group).setParameter("user", user).executeUpdate();
    }

    public void deleteByGroupAndTurnOrder(Session session, Group group, int turnOrder) {
        session.createMutationQuery("DELETE FROM Participant WHERE group = :group AND turnOrder = :turnOrder")
                .setParameter("group", group).setParameter("turnOrder", turnOrder).executeUpdate();
    }

    public void deleteByUserAndTurnOrder(Session session, User user, int turnOrder) {
        session.createMutationQuery("DELETE FROM Participant WHERE user = :user AND turnOrder = :turnOrder")
                .setParameter("user", user).setParameter("turnOrder", turnOrder).executeUpdate();
    }

    public Participant getByRole(Session session, Group group, GroupRole role) {
        return session.createQuery(
                "FROM Participant p WHERE p.group = :group AND p.role = :role", Participant.class
        ).setParameter("group", group).setParameter("role", role).uniqueResult();
    }

    public boolean existsByUserIdAndGroup(Session session, Long userId, Long groupId) {
        String hql = "SELECT COUNT(p) FROM Participant p " +
                "WHERE p.user.id = :userId " +
                "AND p.group.id = :groupId";

        Long count = session.createQuery(hql, Long.class)
                .setParameter("userId", userId)
                .setParameter("groupId", groupId)
                .uniqueResult();

        return count > 0;
    }

    public Long getCountByGroup(Session session, Group group) {
        return session.createQuery(
            "SELECT COUNT(p) FROM Participant p WHERE p.group = :group", Long.class)
            .setParameter("group", group)
            .getSingleResult();
    }
}
