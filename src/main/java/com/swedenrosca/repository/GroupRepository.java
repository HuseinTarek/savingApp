package com.swedenrosca.repository;

import com.swedenrosca.model.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.*;

public class GroupRepository {

    public Group getById(Session session, Long id) {
        return session.get(Group.class, id);
    }

    public List<Group> getAll(Session session) {
        return session.createQuery("FROM Group", Group.class).getResultList();
    }

    public List<Group> getActiveGroups(Session session) {
        Query<Group> query = session.createQuery("FROM Group WHERE status = :status", Group.class);
        query.setParameter("status", GroupStatus.ACTIVE);
        return query.getResultList();
    }

    public List<Integer> getAvailableTurnOrders1(Long groupId) {
        Session session = SingletonSessionFactory.getSessionFactory().openSession();

        Group group = session.get(Group.class, groupId);
        int max = group.getMaxMembers();

        List<Integer> allOrders = new ArrayList<>();
        for (int i = 1; i <= max; i++) {
            allOrders.add(i);
        }

        Query<Integer> query = session.createQuery(
                "SELECT p.turnOrder FROM Participant p WHERE p.group = :group", Integer.class);
        query.setParameter("group", group);
        List<Integer> taken = query.getResultList();

        allOrders.removeAll(taken);
        session.close();
        return allOrders;
    }

    public List<Integer> getAvailableTurnOrders(Session session, Long groupId) {
        Group group = session.get(Group.class, groupId);
        List<Integer> takenOrders = new ArrayList<>();
        for (Participant participant : group.getParticipants()) {
            takenOrders.add(participant.getTurnOrder());
        }
        List<Integer> availableOrders = new ArrayList<>();
        for (int i = 1; i <= group.getMaxMembers(); i++) {
            if (!takenOrders.contains(i)) {
                availableOrders.add(i);
            }
        }
        return availableOrders;
    }

    public List<Group> getOpen(Session session) {
        Query<Group> query = session.createQuery(
            "FROM Group WHERE status = :status", Group.class
        );
        query.setParameter("status", GroupStatus.WAITING_FOR_MEMBERS);
        return query.getResultList();
    }

    public List<Group> getCompleted(Session session) {
        Query<Group> query = session.createQuery(
            "FROM Group WHERE status = :status", Group.class
        );
        query.setParameter("status", GroupStatus.COMPLETED);
        return query.getResultList();
    }

    public List<Group> getBlocked(Session session) {
       

        // Query to retrieve all groups with status BLOCKED
        Query<Group> query = session.createQuery(
                "FROM Group g WHERE g.status = :status", Group.class
        );
        query.setParameter("status", GroupStatus.BLOCKED);

        List<Group> blockedGroups = query.getResultList();


        return blockedGroups;
    }

    public void save(Session session, Group group) {
        session.persist(group);
    }

    public void update(Session session, Group group) {
        session.merge(group);
    }

    public void delete(Session session, Group group) {
        session.remove(session.contains(group) ? group : session.merge(group));
    }

    public void addParticipant(Session session,Group group, User user, int turnOrder, GroupRole role) {
        // First, get the current state of the group with its participants
        Group currentGroup = session.createQuery(
                "SELECT g FROM Group g LEFT JOIN FETCH g.participants WHERE g.id = :id", Group.class)
                .setParameter("id", group.getId())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (currentGroup == null) {
            throw new IllegalArgumentException("Group not found with id: " + group.getId());
        }

        // Create a new participant entity
        Participant participant = new Participant();
        participant.setUser(user);
        participant.setGroup(currentGroup);
        participant.setTurnOrder(turnOrder);
        participant.setRole(role);
        participant.setPaymentStatus(PaymentStatus.PENDING);
        participant.setPaymentBy(PaymentBy.USER_PAYMENT);

        // Persist the participant to the database
        session.persist(participant);
    }

    public void deleteParticipantFromGroup(Session session,Group group, User user) {
        // Query to find the participant by group and user
        String hql = "FROM Participant p WHERE p.group = :group AND p.user = :user";
        Participant participant = session.createQuery(hql, Participant.class)
                .setParameter("group", group)
                .setParameter("user", user)
                .uniqueResult();

        // If found, remove the participant from the session
        if (participant != null) {
            session.remove(participant);
        }
    }

