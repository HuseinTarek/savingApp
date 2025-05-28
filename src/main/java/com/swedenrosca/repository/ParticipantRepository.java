package com.swedenrosca.repository;


import com.swedenrosca.model.Group;
import com.swedenrosca.model.GroupRole;
import com.swedenrosca.model.Participant;
import com.swedenrosca.model.User;
import org.hibernate.query.Query;
import org.hibernate.*;
import java.util.List;

public class ParticipantRepository {
    private final SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();

    public Participant save(Participant participant) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.persist(participant);
        tx.commit();
        session.close();
        return participant;
    }

    public List<Participant> getByUser(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query<Participant> query = session.createQuery(
                "FROM Participant rp WHERE rp.user = :user", Participant.class
        );
        query.setParameter("user", user);
        List<Participant> result = query.getResultList();
        session.getTransaction().commit();
        session.close();
        return result;
    }


public long countByGroupId(Long groupId) {
    Session session = sessionFactory.openSession();
    long count = session.createQuery(
        "SELECT COUNT(p) FROM Participant p WHERE p.group.id = :gid", Long.class)
      .setParameter("gid", groupId)
      .getSingleResult();
    session.close();
    return count;
}



    public List<Participant> getByGroup(Group group) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query<Participant> query = session.createQuery(
                "FROM Participant rp WHERE rp.group = :group", Participant.class
        );
        query.setParameter("group", group);
        List<Participant> result = query.getResultList();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    public Participant getByUserAndGroup(User user, Group group) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query<Participant> query = session.createQuery(
                "FROM Participant rp WHERE rp.user = :user AND rp.group = :group", Participant.class
        );
        query.setParameter("user", user);
        query.setParameter("group", group);
        Participant participant = query.getSingleResult();
        session.getTransaction().commit();
        session.close();
        return participant;
    }

    public int countParticipantsByGroup(Long groupId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Long count = session.createQuery(
                "SELECT COUNT(p) FROM Participant p WHERE p.group.id = :groupId", Long.class
        ).setParameter("groupId", groupId).uniqueResult();
        session.getTransaction().commit();
        session.close();
        return count.intValue();
    }

    public Participant getByGroupAndTurnOrder(Group group, int turnOrder) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Participant participant = session.createQuery(
                "FROM Participant p WHERE p.group = :group AND p.turnOrder = :turnOrder", Participant.class
        ).setParameter("group", group).setParameter("turnOrder", turnOrder).uniqueResult();
        session.getTransaction().commit();
        session.close();
        return participant;
    }

    public Participant getById(Long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Participant participant = session.get(Participant.class, id);
        session.getTransaction().commit();
        session.close();
        return participant;
    }

    public void update(Participant participant) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.update(participant);
        session.getTransaction().commit();
        session.close();
    }

    public void delete(Participant participant) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.remove(participant);
        session.getTransaction().commit();
        session.close();
    }

    public List<Participant> getAll() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List<Participant> participants = session.createQuery("FROM Participant", Participant.class).getResultList();
        session.getTransaction().commit();
        session.close();
        return participants;
    }

    public void deleteAll() {
        System.out.println("Attempting to delete all participants...");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            
            // First get all participants to ensure they are loaded
            List<Participant> participants = session.createQuery("FROM Participant", Participant.class).getResultList();
            
            // Delete each participant
            for (Participant participant : participants) {
                session.remove(participant);
            }
            
            // Flush and commit
            session.flush();
            transaction.commit();
            System.out.println("Deleted " + participants.size() + " participants.");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deleting participants: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
            System.out.println("deleteAll participants method finished.");
        }
    }

    public void deleteByGroup(Group group) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createQuery("DELETE FROM Participant WHERE group = :group")
                .setParameter("group", group).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    public void deleteByUser(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createQuery("DELETE FROM Participant WHERE user = :user")
                .setParameter("user", user).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    public void deleteByGroupAndUser(Group group, User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createQuery("DELETE FROM Participant WHERE group = :group AND user = :user")
                .setParameter("group", group).setParameter("user", user).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    public void deleteByGroupAndTurnOrder(Group group, int turnOrder) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createQuery("DELETE FROM Participant WHERE group = :group AND turnOrder = :turnOrder")
                .setParameter("group", group).setParameter("turnOrder", turnOrder).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    public void deleteByUserAndTurnOrder(User user, int turnOrder) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createQuery("DELETE FROM Participant WHERE user = :user AND turnOrder = :turnOrder")
                .setParameter("user", user).setParameter("turnOrder", turnOrder).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    public Participant getByRole(Group group, GroupRole role) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Participant participant = session.createQuery(
                "FROM Participant p WHERE p.group = :group AND p.role = :role", Participant.class
        ).setParameter("group", group).setParameter("role", role).uniqueResult();
        session.getTransaction().commit();
        session.close();
        return participant;
    }

    public boolean existsByUserIdAndGroup(Long userId, Long groupId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        String hql = "SELECT COUNT(p) FROM Participant p " +
                "WHERE p.user.id = :userId " +
                "AND p.group.id = :groupId";

        Long count = session.createQuery(hql, Long.class)
                .setParameter("userId", userId)
                .setParameter("groupId", groupId)
                .uniqueResult();

        session.getTransaction().commit();
        session.close();

        return count > 0;
    }

    public Long getCountByGroup(Group group) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "SELECT COUNT(p) FROM Participant p WHERE p.group = :group", Long.class)
                .setParameter("group", group)
                .getSingleResult();
        } finally {
            session.close();
        }
    }

}
