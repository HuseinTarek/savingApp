package com.swedenrosca.controller;

import com.swedenrosca.model.*;
import com.swedenrosca.repository.*;
import org.hibernate.SessionFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;

public class GroupController {
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    private final SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();


    private final ParticipantRepository participantRepository ;
    private final GroupRepository groupRepository ;
    private final UserRepository userRepository;
    private final RoundRepository roundRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentPlanRepository paymentPlanRepository;

    public GroupController(ParticipantRepository participantRepository, GroupRepository groupRepository, UserRepository userRepository, RoundRepository roundRepository, PaymentRepository paymentRepository, PaymentPlanRepository paymentPlanRepository) {
        this.participantRepository = participantRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.roundRepository = roundRepository;
        this.paymentRepository = paymentRepository;
        this.paymentPlanRepository = paymentPlanRepository;
    }

    public String createGroup(String groupName, BigDecimal totalAmount, int maxMembers,
                              LocalDateTime startDate, LocalDateTime endDate, GroupStatus status,
                              BigDecimal monthlyContribution, User creator) {
        Group group = new Group();
        group.setGroupName(groupName);
        group.setTotalAmount(totalAmount);
        group.setMaxMembers(maxMembers);
        group.setStartDate(startDate);
        group.setEndDate(endDate);
        group.setStatus(GroupStatus.WAITING_FOR_MEMBERS);
        group.setMonthlyContribution(monthlyContribution);
        group.setCreator(creator);
        group.setPaymentBy(PaymentBy.USER_PAYMENT);
        groupRepository.save(group);
        return "Group created successfully";
    }

    public List<Group> getAllActiveGroups() {
        return groupRepository.getActiveGroups();
    }

    public Group getGroupById(Long id) {
        return groupRepository.getById(id);
    }


    private void updateGroupStatusIfFull(Group group) {
       // Get fresh group data with participants
       Group freshGroup = groupRepository.getById(group.getId());
       if (freshGroup != null && 
           freshGroup.getParticipants().size() == freshGroup.getMaxMembers() &&
           freshGroup.getStatus() == GroupStatus.WAITING_FOR_MEMBERS) {
            freshGroup.setStatus(GroupStatus.PENDING_APPROVAL);
            groupRepository.update(freshGroup);
        
       }
    }

    public void initializeGroupPayments(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Group must not be null");
        }
        // Refresh group from DB to get latest participants and rounds
        Group freshGroup = groupRepository.getById(group.getId());
        if (freshGroup == null) {
            throw new IllegalStateException("Group not found");
        }
        if (freshGroup.getStatus() != GroupStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Group must be in PENDING_APPROVAL to activate");
        }
        freshGroup.setStatus(GroupStatus.ACTIVE);
        groupRepository.update(freshGroup);
        logger.info("Group {} status set to ACTIVE", freshGroup.getGroupName());

        // Get all participants and rounds
        List<Participant> participants = freshGroup.getParticipants();
        List<Round> rounds = roundRepository.getByGroup(freshGroup);

