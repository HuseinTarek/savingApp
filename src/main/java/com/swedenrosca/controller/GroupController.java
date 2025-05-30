package com.swedenrosca.controller;

import com.swedenrosca.model.*;
import com.swedenrosca.service.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;
import java.util.Scanner;

public class GroupController {
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    private final GroupService groupService;
    private final UserService userService;
    private final ParticipantService participantService;
    private final PaymentPlanService paymentPlanService;
    private final RoundService roundService;
    private final PaymentService paymentService;

    public GroupController(GroupService groupService, UserService userService, ParticipantService participantService, PaymentPlanService paymentPlanService, RoundService roundService, PaymentService paymentService) {
        this.groupService = groupService;
        this.userService = userService;
        this.participantService = participantService;
        this.paymentPlanService = paymentPlanService;
        this.roundService = roundService;
        this.paymentService = paymentService;
    }

    public String createGroup(Long creatorId, BigDecimal monthlyContribution) {
        User creator = userService.getUserById(creatorId);
        return groupService.createGroup(creator, monthlyContribution);
    }

    public List<Group> getAllActiveGroups() {
        return groupService.getAllActiveGroups();
    }

    public Group getGroupById(Long id) {
        return groupService.getGroupById(id);
    }

    public void updateGroupStatusIfFull(Group group) {
        groupService.updateGroupStatusIfFull(group);
    }

    public void activateGroup(Group group) {
        groupService.activateGroup(group);
    }

    public String removeMember(Long groupId, Long userId) {
        return groupService.removeMember(groupId, userId);
    }

    public String deactivateGroup(Long groupId) {
        return groupService.deactivateGroup(groupId);
    }

    public void addMemberToGroup(Long groupId, Long userId, Long paymentPlanId, Integer requestedTurnOrder) {
        groupService.addMemberToGroup(groupId, userId, paymentPlanId, requestedTurnOrder);
    }

    public void initializeGroupPayments(Group group) {
        try {
            groupService.initializeGroupPayments(group);
            System.out.println("Group payments initialized successfully!");
        } catch (Exception e) {
            logger.error("Failed to initialize group payments: {}", e.getMessage());
            System.out.println("Failed to initialize group payments: " + e.getMessage());
        }
    }

    public void addUserToGroup(Group group, User user, PaymentPlan paymentPlan, int turnOrder) {
        try {
            groupService.addUserToGroupAsParticipant(group, user, paymentPlan, turnOrder);
            System.out.println("User added to group successfully!");
        } catch (Exception e) {
            logger.error("Failed to add user to group: {}", e.getMessage());
            System.out.println("Failed to add user to group: " + e.getMessage());
        }
    }

    public void matchAndDistribute() {
        try {
            groupService.matchAndDistribute();
            System.out.println("Match and distribute process completed successfully!");
        } catch (Exception e) {
            logger.error("Failed to match and distribute: {}", e.getMessage());
            System.out.println("Failed to match and distribute: " + e.getMessage());
        }
    }

    private void showActiveGroups() {
        List<Group> activeGroups = groupService.getAllActiveGroups();

        if (activeGroups.isEmpty()) {
            System.out.println("❌ No active groups found.");
            return;
        }

        System.out.println("\n✅ Active Groups:");
        System.out.printf("%-5s | %-25s | %-10s | %-12s | %-12s | %-10s%n",
                "ID", "Group Name", "Status", "Start Date", "End Date", "Members");
        System.out.println("----------------------------------------------------------------------------------");

        for (Group group : activeGroups) {
            System.out.printf("%-5d | %-25s | %-10s | %-12s | %-12s | %-10d%n",
                    group.getId(),
                    group.getGroupName(),
                    group.getStatus(),
                    group.getStartDate().toLocalDate(),
                    group.getEndDate().toLocalDate(),
                    group.getMaxMembers());
        }
    }

    private void viewAllGroups() {
        List<Group> allGroups = groupService.getAllGroups();

        if (allGroups.isEmpty()) {
            System.out.println("❌ No groups found.");
            return;
        }

        System.out.println("\n📋 All Groups:");
        System.out.printf("%-5s | %-25s | %-12s | %-12s | %-12s | %-10s%n",
                "ID", "Group Name", "Status", "Start Date", "End Date", "Members");
        System.out.println("-----------------------------------------------------------------------------------");

        for (Group group : allGroups) {
            System.out.printf("%-5d | %-25s | %-12s | %-12s | %-12s | %-10d%n",
                    group.getId(),
                    group.getGroupName(),
                    group.getStatus(),
                    group.getStartDate().toLocalDate(),
                    group.getEndDate().toLocalDate(),
                    group.getMaxMembers());
        }
    }

    private void activatePendingApprovalGroups() {
        List<Group> pendingGroups = groupService.getAllPendingApprovalGroups();
        if (pendingGroups.isEmpty()) {
            System.out.println("❌ No pending approval groups found.");
            return;
        }

        System.out.println("\n=== Pending Groups ===");
        System.out.printf("%-6s | %-25s | %-10s | %-10s%n", "ID", "Group Name", "Status", "Members");
        System.out.println("------------------------------------------------------------");

        boolean anyActivated = false;
        for (Group group : pendingGroups) {
            String name = group.getGroupName();
            if (name.length() > 25) {
                name = name.substring(0, 22) + "...";
            }

            // Check if group is complete
            if (group.getParticipants().size() == group.getMaxMembers()) {
                group.setStatus(GroupStatus.ACTIVE);
                groupService.updateGroup(group);
                anyActivated = true;
                System.out.printf("%-6d | %-25s | %-10s | %d/%d%n", 
                    group.getId(), 
                    name, 
                    group.getStatus().name(),
                    group.getParticipants().size(),
                    group.getMaxMembers());
            } else {
                System.out.printf("%-6d | %-25s | %-10s | %d/%d (Incomplete)%n", 
                    group.getId(), 
                    name, 
                    group.getStatus().name(),
                    group.getParticipants().size(),
                    group.getMaxMembers());
            }
        }

        if (!anyActivated) {
            System.out.println("\n❌ No groups were activated. All groups are incomplete.");
        } else {
            System.out.println("\n✅ Successfully activated complete groups.");
        }
    }

    private void manageGroupStatus() {
        System.out.println("\n=== Manage Group Status ===");
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter group ID: ");
            Long groupId = scanner.nextLong();
            scanner.nextLine();
            Group group = groupService.getGroupById(groupId);
            if (group == null) {
                System.out.println("❌ Group not found!");
                return;
            }

            System.out.println("Current status: " + group.getStatus());
            System.out.println("Available statuses:");
            System.out.println("1. PENDING APPROVAL");
            System.out.println("2. ACTIVE");
            System.out.println("3. COMPLETED");
            System.out.println("4. BLOCKED");
            System.out.print("Select new status (1-4): ");
            int statusChoice = scanner.nextInt();
            scanner.nextLine();
            GroupStatus newStatus = switch (statusChoice) {
                case 1 -> GroupStatus.PENDING_APPROVAL;
                case 2 -> GroupStatus.ACTIVE;
                case 3 -> GroupStatus.COMPLETED;
                case 4 -> GroupStatus.BLOCKED;
                default -> throw new IllegalArgumentException("Invalid status choice");
            };
            group.setStatus(newStatus);
            groupService.updateGroup(group);
            System.out.println("✅ Group status updated successfully!");
        } catch (Exception e) {
            System.out.println("❌ Error updating group status: " + e.getMessage());
        }
    }
}