    public List<Group> getByMember(Session session,Long userId) {
        return session.createQuery("SELECT g FROM Group g JOIN g.participants p WHERE p.user.id = :userId", Group.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Group> getByMonthlyPayment(Session session,BigDecimal monthlyContribution) {
        // Query to find all groups with the specified monthly payment amount
        Query<Group> query = session.createQuery(
                "SELECT g FROM Group g WHERE g.monthlyPayment = :monthlyPayment", Group.class
        );
        query.setParameter("monthlyPayment", monthlyContribution);

        List<Group> groups = query.getResultList();

        return groups;
    }

    public List<Group> getByNumberOfMembers(Session session,int numberOfMembers) {
        // Query to find groups by exact number of members
        Query<Group> query = session.createQuery(
                "SELECT g FROM Group g WHERE g.maxMembers = :numberOfMembers", Group.class
        );
        query.setParameter("numberOfMembers", numberOfMembers);
        List<Group> groups = query.getResultList();
        return groups;
    }

    public List<Group> getByTotalAmount(Session session,BigDecimal totalAmount) {
        // Query to find groups by exact total amount
        Query<Group> query = session.createQuery(
                "SELECT g FROM Group g WHERE g.totalAmount = :totalAmount", Group.class
        );
        query.setParameter("totalAmount", totalAmount);

        List<Group> groups = query.getResultList();

        return groups;
    }

    public List<Group> getByContributionAndMembersAndStatus(Session session,BigDecimal monthlyPayment,
                                                            int monthsCount, GroupStatus status) {
        // First get payment plans matching the criteria
        Query<Group> query = session.createQuery(
                "SELECT DISTINCT g FROM Group g " +
                        "JOIN g.paymentPlan p " +
                        "WHERE p.monthlyPayment = :payment " +
                        "AND p.monthsCount = :months " +
                        "AND g.status = :status", Group.class
        );

        query.setParameter("payment", monthlyPayment);
        query.setParameter("months", monthsCount);
        query.setParameter("status", status);

        List<Group> groups = query.getResultList();

        return groups;
    }

    public List<Group> getPendingApprovalGroups(Session session) {
        Query<Group> query = session.createQuery("FROM Group WHERE status = :status", Group.class);
        query.setParameter("status", GroupStatus.PENDING_APPROVAL);
        return query.getResultList();
    }

    public List<Participant> getParticipantsInGroup(Session session,Long id) {
        Group group = session.get(Group.class, id);
        List<Participant> participants = group.getParticipants();
        return participants;
    }

    public List<PaymentPlan> findByPaymentAndMonths(Session session,BigDecimal monthlyPayment, int monthsCount) {
        Query<PaymentPlan> query = session.createQuery(
                "FROM PaymentPlan p WHERE p.monthlyPayment = :payment AND p.monthsCount = :months", 
                PaymentPlan.class
        );
        query.setParameter("payment", monthlyPayment);
        query.setParameter("months", monthsCount);

        List<PaymentPlan> plans = query.getResultList();
        
        return plans;
    }

    public List<Group> findAvailableGroupsByPaymentPlan(Session session,PaymentPlan paymentPlan, GroupStatus status) {
        // First get the group IDs that have available space
        Query<Long> groupQuery = session.createQuery(
                "SELECT g.id FROM Group g " +
                        "LEFT JOIN g.participants p " +
                        "WHERE g.monthlyContribution = :monthlyPayment " +
                        "AND g.maxMembers = :monthsCount " +
                        "AND g.status = :status " +
                        "GROUP BY g.id, g.maxMembers " +
                        "HAVING COUNT(p) < g.maxMembers", Long.class
        );

        groupQuery.setParameter("monthlyPayment", paymentPlan.getMonthlyPayment());
        groupQuery.setParameter("monthsCount", paymentPlan.getMonthsCount());
        groupQuery.setParameter("status", status);

        List<Long> groupIds = groupQuery.getResultList();

        // Then fetch the complete groups with their participants
        List<Group> groups = new ArrayList<>();
        if (!groupIds.isEmpty()) {
            Query<Group> fullQuery = session.createQuery(
                    "SELECT DISTINCT g FROM Group g " +
                            "LEFT JOIN FETCH g.participants " +
                            "WHERE g.id IN :groupIds", Group.class
            );
            fullQuery.setParameter("groupIds", groupIds);
            groups = fullQuery.getResultList();
        }

        return groups;
    }

    public List<Group> getWaitingForMembersGroups(Session session) {
        Query<Group> query = session.createQuery(
                "FROM Group g WHERE g.status = :status", Group.class
        );
        query.setParameter("status", GroupStatus.WAITING_FOR_MEMBERS);
        
        List<Group> waitingGroups = query.getResultList();
        
        return waitingGroups;
    }

    public void deleteAll(Session session) {
        session.createMutationQuery("DELETE FROM Group").executeUpdate();
    }

    public void updateGroupStatusIfFull(Session session, Group group) {
        if (group.getParticipants().size() == group.getMaxMembers() &&
            group.getStatus() == GroupStatus.WAITING_FOR_MEMBERS) {
            group.setStatus(GroupStatus.PENDING_APPROVAL);
            session.merge(group);
        }
    }

    public void activateGroup(Session session, Group group) {
        group.setStatus(GroupStatus.ACTIVE);
        session.merge(group);
    }
}