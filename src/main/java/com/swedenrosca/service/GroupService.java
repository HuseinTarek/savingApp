package com.swedenrosca.service;

import com.swedenrosca.model.*;
import com.swedenrosca.repository.*;
import org.hibernate.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupService {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final SessionFactory sessionFactory;
    private final GroupRepository groupRepository;
    private final ParticipantRepository participantRepository;
    private final PaymentPlanRepository paymentPlanRepository;
    private final RoundRepository roundRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public GroupService(SessionFactory sessionFactory, GroupRepository groupRepository, 
                       ParticipantRepository participantRepository, 
                       PaymentPlanRepository paymentPlanRepository,
                       RoundRepository roundRepository,
                       PaymentRepository paymentRepository,
                       UserRepository userRepository) {
        this.sessionFactory = sessionFactory;
        this.groupRepository = groupRepository;
        this.participantRepository = participantRepository;
        this.paymentPlanRepository = paymentPlanRepository;
        this.roundRepository = roundRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public List<Group> findAvailableGroupsByPaymentPlan(PaymentPlan plan, GroupStatus status) {
        try (Session session = sessionFactory.openSession()) {
            return groupRepository.findAvailableGroupsByPaymentPlan(session, plan, status);
        }
    }

    public Group createGroup(PaymentPlan plan, BigDecimal monthlyContribution, int maxMembers) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            if (plan.getId() == null) {
                paymentPlanRepository.save(session, plan);
            }
            Group group = new Group();
            group.setPaymentPlan(plan);
            group.setMonthlyContribution(monthlyContribution);
            group.setMaxMembers(maxMembers);
            group.setStatus(GroupStatus.WAITING_FOR_MEMBERS);
            group.setPaymentBy(PaymentBy.USER_PAYMENT);
            group.setStartDate(java.time.LocalDateTime.now());
            group.setEndDate(java.time.LocalDateTime.now().plusMonths(maxMembers));
            group.setTotalAmount(monthlyContribution.multiply(java.math.BigDecimal.valueOf(maxMembers)));
            group.setGroupName(group.generateGroupName(group.getStartDate(), group.getEndDate(), group.getTotalAmount()));
            groupRepository.save(session, group);
            session.getTransaction().commit();
            return group;
        }
    }

    public void joinGroup(User user, Group group, int turnOrder) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            // Reload group to attach to this session
            Group attachedGroup = groupRepository.getById(session, group.getId());
            Participant participant = new Participant();
            participant.setUser(user);
            participant.setGroup(attachedGroup);
            participant.setPaymentBy(PaymentBy.USER_PAYMENT);
            participant.setRole(GroupRole.PAYER);
            participant.setTurnOrder(turnOrder);
            participantRepository.save(session, participant);
            session.getTransaction().commit();
        }
    }

    public void updateGroup(Group group) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            groupRepository.update(session, group);
            session.getTransaction().commit();
        }
    }

    public Group getGroupById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return groupRepository.getById(session, id);
        }
    }

    public void createGroup(Group group) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            groupRepository.save(session, group);
            session.getTransaction().commit();
        }
    }

    public List<Group> getAllActiveGroups() {
        try (Session session = sessionFactory.openSession()) {
            return groupRepository.getActiveGroups(session);
        }
    }

    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            groupRepository.deleteAll(session);
            session.getTransaction().commit();
        }
    }

    public List<Group> getAllPendingApprovalGroups() {
        try (Session session = sessionFactory.openSession()) {
            return groupRepository.getPendingApprovalGroups(session);
        }
    }

    public List<Group> getAllWaitingForMembersGroups() {
        try (Session session = sessionFactory.openSession()) {
            return groupRepository.getWaitingForMembersGroups(session);
        }
    }

    public List<Group> getByMember(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return groupRepository.getByMember(session, id);
        }
    }

    public List<Group> getAllGroups() {
        try (Session session = sessionFactory.openSession()) {
            return groupRepository.getAll(session);
        }
    }

    public void updateGroupStatusIfFull(Group group) {  
        try (Session session = sessionFactory.openSession()) {
            groupRepository.updateGroupStatusIfFull(session, group);
        }
    }

    public void activateGroup(Group group) {
        try (Session session = sessionFactory.openSession()) {  
            session.beginTransaction();
            groupRepository.activateGroup(session, group);
            session.getTransaction().commit();
        }
    }

    public String removeMember(Long groupId, Long userId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            groupRepository.deleteParticipantFromGroup(session, 
                groupRepository.getById(session, groupId), 
                userRepository.getById(session, userId));
            session.getTransaction().commit();
            return "Member removed successfully!";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String deactivateGroup(Long groupId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deactivateGroup'");
    }

    public void addMemberToGroup(Long groupId, Long userId, Long paymentPlanId, Integer requestedTurnOrder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addMemberToGroup'");
    }

    public String createGroup(User creator, BigDecimal monthlyContribution) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Group group = new Group();
            group.setCreator(creator);
            group.setMonthlyContribution(monthlyContribution);
            group.setStatus(GroupStatus.WAITING_FOR_MEMBERS);
            group.setPaymentBy(PaymentBy.USER_PAYMENT);
            // Set other required fields as needed
            groupRepository.save(session, group);
            session.getTransaction().commit();
            return "Group created successfully";
        }
    }

    public void initializeGroupPayments(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Group must not be null");
        }
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            // Refresh group from DB to get latest participants and rounds
            Group freshGroup = groupRepository.getById(session, group.getId());
            if (freshGroup == null) {
                throw new IllegalStateException("Group not found");
            }
            if (freshGroup.getStatus() != GroupStatus.PENDING_APPROVAL) {
                throw new IllegalStateException("Group must be in PENDING_APPROVAL to activate");
            }
            freshGroup.setStatus(GroupStatus.ACTIVE);
            groupRepository.update(session, freshGroup);
            logger.info("Group {} status set to ACTIVE", freshGroup.getGroupName());

            // Get all participants and rounds
            List<Participant> participants = freshGroup.getParticipants();
            List<Round> rounds = roundRepository.getByGroup(session, freshGroup);

            for (Participant participant : participants) {
                for (Round round : rounds) {
                    Payment payment = new Payment();
                    payment.setGroup(freshGroup);
                    payment.setRound(round);
                    payment.setAmount(freshGroup.getMonthlyContribution());
                    payment.setPaymentStatus(PaymentStatus.PENDING);
                    payment.setDueDate(round.getStartDate().plusDays(5));
                    payment.setCreatedAt(LocalDateTime.now());
                    payment.setServiceFee(BigDecimal.ZERO);

                    if (round.getRoundNumber() == participant.getTurnOrder()) {
                        round.setWinnerParticipant(participant);
                        roundRepository.update(session, round);
                        logger.info("Set participant {} as winner for round {}", participant.getUser().getUsername(), round.getRoundNumber());
                    }

                    logger.info("Preparing to save payment: groupId={}, roundId={}, participantId={}, payerId={}, amount={}, status={}, dueDate={}, createdAt={}, paymentBy={}",
                        freshGroup.getId(),
                        round.getId(),
                        participant.getId(),
                        participant.getId(),
                        freshGroup.getMonthlyContribution(),
                        PaymentStatus.PENDING,
                        round.getStartDate().plusDays(5),
                        LocalDateTime.now(),
                        PaymentBy.USER_PAYMENT
                    );

                    try {
                        paymentRepository.save(session, payment);
                        logger.info("Saved payment: groupId={}, roundId={}, participantId={}", 
                            freshGroup.getId(), round.getId(), participant.getId());
                    } catch (Exception e) {
                        logger.error("Failed to save payment: groupId={}, roundId={}, participantId={}. Exception: {}",
                            freshGroup.getId(), round.getId(), participant.getId(), e.getMessage(), e);
                    }
                }
            }
            session.getTransaction().commit();
            logger.info("Group {} activated: all payments and winners set.", freshGroup.getGroupName());
        }
    }

    public void addUserToGroupAsParticipant(Group group, User user, PaymentPlan paymentPlan, int requestedTurnOrder) {
        logger.info("Starting to add user {} to group {}", user.getUsername(), group.getGroupName());
        logger.info("Current group status: {}, members: {}/{}", 
            group.getStatus(), 
            group.getParticipants().size(), 
            group.getMaxMembers());

        if (group == null || user == null || paymentPlan == null) {
            throw new IllegalArgumentException("Group, user and payment plan must not be null");
        }
    
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            
            // Check group status
            if (!group.getStatus().equals(GroupStatus.WAITING_FOR_MEMBERS)) {
                throw new IllegalStateException("Cannot add user to group with status: " + group.getStatus());
            }
        
            // Check if group is full
            if (group.getParticipants().size() >= group.getMaxMembers()) {
                throw new IllegalStateException("Group is full: " + group.getGroupName());
            }
        
            // Check if user is already in group
            boolean alreadyInGroup = group.getParticipants().stream()
                    .anyMatch(p -> p.getUser().getId().equals(user.getId()));
            if (alreadyInGroup) {
                throw new IllegalStateException("User already in group: " + user.getUsername());
            }
        
            // Validate requested turn order
            if (requestedTurnOrder <= 0 || requestedTurnOrder > group.getMaxMembers()) {
                throw new IllegalArgumentException("Invalid turn order requested: " + requestedTurnOrder);
            }
        
            boolean turnOrderTaken = group.getParticipants().stream()
                    .anyMatch(p -> p.getTurnOrder() == requestedTurnOrder);
            if (turnOrderTaken) {
                throw new IllegalStateException("Turn order " + requestedTurnOrder + " is already taken");
            }
        
            // Create new participant
            Participant participant = new Participant();
            participant.setUser(user);
            participant.setGroup(group);
            participant.setTurnOrder(requestedTurnOrder);
            participant.setRole(GroupRole.PAYER);
            participant.setPaymentStatus(PaymentStatus.PENDING);
            participant.setReceiveStatus(ReceiveStatus.PENDING);
            participant.setPaymentBy(PaymentBy.USER_PAYMENT);
        
            // Save participant to database
            participantRepository.save(session, participant);
            logger.info("Saved new participant with turn order {}", requestedTurnOrder);
        
            // Get fresh group data to ensure we have the latest participant count
            Group freshGroup = groupRepository.getById(session, group.getId());
            if (freshGroup != null) {
                logger.info("Fresh group data - members: {}/{}", 
                    freshGroup.getParticipants().size(), 
                    freshGroup.getMaxMembers());
                
                // Check if group is now full and update status
                if (freshGroup.getParticipants().size() == freshGroup.getMaxMembers()) {
                    freshGroup.setStatus(GroupStatus.PENDING_APPROVAL);
                    groupRepository.update(session, freshGroup);
                    logger.info("Group {} is now full and status updated to PENDING_APPROVAL", freshGroup.getGroupName());
                } else {
                    logger.info("Group {} is not full yet, keeping status as {}", 
                        freshGroup.getGroupName(), 
                        freshGroup.getStatus());
                }
            } else {
                logger.error("Could not retrieve fresh group data after adding participant");
            }
            session.getTransaction().commit();
        }
    }

    private Group createNewGroup(PaymentPlan paymentPlan) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Group group = new Group();
            group.setPaymentPlan(paymentPlan);
            group.setMonthlyContribution(paymentPlan.getMonthlyPayment());
            group.setMaxMembers(paymentPlan.getMonthsCount());
            group.setStatus(GroupStatus.WAITING_FOR_MEMBERS);
            group.setPaymentBy(PaymentBy.USER_PAYMENT);
            group.setStartDate(LocalDateTime.now());
            group.setEndDate(LocalDateTime.now().plusMonths(paymentPlan.getMonthsCount()));
            group.setTotalAmount(paymentPlan.getMonthlyPayment().multiply(BigDecimal.valueOf(paymentPlan.getMonthsCount())));
            group.setGroupName(group.generateGroupName(group.getStartDate(), group.getEndDate(), group.getTotalAmount()));
            
            // Save the group first to get its ID
            groupRepository.save(session, group);
            
            // Create rounds for each month
            for (int i = 1; i <= group.getMaxMembers(); i++) {
                Round round = new Round();
                round.setGroup(group);
                round.setRoundNumber(i);
                round.setStatus(RoundStatus.PENDING_APPROVAL);
                round.setStartDate(group.getStartDate().plusMonths(i - 1));
                round.setEndDate(group.getStartDate().plusMonths(i));
                round.setAmount(group.getMonthlyContribution());
                round.setWinnerParticipant(null);
                
                roundRepository.save(session, round);
                logger.info("Created round {} for group {}", i, group.getGroupName());
            }
            
            session.getTransaction().commit();
            return group;
        }
    }

    private List<Group> findMatchingGroup(PaymentPlan paymentPlan) {
        try (Session session = sessionFactory.openSession()) {
            return groupRepository.findAvailableGroupsByPaymentPlan(session, paymentPlan, GroupStatus.WAITING_FOR_MEMBERS);
        }
    }

    private Group findOrCreateGroup(PaymentPlan paymentPlan) {
        List<Group> matches = findMatchingGroup(paymentPlan);
        if (matches.isEmpty()) {
            return createNewGroup(paymentPlan);
        } else {
            return matches.get(0);
        }
    }

    public void matchAndDistribute() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            List<User> users = userRepository.getAll(session);

            for (User user : users) {
                // Skip if user is already in a group
                if (participantRepository.getByUser(session, user) != null) {
                    logger.debug("Skipping user {} - already in a group", user.getUsername());
                    continue;
                }
                
                // Find a matching payment plan for the user based on their preferences
                PaymentPlan userPreferredPlan = paymentPlanRepository.findByPaymentAndMonths(session,
                    user.getMonthlyContribution(),
                    user.getNumberOfMembers()
                ).stream().findFirst().orElse(null);

                if (userPreferredPlan == null) {
                    paymentPlanRepository.save(session, userPreferredPlan);
                    continue;
                }
                PaymentPlan updatedUserPreferredPlan = paymentPlanRepository.getById(session, userPreferredPlan.getId());

                // Find or create a group that matches the user's preferred payment plan
                Group group = findOrCreateGroup(updatedUserPreferredPlan);
                updateGroupStatusIfFull(group);

                if (group == null) {
                    logger.error("Failed to find or create group for user {}", user.getUsername());
                    continue;
                }

                // Get fresh group data to ensure we have the latest participant count
                Group freshGroup = groupRepository.getById(session, group.getId());
                if (freshGroup == null) {
                    logger.error("Group not found after creation. Skipping user {}", user.getUsername());
                    continue;
                }

                logger.info("Processing group: {} for user: {}. Current members: {}/{}", 
                    freshGroup.getGroupName(), 
                    user.getUsername(),
                    freshGroup.getParticipants().size(),
                    freshGroup.getMaxMembers());

                // Add the user to the group if there is space
                if (freshGroup.getParticipants().size() < freshGroup.getMaxMembers()) {
                    try {
                        // Get all taken turn orders
                        List<Integer> takenTurnOrders = freshGroup.getParticipants().stream()
                            .map(Participant::getTurnOrder)
                            .collect(Collectors.toList());
                        
                        // Get all available turn orders
                        List<Integer> availableTurnOrders = new ArrayList<>();
                        for (int i = 1; i <= freshGroup.getMaxMembers(); i++) {
                            if (!takenTurnOrders.contains(i)) {
                                availableTurnOrders.add(i);
                            }
                        }

                        // For matchAndDistribute, we'll assign turn orders sequentially
                        int nextTurnOrder = availableTurnOrders.get(0);
                        addUserToGroupAsParticipant(freshGroup, user, userPreferredPlan, nextTurnOrder);
                        logger.info("Added user {} to group {} with turn order {}", 
                            user.getUsername(), 
                            freshGroup.getGroupName(),
                            nextTurnOrder);
                        
                        // Get fresh group data again after adding participant
                        Group updatedGroup = groupRepository.getById(session, freshGroup.getId());
                        if (updatedGroup != null) {
                            logger.debug("Updated member count: {}/{}", 
                                updatedGroup.getParticipants().size(),
                                updatedGroup.getMaxMembers());
                            // Update group status if needed
                            updateGroupStatusIfFull(updatedGroup);
                        }
                    } catch (Exception e) {
                        logger.error("Error adding user {} to group {}: {}", 
                            user.getUsername(), 
                            freshGroup.getGroupName(),
                            e.getMessage(), 
                            e);
                    }
                } else {
                    logger.warn("No space in matching group {} for user: {}", 
                        freshGroup.getGroupName(), 
                        user.getUsername());
                }
            }
            session.getTransaction().commit();
            logger.info("Match and distribute process completed");
        }
    }

    public List<Group> getOpenGroups() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            List<Group> groups = groupRepository.getOpen(session);
            session.getTransaction().commit();
            return groups;
        }
    }
} 