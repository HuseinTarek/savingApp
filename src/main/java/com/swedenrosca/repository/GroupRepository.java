package com.swedenrosca.repository;

import com.swedenrosca.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.*;

    public class GroupRepository {

    private final SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();

        public Group getById(Long id) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            try {
                Group group = session.createQuery(
                        "SELECT g FROM Group g LEFT JOIN FETCH g.participants WHERE g.id = :id", Group.class)
                        .setParameter("id", id)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                session.getTransaction().commit();
                return group;
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            } finally {
                session.close();
            }
        }

        public List<Group> getAll() {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Query to retrieve all group records
            Query<Group> query = session.createQuery("FROM Group", Group.class);
            List<Group> groups = query.getResultList();

            session.getTransaction().commit();
            session.close();

            return groups;
        }

        public List<Group> getActiveGroups() {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            Query<Group> query = session.createQuery(
                    "FROM Group g WHERE g.status = :status", Group.class
            );
            query.setParameter("status", GroupStatus.ACTIVE);

            List<Group> activeGroups = query.getResultList();

            session.getTransaction().commit();
            session.close();

            return activeGroups;
        }

        public List<Integer> getAvailableTurnOrders1(Long groupId) {
            
            Session session = sessionFactory.openSession();

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

        public List<Integer> getAvailableTurnOrders(Long groupId) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            Group group=session.get(Group.class, groupId);
            int maxParticipant=group.getMaxMembers();
            Query<Integer> query=session.createQuery("SELECT p.turnOrder FROM Participant p WHERE p.group.id=:id",Integer.class);
            query.setParameter("id", groupId);
            List<Integer> takenTurnOrders=query.getResultList();
            List<Integer> availableTurnOrders=new ArrayList<>();
            for(int i=1;i<=maxParticipant;i++){
                if(!takenTurnOrders.contains(i)){
                    availableTurnOrders.add(i);
                }
            }
            session.getTransaction().commit();
            session.close();
            return availableTurnOrders;
        }


        public List<Group> getOpen() {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Query to retrieve all groups with status OPEN
            Query<Group> query = session.createQuery(
                    "FROM Group g WHERE g.status = :status", Group.class
            );
            query.setParameter("status", GroupStatus.PENDING_APPROVAL);

            List<Group> openGroups = query.getResultList();

            session.getTransaction().commit();
            session.close();

            return openGroups;
        }


        public List<Group> getCompleted() {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Query to retrieve all groups with status COMPLETED
            Query<Group> query = session.createQuery(
                    "FROM Group g WHERE g.status = :status", Group.class
            );
            query.setParameter("status", GroupStatus.COMPLETED);

            List<Group> completedGroups = query.getResultList();

            session.getTransaction().commit();
            session.close();

            return completedGroups;
        }

        public List<Group> getBlocked() {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Query to retrieve all groups with status BLOCKED
            Query<Group> query = session.createQuery(
                    "FROM Group g WHERE g.status = :status", Group.class
            );
            query.setParameter("status", GroupStatus.BLOCKED);

            List<Group> blockedGroups = query.getResultList();

            session.getTransaction().commit();
            session.close();

            return blockedGroups;
        }

        public Group save(Group group) {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                session.persist(group);
                session.getTransaction().commit();
                return group;
            } catch (Exception e) {
                throw e;
            }
        }


        public Group update(Group group) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            try {
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

                // Update the status
                currentGroup.setStatus(group.getStatus());
                
                // Ensure changes are flushed to the database
                session.flush();
                session.getTransaction().commit();
                
                // Return the updated group
                return currentGroup;
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            } finally {
                session.close();
            }
        }


        // Delete a group by opening and managing the session inside the method
        public void delete(Group group) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            Group managedGroup = session.merge(group); // Ensure the entity is managed before deletion
            session.remove(managedGroup); // Remove the entity from the session
            session.flush(); // Apply the deletion to the database

            session.getTransaction().commit(); // Commit the transaction
            session.close(); // Close the session
        }

        public void addParticipant(Group group, User user, int turnOrder, GroupRole role) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            try {
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
                
                // Ensure changes are flushed to the database
                session.flush();
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            } finally {
                session.close();
            }
        }

        public void deleteParticipantFromGroup(Group group, User user) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Query to find the participant by group and user
            String hql = "FROM Participant p WHERE p.group = :group AND p.user = :user";
            Participant participant = session.createQuery(hql, Participant.class)
                    .setParameter("group", group)
                    .setParameter("user", user)
                    .uniqueResult();

            // If found, remove the participant from the session
            if (participant != null) {
                session.remove(participant);
                session.flush(); // Apply the deletion
            }

            session.getTransaction().commit(); // Commit the transaction
            session.close(); // Close the session
        }

        public List<Group> getByMember(Long userId) {
            try (Session session = sessionFactory.openSession()) {
                return session.createQuery("SELECT g FROM Group g JOIN g.participants p WHERE p.user.id = :userId", Group.class)
                        .setParameter("userId", userId)
                        .getResultList();
            }
        }

        public List<Group> getByMonthlyPayment(BigDecimal monthlyContribution) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Query to find all groups with the specified monthly payment amount
            Query<Group> query = session.createQuery(
                    "SELECT g FROM Group g WHERE g.monthlyPayment = :monthlyPayment", Group.class
            );
            query.setParameter("monthlyPayment", monthlyContribution);

            List<Group> groups = query.getResultList();

            session.getTransaction().commit(); // Commit the transaction
            session.close(); // Close the session

            return groups;
        }

        public List<Group> getByNumberOfMembers(int numberOfMembers) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Query to find groups by exact number of members
            Query<Group> query = session.createQuery(
                    "SELECT g FROM Group g WHERE g.maxMembers = :numberOfMembers", Group.class
            );
            query.setParameter("numberOfMembers", numberOfMembers);

            List<Group> groups = query.getResultList();

            session.getTransaction().commit();
            session.close();

            return groups;
        }
        public List<Group> getByTotalAmount(BigDecimal totalAmount) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            // Query to find groups by exact total amount
            Query<Group> query = session.createQuery(
                    "SELECT g FROM Group g WHERE g.totalAmount = :totalAmount", Group.class
            );
            query.setParameter("totalAmount", totalAmount);

            List<Group> groups = query.getResultList();

            session.getTransaction().commit();
            session.close();

            return groups;
        }

        public List<Group> getByContributionAndMembersAndStatus(BigDecimal monthlyPayment,
                                                                int monthsCount, GroupStatus status) {

            Session session = sessionFactory.openSession();
            session.beginTransaction();

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

            session.getTransaction().commit();
            session.close();

            return groups;
        }


        public List<Group> getPendingApprovalGroups() {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            // Query to retrieve all groups with status OPEN
            Query<Group> query = session.createQuery(
                    "FROM Group g WHERE g.status = :status", Group.class
            );
            query.setParameter("status", GroupStatus.PENDING_APPROVAL);
                        List<Group> openGroups = query.getResultList();
            session.getTransaction().commit();
            session.close();
            return openGroups;

        }

        public List<Participant> getParticipantsInGroup(Long id) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            Group group = session.get(Group.class, id);
            List<Participant> participants = group.getParticipants();
            session.getTransaction().commit();
            session.close();
            return participants;
        }
    public List<PaymentPlan> findByPaymentAndMonths(BigDecimal monthlyPayment, int monthsCount) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query<PaymentPlan> query = session.createQuery(
            "FROM PaymentPlan p WHERE p.monthlyPayment = :payment AND p.monthsCount = :months", 
            PaymentPlan.class
        );
        query.setParameter("payment", monthlyPayment);
        query.setParameter("months", monthsCount);

        List<PaymentPlan> plans = query.getResultList();
        
        session.getTransaction().commit();
        session.close();
        
        return plans;
    }

    public List<Group> findAvailableGroupsByPaymentPlan(PaymentPlan paymentPlan, GroupStatus status) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

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

        session.getTransaction().commit();
        session.close();

        return groups;
    }

    public List<Group> getWaitingForMembersGroups() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        
        Query<Group> query = session.createQuery(
                "FROM Group g WHERE g.status = :status", Group.class
        );
        query.setParameter("status", GroupStatus.WAITING_FOR_MEMBERS);
        
        List<Group> waitingGroups = query.getResultList();
        
        session.getTransaction().commit();
        session.close();
        
        return waitingGroups;
    }

    public void deleteAll() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.createQuery("DELETE FROM Group").executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}