        for (Participant participant : participants) {
            for (Round round : rounds) {
                Payment payment = new Payment();
                payment.setGroup(freshGroup);
                payment.setRound(round);
                payment.setParticipant(participant);
                payment.setPayer(participant);
                payment.setAmount(freshGroup.getMonthlyContribution());
                payment.setPaymentStatus(PaymentStatus.PENDING);
                payment.setDueDate(round.getStartDate().plusDays(5));
                payment.setCreatedAt(LocalDateTime.now());
                payment.setPaymentBy(PaymentBy.USER_PAYMENT);

                // Set winner for the round if this is the participant's turn
                if (round.getRoundNumber() == participant.getTurnOrder()) {
                    round.setWinnerParticipant(participant);
                    roundRepository.update(round);
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
                    paymentRepository.save(payment);
                    logger.info("Saved payment: groupId={}, roundId={}, participantId={}", 
                        freshGroup.getId(), round.getId(), participant.getId());
                } catch (Exception e) {
                    logger.error("Failed to save payment: groupId={}, roundId={}, participantId={}. Exception: {}",
                        freshGroup.getId(), round.getId(), participant.getId(), e.getMessage(), e);
                }
            }
        }
        logger.info("Group {} activated: all payments and winners set.", freshGroup.getGroupName());
    }
     
    public String removeMember(Long groupId, Long userId) {
        try {
            groupRepository.deleteParticipantFromGroup(groupRepository.getById(groupId), userRepository.getById(userId));
            return "Member removed successfully!";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String deactivateGroup(Long groupId) {
        Group group = groupRepository.getById(groupId);
        if (group == null) {
            return "Group not found";
        }
        group.setStatus(GroupStatus.BLOCKED);
        groupRepository.update(group);
        return "Group deactivated successfully!";
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
        participant = participantRepository.save(participant);
        logger.info("Saved new participant with turn order {}", requestedTurnOrder);
    
        // Get fresh group data to ensure we have the latest participant count
        Group freshGroup = groupRepository.getById(group.getId());
        if (freshGroup != null) {
            logger.info("Fresh group data - members: {}/{}", 
                freshGroup.getParticipants().size(), 
                freshGroup.getMaxMembers());
            
            // Check if group is now full and update status
            if (freshGroup.getParticipants().size() == freshGroup.getMaxMembers()) {
                freshGroup.setStatus(GroupStatus.PENDING_APPROVAL);
                groupRepository.update(freshGroup);
                logger.info("Group {} is now full and status updated to PENDING_APPROVAL", freshGroup.getGroupName());
            } else {
                logger.info("Group {} is not full yet, keeping status as {}", 
                    freshGroup.getGroupName(), 
                    freshGroup.getStatus());
            }
        } else {
            logger.error("Could not retrieve fresh group data after adding participant");
        }
    }
    
    private Group createNewGroup(PaymentPlan paymentPlan) {
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
        group = groupRepository.save(group);
        
        // Create rounds for each month
        for (int i = 1; i <= group.getMaxMembers(); i++) {
            Round round = new Round();
            round.setGroup(group);
            round.setRoundNumber(i);
            round.setStatus(RoundStatus.PENDING_APPROVAL);
            round.setStartDate(group.getStartDate().plusMonths(i - 1));
            round.setEndDate(group.getStartDate().plusMonths(i));
            round.setAmount(group.getMonthlyContribution());
            // The winner will be set when a participant joins with this turn order
            round.setWinnerParticipant(null);
            
            roundRepository.save(round);
            logger.info("Created round {} for group {}", i, group.getGroupName());
        }
        
        return group;
    }

    //**
    // 🔍 Finds an open groups with matching monthly amount and duration
    private List<Group> findMatchingGroup(PaymentPlan paymentPlan) {
        List<Group> waitingGroups = groupRepository.findAvailableGroupsByPaymentPlan(
            paymentPlan,
            GroupStatus.WAITING_FOR_MEMBERS
        );
        
        return waitingGroups;
    }

    // 🔍 Finds an open groups with matching monthly amount and duration or creates a new one if nothing matches
    private Group findOrCreateGroup(PaymentPlan paymentPlan) {
        List<Group> matches = findMatchingGroup(paymentPlan);
        if (matches.isEmpty()) {
            Group newGroup= createNewGroup(paymentPlan);
            return groupRepository.save(newGroup);
        } else {
            return matches.get(0);
        }
    }

    // 🔁 Main method that goes through each user and matches or creates group, then adds them
    public void matchAndDistribute() {
        List<User> users = userRepository.getAll();

        for (User user : users) {
            // Skip if user is already in a group
            if (participantRepository.getByUser(user) != null) {
                logger.debug("Skipping user {} - already in a group", user.getUsername());
                continue;
            }
            
            // Find a matching payment plan for the user based on their preferences
            PaymentPlan userPreferredPlan = paymentPlanRepository.findByPaymentAndMonths(
                user.getMonthlyContribution(),
                user.getNumberOfMembers()
            ).stream().findFirst().orElse(null);

            if (userPreferredPlan == null) {
                paymentPlanRepository.save(userPreferredPlan);
                continue;
            }
            PaymentPlan updatedUserPreferredPlan = paymentPlanRepository.findById(userPreferredPlan.getId());

            // Find or create a group that matches the user's preferred payment plan
            Group group = findOrCreateGroup(updatedUserPreferredPlan);
            updateGroupStatusIfFull(group);

            if (group == null) {
                logger.error("Failed to find or create group for user {}", user.getUsername());
                continue;
            }

            // Get fresh group data to ensure we have the latest participant count
            Group freshGroup = groupRepository.getById(group.getId());
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
                    // This is different from manual join where user can choose
                    int nextTurnOrder = availableTurnOrders.get(0);
                    addUserToGroupAsParticipant(freshGroup, user, userPreferredPlan, nextTurnOrder);
                    logger.info("Added user {} to group {} with turn order {}", 
                        user.getUsername(), 
                        freshGroup.getGroupName(),
                        nextTurnOrder);
                    
                    // Get fresh group data again after adding participant
                    Group updatedGroup = groupRepository.getById(freshGroup.getId());
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
        logger.info("Match and distribute process completed");
    }
    

    public String pendingApprovGroup(Long groupId){
        Group group = groupRepository.getById(groupId);
        if (group == null) {
            return "Group not found";
        }
        group.setStatus(GroupStatus.PENDING_APPROVAL);
        groupRepository.update(group);
        return "Group pending approval successfully!";
    }

    // Modified this method to ONLY check if the group is full, removing the status change side effect.
    public Boolean isComplete(Long groupId) {
        Group group = groupRepository.getById(groupId);
        if (group == null) {
            return false; // Group not found, not complete
        }
        // Check if the number of participants equals the maximum members
        return group.getParticipants().size() == group.getMaxMembers();
    }

    public boolean isGroupFull(Group group) {
        return group.getParticipants().size() == group.getMaxMembers();
    }

    public List<Integer> getAvailableTurnOrders(Group group) {
        List<Integer> takenTurnOrders = group.getParticipants().stream()
            .map(Participant::getTurnOrder)
            .collect(Collectors.toList());
        
        List<Integer> availableTurnOrders = new ArrayList<>();
        for (int i = 1; i <= group.getMaxMembers(); i++) {
            if (!takenTurnOrders.contains(i)) {
                availableTurnOrders.add(i);
            }
        }
        return availableTurnOrders;
    }

}