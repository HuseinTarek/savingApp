package com.swedenrosca;

import com.swedenrosca.controller.*;
import com.swedenrosca.model.*;
import com.swedenrosca.repository.*;
import com.swedenrosca.seed.DemoDataGenerator;
import com.swedenrosca.service.*;
import com.swedenrosca.util.SingletonSessionFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class ApplicationRunner {
    private final Scanner scanner = new Scanner(System.in);
    private final SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();

    // Controllers
    private final MonthlyPaymentController monthlyPaymentController;
    private final PaymentController paymentController;
    private final RoundController roundController;
    private final GroupController groupController;
    private final UserController userController;
    private final ParticipantController participantController;
    private final RoundRepository roundRepository;
    private final PaymentPlanRepository paymentPlanRepository;

    private final DemoDataGenerator demoDataGenerator;
    // Repositories
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ParticipantRepository participantRepository;
    private final PaymentRepository paymentRepository;
    private final MonthlyPaymentRepository monthlyPaymentRepository;
    private final PaymentPlanController paymentPlanController;
    private final PaymentOptionRepository paymentOptionRepository;
    private final MonthOptionRepository monthOptionRepository;
    private final UserService userService;

    public ApplicationRunner() {
        SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();
        
        // Initialize repositories
        this.userRepository = new UserRepository();
        this.groupRepository = new GroupRepository();
        this.participantRepository = new ParticipantRepository();
        this.paymentRepository = new PaymentRepository();
        this.monthlyPaymentRepository = new MonthlyPaymentRepository();
        this.paymentPlanRepository = new PaymentPlanRepository();
        this.roundRepository = new RoundRepository();
        this.paymentOptionRepository = new PaymentOptionRepository();
        this.monthOptionRepository = new MonthOptionRepository();
        
        // Initialize services
        MonthlyPaymentService monthlyPaymentService = new MonthlyPaymentService(monthlyPaymentRepository);
        PaymentService paymentService = new PaymentService(paymentRepository, groupRepository, participantRepository);
        RoundService roundService = new RoundService(roundRepository, groupRepository, participantRepository, userRepository);
        GroupService groupService = new GroupService(sessionFactory, groupRepository, participantRepository, paymentPlanRepository, roundRepository, paymentRepository, userRepository);
        this.userService = new UserService(sessionFactory, userRepository);
        ParticipantService participantService = new ParticipantService(participantRepository);
        PaymentPlanService paymentPlanService = new PaymentPlanService(sessionFactory, paymentPlanRepository);
        
        // Initialize controllers
        this.monthlyPaymentController = new MonthlyPaymentController(monthlyPaymentService);
        this.paymentController = new PaymentController(paymentService);
        this.roundController = new RoundController(participantService, roundService);
        this.groupController = new GroupController(groupService, userService, participantService, paymentPlanService, roundService, paymentService);
        this.userController = new UserController(userService);
        this.participantController = new ParticipantController(participantService);
        this.paymentPlanController = new PaymentPlanController(paymentPlanService);

        // Initialize DemoDataGenerator
        MonthOptionService monthOptionService = new MonthOptionService(sessionFactory, monthOptionRepository);
        PaymentOptionService paymentOptionService = new PaymentOptionService(sessionFactory, paymentOptionRepository);
        this.demoDataGenerator = new DemoDataGenerator(
            userService,
            groupService,
            participantService,
            paymentService,
            paymentPlanService,
            roundService,
            monthOptionService,
            paymentOptionService
        );

        // Generate demo data
        this.demoDataGenerator.generateAllDemoData();
    }

    public void run() {
        try (Session session = sessionFactory.openSession()) {
            // 2) Main loop: role selection → auth → role‐menu → back to role selection on logout
            while (true) {
                Role role = promptRoleSelection();
                if (role == null) break;               // Exit application

                User currentUser = handleAuthFlow(role);
                if (currentUser == null) continue;     // Back to role selection

                // 3) Run the menu for this role until logout
                boolean stayInRole = true;
                while (stayInRole) {
                    switch (role) {
                        case ADMIN ->
                                stayInRole = showAdminMenu(session);
                        case USER ->
                                stayInRole = showUserMenu(session, currentUser);
                        case CUSTOMER_SERVICE ->
                                stayInRole = showCustomerServiceMenu(session);
                    }
                }
                // when stayInRole==false the user logged out → go back to role selection
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println(" Goodbye!");
    }

    private Role promptRoleSelection() {
        while (true) {
            System.out.println("\n=== Welcome to Savings System ===");
            System.out.println("0. Exit");
            System.out.println("1. ADMIN");
            System.out.println("2. USER");
            System.out.println("3. CUSTOMER_SERVICE");
            System.out.print("Enter your role number: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                return switch (choice) {
                    case 0 -> null;
                    case 1 -> Role.ADMIN;
                    case 2 -> Role.USER;
                    case 3 -> Role.CUSTOMER_SERVICE;
                    default -> {
                        System.out.println("❌ Invalid choice. Try again.");
                        yield null;
                    }
                };
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("❌ Invalid input. Try again.");
            }
        }
    }

    public User handleAuthFlow(Role expectedRole) {
        while (true) {
            System.out.println("\n1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> {
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    User user = userRepository.getByUsername(null, username);
                    if (user != null && user.getPassword().equals(password)) {
                        if (user.getRole() == expectedRole) {
                            System.out.println("✅ Login successful.");
                            return user;
                        } else {
                            System.out.println("❌ Role mismatch.");
                        }
                    } else {
                        System.out.println("❌ Invalid credentials.");
                    }
                }
                case 2 -> {
                    System.out.print("Username: "); String username = scanner.nextLine();
                    if (userRepository.getByUsername(null, username) != null) {
                        System.out.println("❌ Username already exists."); break;
                    }

                    System.out.print("Password: "); String password = scanner.nextLine();
                    System.out.print("Email: "); String email = scanner.nextLine();
                    System.out.print("First name: "); String firstName = scanner.nextLine();
                    System.out.print("Last name: "); String lastName = scanner.nextLine();

                    System.out.print("Mobile number: "); String mobile = scanner.nextLine();
                    if (userRepository.existsByMobileNumber(null, mobile)) {
                        System.out.println("❌ Mobile number already exists."); break;
                    }

                    System.out.print("Personal number: "); String personalNumber = scanner.nextLine();

                    User newUser = new User(username, password, email, personalNumber, firstName, lastName,
                            mobile, expectedRole);

                    userRepository.save(null, newUser);
                    System.out.println("✅ Registered successfully. You can now log in.");
                }
                case 3 -> {
                    return null; // Exit
                }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    private void runRoleMenu(Session session, User user) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== " + user.getRole() + " Menu ===");
            switch (user.getRole()) {
                case ADMIN -> running = showAdminMenu(session);
                case CUSTOMER_SERVICE -> running = showCustomerServiceMenu(session);
                case USER -> running = showUserMenu(session, user);
            }
        }
    }

    private boolean showAdminMenu(Session session) {
        while (true) {
            System.out.println("=== Group Management ===");
            System.out.println("1. Show Active Groups");
            System.out.println("2. Activate Pending Approval Groups");
            System.out.println("3. View All Groups");
            System.out.println("4. Manage Group Status");
            
            System.out.println("\n=== Payment Management ===");
            System.out.println("5. View All Payments");
            System.out.println("6. View Monthly Payments for specific month");
            System.out.println("7. View Late Payments");

            System.out.println("\n=== Round Management ===");
            System.out.println("8. Manage Round Status");

            System.out.println("\n=== User Management ===");
            System.out.println("9. View All Users");
            System.out.println("10. Decide monthly payment and months count");

            System.out.println("\n0. Logout");
            System.out.print("\nSelect an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 0 -> { return false; }
                case 1 -> showActiveGroups();
                case 2 -> activatePendingApprovalGroups();
                case 3 -> viewAllGroups();
                case 4 -> manageGroupStatus();
                case 5 -> viewAllPayments();
                case 6 -> viewMonthlyPaymentsForSpecificMonth();
                case 7 -> viewLatePayments();
                case 8 -> manageRoundStatus();
                case 9 -> viewAllUsers(session);
                case 10 -> decideMonthlyPaymentAndMonthsCount();
                default -> System.out.println("❌ Invalid option. Please try again.");
            }
        }
    }

    private void decideMonthlyPaymentAndMonthsCount() {
       AdminMonthPaymentController adminMonthPaymentController = new AdminMonthPaymentController();
       adminMonthPaymentController.adminMonthPaymentMenu(scanner);
    }

    private void activatePendingApprovalGroups() {
        GroupRepository groupRepository = new GroupRepository();
        List<Group> pendingGroups = groupRepository.getPendingApprovalGroups(null);
        
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
                groupRepository.update(null, group);
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

    private void showActiveGroups() {
        GroupRepository groupRepository = new GroupRepository();
        List<Group> activeGroups = groupRepository.getActiveGroups(null);

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
        GroupRepository groupRepository = new GroupRepository();
        List<Group> allGroups = groupRepository.getAll(null);

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

 
    private void manageGroupStatus() {
        System.out.println("\n=== Manage Group Status ===");
        try {
            System.out.print("Enter group ID: ");
            Long groupId = scanner.nextLong();
            scanner.nextLine();
            
            Group group = groupRepository.getById(null, groupId);
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
            groupRepository.update(null, group);
            System.out.println("✅ Group status updated successfully!");
        } catch (Exception e) {
            System.out.println("❌ Error updating group status: " + e.getMessage());
        }
    }

    private void manageRoundStatus() {
        System.out.println("Enter round ID: ");
        Long roundId = scanner.nextLong();
        scanner.nextLine();

        Round round = roundRepository.getById(null, roundId);
        if (round == null) {
            System.out.println("❌ Round not found!");
            return;
        }

        System.out.println("Current status: " + round.getStatus());
        System.out.println("Available statuses:");
        System.out.println("1. NOT_STARTED");
        System.out.println("2. ACTIVE");
        System.out.println("3. COMPLETED");
        System.out.println("4. BLOCKED");

        System.out.print("Select new status (1-4): ");
        int statusChoice = scanner.nextInt();
        scanner.nextLine();

        RoundStatus newStatus = switch (statusChoice) {
            case 1 -> RoundStatus.PENDING_APPROVAL;
            case 2 -> RoundStatus.ACTIVE;
            case 3 -> RoundStatus.COMPLETED;
            case 4 -> RoundStatus.BLOCKED;
            default -> throw new IllegalArgumentException("Invalid status choice");
        };

        round.setStatus(newStatus);
        roundRepository.update(null, round);
        System.out.println("✅ Round status updated successfully!");
    }

    // Other menu methods remain unchanged
    private boolean showCustomerServiceMenu(Session session) {
        System.out.println("1. Help user");
        System.out.println("2. View reports");
        System.out.println("0. Exit");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice != 0;
    }

    private boolean showUserMenu(Session session, User user) {
        while (true) {
            System.out.println("\n=== USER MENU ===");
            System.out.println("1. View my info");
            System.out.println("2. View my groups");
            System.out.println("3. Join a group");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter 0-3.");
                continue;
            }

            switch (choice) {
                case 0 -> { return false; }  // Logout
                case 1 -> viewMyInfo(user);
                case 2 -> viewMyGroups(user);
                case 3 -> joinGroup(user);
                default -> System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private void viewMyInfo(User user) {
        System.out.println("\n=== My Information ===");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Name: " + user.getFirstName() + " " + user.getLastName());
        System.out.println("Mobile: " + user.getMobileNumber());
        System.out.println("Role: " + user.getRole());
        if (user.getMonthlyContribution() != null) {
            System.out.println("Monthly Contribution: " + user.getMonthlyContribution() + " SEK");
        }
    }

    private void viewMyGroups(User user) {
        List<Group> userGroups = groupRepository.getByMember(null, user.getId());
        if (userGroups.isEmpty()) {
            System.out.println("\nYou are not a member of any groups.");
            return;
        }

        System.out.println("\n=== My Groups ===");
        System.out.printf("%-5s | %-25s | %-10s | %-12s | %-12s%n",
                "ID", "Group Name", "Status", "Start Date", "End Date");
        System.out.println("------------------------------------------------------------");

        for (Group group : userGroups) {
            System.out.printf("%-5d | %-25s | %-10s | %-12s | %-12s%n",
                    group.getId(),
                    group.getGroupName(),
                    group.getStatus(),
                    group.getStartDate().toLocalDate(),
                    group.getEndDate().toLocalDate());
        }
    }

    private void joinGroup(User currentUser) {
        System.out.println("\n=== Join a Group ===");
        
        // Get all available month options
        List<MonthOption> monthOptions;
        try (Session session = sessionFactory.openSession()) {
            monthOptions = monthOptionRepository.getAll(session);
        }
        if (monthOptions.isEmpty()) {
            System.out.println("No month options available. Please contact an administrator.");
            return;
        }

        // Display month options
        System.out.println("\nAvailable Month Options:");
        for (int i = 0; i < monthOptions.size(); i++) {
            MonthOption option = monthOptions.get(i);
            System.out.printf("%d) %d months%n", i + 1, option.getMonthsCount());
        }

        // Get user's month choice
        int monthChoice;
        do {
            System.out.print("Select number of months (1-" + monthOptions.size() + "): ");
            try {
                monthChoice = Integer.parseInt(scanner.nextLine().trim());
                if (monthChoice < 1 || monthChoice > monthOptions.size()) {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                monthChoice = 0;
            }
        } while (monthChoice < 1 || monthChoice > monthOptions.size());

        MonthOption selectedMonthOption = monthOptions.get(monthChoice - 1);

        // Get all available payment options
        List<PaymentOption> paymentOptions;
        try (Session session = sessionFactory.openSession()) {
            paymentOptions = paymentOptionRepository.getAllMonthlyPayments(session);
        }
        if (paymentOptions.isEmpty()) {
            System.out.println("No payment options available. Please contact an administrator.");
            return;
        }

        // Display payment options
        System.out.println("\nAvailable Payment Options:");
        for (int i = 0; i < paymentOptions.size(); i++) {
            PaymentOption option = paymentOptions.get(i);
            System.out.printf("%d) %d SEK%n", i + 1, option.getMonthlyPayment());
        }

        // Get user's payment choice
        int paymentChoice;
        do {
            System.out.print("Select monthly payment (1-" + paymentOptions.size() + "): ");
            try {
                paymentChoice = Integer.parseInt(scanner.nextLine().trim());
                if (paymentChoice < 1 || paymentChoice > paymentOptions.size()) {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                paymentChoice = 0;
            }
        } while (paymentChoice < 1 || paymentChoice > paymentOptions.size());

        PaymentOption selectedPaymentOption = paymentOptions.get(paymentChoice - 1);

        // Create payment plan
        PaymentPlan paymentPlan = new PaymentPlan();
        paymentPlan.setMonthsCount(selectedMonthOption.getMonthsCount());
        paymentPlan.setMonthlyPayment(java.math.BigDecimal.valueOf(selectedPaymentOption.getMonthlyPayment()));
        paymentPlanRepository.save(null, paymentPlan);

        // Find or create group
        List<Group> matchingGroups = groupRepository.findAvailableGroupsByPaymentPlan(null, paymentPlan, GroupStatus.WAITING_FOR_MEMBERS);
        Group selectedGroup;
        
        if (matchingGroups.isEmpty()) {
            // Create new group
            selectedGroup = new Group();
            selectedGroup.setPaymentPlan(paymentPlan);
            selectedGroup.setMonthlyContribution(paymentPlan.getMonthlyPayment());
            selectedGroup.setMaxMembers(paymentPlan.getMonthsCount());
            selectedGroup.setStatus(GroupStatus.WAITING_FOR_MEMBERS);
            selectedGroup.setPaymentBy(PaymentBy.USER_PAYMENT);
            selectedGroup.setStartDate(java.time.LocalDateTime.now());
            selectedGroup.setEndDate(java.time.LocalDateTime.now().plusMonths(paymentPlan.getMonthsCount()));
            selectedGroup.setTotalAmount(paymentPlan.getMonthlyPayment().multiply(java.math.BigDecimal.valueOf(paymentPlan.getMonthsCount())));
            selectedGroup.setGroupName(selectedGroup.generateGroupName(selectedGroup.getStartDate(), selectedGroup.getEndDate(), selectedGroup.getTotalAmount()));
            groupRepository.save(null, selectedGroup);
        } else {
            selectedGroup = matchingGroups.get(0);
        }

        // Add user to group
        Participant participant = new Participant();
        participant.setUser(currentUser);
        participant.setGroup(selectedGroup);
        participant.setPaymentBy(PaymentBy.USER_PAYMENT);
        participant.setRole(GroupRole.PAYER);
        participant.setTurnOrder(selectedGroup.getParticipants().size() + 1);
        participantRepository.save(null, participant);
        System.out.println("Successfully joined group: " + selectedGroup.getGroupName());
    }

    private void viewAllPayments() {
        List<Payment> payments = paymentRepository.getAll(null);
        System.out.printf("%-5s %-10s %-10s %-20s\n",
                "ID", "Amount", "Status", "PaidAt");

        for (Payment p : payments) {
            System.out.printf("%-5d %-10s %-10s %-20s\n",
                    p.getId(),
                    p.getAmount(),
                    p.getPaymentStatus(),
                    p.getPaidAt() != null ? p.getPaidAt().toString() : "Not Paid");
        }
    }

    private void viewMonthlyPaymentsForSpecificMonth() {
        System.out.println("please enter the month :");
        int month = scanner.nextInt();
        scanner.nextLine();
        System.out.println("please enter the year :");
        int year = scanner.nextInt();
        scanner.nextLine();

        LocalDateTime firstOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime startDate = firstOfMonth;
        LocalDateTime endDate = firstOfMonth.plusMonths(1);
        List<Payment> payments = paymentRepository.getPaymentsByDateRange(null, startDate, endDate);

        System.out.printf("%-5s %-10s %-10s %-20s\n",
                "ID", "Amount", "Status", "PaidAt");

        for (Payment p : payments) {
            System.out.printf("%-5d %-10s %-10s %-20s\n",
                    p.getId(),
                    p.getAmount(),
                    p.getPaymentStatus(),
                    p.getPaidAt() != null ? p.getPaidAt().toString() : "Not Paid");
        }
    }

    private void viewLatePayments() {
        List<Payment> latePayments = paymentRepository.getLatePayments(null);
        System.out.printf("%-5s %-10s %-10s %-20s\n",
                "ID", "Amount", "Status", "PaidAt");

        for (Payment p : latePayments) {
            System.out.printf("%-5d %-10s %-10s %-20s\n",
                    p.getId(),
                    p.getAmount(),
                    p.getPaymentStatus(),
                    p.getPaidAt() != null ? p.getPaidAt().toString() : "Not Paid");
        }
    }

//    private void paymentStatistics() {
//        System.out.println("📊 Payment Statistics:");
//        System.out.println("Total Paid: " + totalPayments + " SEK");
//        System.out.println("Total Unpaid: " + totalUnpaid + " SEK");
//        System.out.println("Paid: " + paidCount + ", Unpaid: " + unpaidCount);
//        System.out.println("Average Payment: " + averagePayment + " SEK");
//    }

    private void viewActiveRounds() {
        RoundRepository roundRepository = new RoundRepository();
        List<Round> activeRounds = roundRepository.getActiveRounds(null);

        if (activeRounds.isEmpty()) {
            System.out.println("❌ No active rounds found.");
            return;
        }

        System.out.println("📋 Active Rounds:");
        System.out.printf("%-10s | %-20s | %-10s | %-12s | %-12s%n",
                "Round ID", "Group Name", "Status", "Start Date", "End Date");
        System.out.println("---------------------------------------------------------------------");

        for (Round round : activeRounds) {
            System.out.printf("%-10d | %-20s | %-10s | %-12s | %-12s%n",
                    round.getId(),
                    round.getGroup().getGroupName(),
                    round.getStatus(),
                    round.getStartDate().toLocalDate(),
                    round.getEndDate().toLocalDate());
        }
    }

    private void viewAllUsers(Session session) {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\n=== All Users ===");
        for (User user : users) {
            System.out.printf("Username: %s, Role: %s, Email: %s%n",
                    user.getUsername(), user.getRole(), user.getEmail());
        }
    }


    private void manageUserRoles() {
        System.out.println("Manage User Roles - Not implemented yet");
    }

    private void userStatistics() {
        System.out.println("User Statistics - Not implemented yet");
    }

    private void systemStatistics() {
        System.out.println("System Statistics - Not implemented yet");
    }

    private void activityLog() {
        System.out.println("Activity Log - Not implemented yet");
    }

    private boolean handleNumericInput(String prompt, Consumer<Integer> action) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            // Check for exit condition
            if (input.equalsIgnoreCase("0")) {
                return false;
            }

            try {
                int choice = Integer.parseInt(input);
                action.accept(choice);
                return true;
            } catch (NumberFormatException e) {
                // Log error and show user-friendly message
                System.out.println("❌ Please enter a valid number");
                // Continue the loop
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
                // Continue the loop
            }
        }
    }

    public static void main(String[] args) {
        // Create and run the application
        ApplicationRunner app = new ApplicationRunner();
        app.run();
    }
}