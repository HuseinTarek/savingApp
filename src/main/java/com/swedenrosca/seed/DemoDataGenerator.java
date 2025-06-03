package com.swedenrosca.seed;

import com.swedenrosca.model.*;
import com.swedenrosca.repository.*;
import com.swedenrosca.service.*;
import com.swedenrosca.util.SingletonSessionFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.stream.Collectors;

public class DemoDataGenerator {
    private final UserService userService;
    private final GroupService groupService;
    private final ParticipantService participantService;
    private final PaymentService paymentService;
    private final PaymentPlanService paymentPlanService;
    private final RoundService roundService;
    private final MonthOptionService monthOptionService;
    private final PaymentOptionService paymentOptionService;
    private final SessionFactory sessionFactory;

    public DemoDataGenerator(
        UserService userService,
        GroupService groupService,
        ParticipantService participantService,
        PaymentService paymentService,
        PaymentPlanService paymentPlanService,
        RoundService roundService,
        MonthOptionService monthOptionService,
        PaymentOptionService paymentOptionService
    ) {
        this.userService = userService;
        this.groupService = groupService;
        this.participantService = participantService;
        this.paymentService = paymentService;
        this.paymentPlanService = paymentPlanService;
        this.roundService = roundService;
        this.monthOptionService = monthOptionService;
        this.paymentOptionService = paymentOptionService;
        this.sessionFactory = SingletonSessionFactory.getSessionFactory();
    }

    private boolean isDatabaseEmpty() {
        System.out.println("\n=== Checking if database is empty ===");
        
        // Check if any data exists in any of the repositories
        List<User> users = userService.getAllUsers();
        System.out.println("Found " + users.size() + " users in database");
        if (!users.isEmpty()) {
            System.out.println("Users found: " + users.stream().map(User::getUsername).collect(Collectors.joining(", ")));
            return false;
        }

        List<Group> groups = groupService.getAllGroups();
        System.out.println("Found " + groups.size() + " groups in database");
        if (!groups.isEmpty()) {
            return false;
        }

        List<Payment> payments = paymentService.getAllPayments();
        System.out.println("Found " + payments.size() + " payments in database");
        if (!payments.isEmpty()) {
            return false;
        }

        List<PaymentPlan> plans = paymentPlanService.getAllPaymentPlans();
        System.out.println("Found " + plans.size() + " payment plans in database");
        if (!plans.isEmpty()) {
            return false;
        }

        List<Round> rounds = roundService.getAllRounds();
        System.out.println("Found " + rounds.size() + " rounds in database");
        if (!rounds.isEmpty()) {
            return false;
        }

        List<MonthOption> monthOptions = monthOptionService.getAll();
        System.out.println("Found " + monthOptions.size() + " month options in database");
        if (!monthOptions.isEmpty()) {
            return false;
        }

        List<PaymentOption> paymentOptions = paymentOptionService.getAll();
        System.out.println("Found " + paymentOptions.size() + " payment options in database");
        if (!paymentOptions.isEmpty()) {
            return false;
        }

        System.out.println("Database is empty, proceeding with demo data generation");
        return true;
    }

    public void generateAllDemoData() {
        System.out.println("\n=== Starting Demo Data Generation ===");
        if (!isDatabaseEmpty()) {
            System.out.println("Demo data already exists. Skipping generation.");
            return;
        }
        System.out.println("Database is empty. Generating demo data...");

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Create month options
                System.out.println("\nCreating month options...");
                MonthOption monthOption2 = new MonthOption();
                monthOption2.setMonthsCount(2);
                monthOptionService.save(monthOption2);
                System.out.println("Created month option: 2 months");

                MonthOption monthOption4 = new MonthOption();
                monthOption4.setMonthsCount(4);
                monthOptionService.save(monthOption4);
                System.out.println("Created month option: 4 months");

                MonthOption monthOption6 = new MonthOption();
                monthOption6.setMonthsCount(6);
                monthOptionService.save(monthOption6);
                System.out.println("Created month option: 6 months");

                // Create payment options
                System.out.println("\nCreating payment options...");
                PaymentOption paymentOption1000 = new PaymentOption();
                paymentOption1000.setMonthlyPayment(1000);
                paymentOptionService.save(paymentOption1000);
                System.out.println("Created payment option: 1000 SEK");

                PaymentOption paymentOption2000 = new PaymentOption();
                paymentOption2000.setMonthlyPayment(2000);
                paymentOptionService.save(paymentOption2000);
                System.out.println("Created payment option: 2000 SEK");

                PaymentOption paymentOption3000 = new PaymentOption();
                paymentOption3000.setMonthlyPayment(3000);
                paymentOptionService.save(paymentOption3000);
                System.out.println("Created payment option: 3000 SEK");

                // Create payment plans
                System.out.println("\nCreating payment plans...");
                PaymentPlan plan1 = new PaymentPlan();
                plan1.setMonthsCount(5);
                plan1.setMonthlyPayment(new BigDecimal("2000"));
                paymentPlanService.createPaymentPlan(plan1);
                System.out.println("Created plan1: " + plan1.getMonthsCount() + " months, " + plan1.getMonthlyPayment() + " SEK");

                PaymentPlan plan2 = new PaymentPlan();
                plan2.setMonthsCount(3);
                plan2.setMonthlyPayment(new BigDecimal("3000"));
                paymentPlanService.createPaymentPlan(plan2);
                System.out.println("Created plan2: " + plan2.getMonthsCount() + " months, " + plan2.getMonthlyPayment() + " SEK");

                // Create users
                // Create users
                System.out.println("\nCreating users...");

                User admin = new User("admin", "123", "admin@demo.com", "123456-7890", "Admin", "User", "0701234567", "1234567890", "1234", new BigDecimal("0"), 0, Role.ADMIN);
                userService.createUser(admin);
                admin.setCurrentBalance(new BigDecimal("10000.00")); // Specific balance for admin
                userService.updateUser(admin); // Assuming UserService can update the user
                System.out.println("Created admin user: " + admin.getUsername() + " with ID: " + admin.getId() + ", Balance: " + admin.getCurrentBalance() + " SEK");

                User user1 = new User("user1", "123", "user1@demo.com", "123456-7891", "John", "Doe", "0701234568", "1234567891", "1234", new BigDecimal("2000"), 5, Role.USER);
                userService.createUser(user1);
                user1.setCurrentBalance(new BigDecimal("5000.00")); // Specific balance for user1
                userService.updateUser(user1);
                System.out.println("Created user1: " + user1.getUsername() + " with ID: " + user1.getId() + ", Balance: " + user1.getCurrentBalance() + " SEK");

                User user2 = new User("user2", "123", "user2@demo.com", "123456-7892", "Jane", "Smith", "0701234569", "1234567892", "1234", new BigDecimal("3000"), 3, Role.USER);
                userService.createUser(user2);
                user2.setCurrentBalance(new BigDecimal("7500.00")); // Specific balance for user2
                userService.updateUser(user2);
                System.out.println("Created user2: " + user2.getUsername() + " with ID: " + user2.getId() + ", Balance: " + user2.getCurrentBalance() + " SEK");

                User user3 = new User("user3", "123", "user3@demo.com", "123456-7893", "Bob", "Johnson", "0701234570", "1234567893", "1234", new BigDecimal("2000"), 5, Role.USER);
                userService.createUser(user3);
                user3.setCurrentBalance(new BigDecimal("6000.00")); // Specific balance for user3
                userService.updateUser(user3);
                System.out.println("Created user3: " + user3.getUsername() + " with ID: " + user3.getId() + ", Balance: " + user3.getCurrentBalance() + " SEK");

                User user4 = new User("user4", "123", "user4@demo.com", "123456-7894", "Alice", "Brown", "0701234571", "1234567894", "1234", new BigDecimal("3000"), 3, Role.USER);
                userService.createUser(user4);
                user4.setCurrentBalance(new BigDecimal("8000.00")); // Specific balance for user4
                userService.updateUser(user4);
                System.out.println("Created user4: " + user4.getUsername() + " with ID: " + user4.getId() + ", Balance: " + user4.getCurrentBalance() + " SEK");

                // Verify users were created
                List<User> allUsers = userService.getAllUsers();
                System.out.println("\nVerifying created users (with balances):");
                for (User user : allUsers) {
                    System.out.println("- " + user.getUsername() + " (ID: " + user.getId() + ", Role: " + user.getRole() + ", Balance: " + (user.getCurrentBalance() != null ? user.getCurrentBalance() + " SEK" : "N/A") + ")");
                }


                // Create groups
                System.out.println("\nCreating groups...");
                Group group1 = groupService.createGroup(plan1, new BigDecimal("2000"), 5);
                group1.setGroupName("Group 2000");
                groupService.updateGroup(group1);
                System.out.println("Created group1: " + group1.getGroupName());

                Group group2 = groupService.createGroup(plan2, new BigDecimal("3000"), 3);
                group2.setGroupName("Group 3000");
                groupService.updateGroup(group2);
                System.out.println("Created group2: " + group2.getGroupName());

                // Add participants to groups
                System.out.println("\nAdding participants to groups...");
                groupService.joinGroup(user1, group1, 1);
                groupService.joinGroup(user2, group1, 2);
                groupService.joinGroup(user3, group1, 3);
                groupService.joinGroup(user4, group1, 4);
                groupService.joinGroup(admin, group1, 5);
                System.out.println("Added participants to group1");

                groupService.joinGroup(user1, group2, 1);
                groupService.joinGroup(user2, group2, 2);
                groupService.joinGroup(user3, group2, 3);
                System.out.println("Added participants to group2");

                // Set groups to PENDING_APPROVAL
                group1.setStatus(GroupStatus.PENDING_APPROVAL);
                group2.setStatus(GroupStatus.PENDING_APPROVAL);
                groupService.updateGroup(group1);
                groupService.updateGroup(group2);
                System.out.println("Set groups to PENDING_APPROVAL status");

                // Create rounds and payments for group1
                System.out.println("\nCreating rounds and payments for group1...");
                LocalDateTime startDate = LocalDateTime.now();
                for (int i = 1; i <= 5; i++) {
                    Round round = new Round();
                    round.setGroup(group1);
                    round.setRoundNumber(i);
                    round.setStatus(RoundStatus.ACTIVE);  // Set to ACTIVE since group is PENDING_APPROVAL
                    round.setStartDate(startDate.plusMonths(i - 1));
                    round.setEndDate(startDate.plusMonths(i));
                    round.setAmount(new BigDecimal("2000"));
                    
                    // Set winner for this round
                    List<Participant> participants = participantService.getByGroup(group1);
                    Participant winner = null;
                    for (Participant p : participants) {
                        if (p.getTurnOrder() == i) {
                            winner = p;
                            round.setWinnerParticipant(p);
                            break;
                        }
                    }
                    
                    roundService.createRound(round);
                    System.out.println("Created round " + i + " for group1 with winner: " + (winner != null ? winner.getUser().getUsername() : "none"));

                    // Create payments for this round
                    for (Participant p : participants) {
                Payment payment = new Payment();
                        payment.setGroup(group1);
                        payment.setCreator(p.getUser());
                payment.setAmount(new BigDecimal("2000"));
                        
                        // Set payment status based on round number and participant
                        if (i == 1) {
                            // First round: all payments are PAID
                            payment.setStatus(PaymentStatus.PAID);
                            payment.setPaidAt(LocalDateTime.now().minusDays(2));
                        } else if (i == 2) {
                            // Second round: winner's payment is PAID, others are PENDING
                            if (p.getTurnOrder() == 2) {
                                payment.setStatus(PaymentStatus.PAID);
                                payment.setPaidAt(LocalDateTime.now().minusDays(1));
                            } else {
                                payment.setStatus(PaymentStatus.PENDING);
                            }
                        } else {
                            // Other rounds: all payments are PENDING
                payment.setStatus(PaymentStatus.PENDING);
                        }
                        
                payment.setPaymentBy(PaymentBy.USER_PAYMENT);
                        payment.setCreatedAt(LocalDateTime.now());
                        payment.setRound(round);
                        payment.setDueDate(round.getStartDate().plusDays(5));
                        payment.setPaymentPlan(plan1);
                        paymentService.createPayment(payment);
            }
                    System.out.println("Created payments for round " + i + " in group1");
                }

                // Create rounds and payments for group2
                System.out.println("\nCreating rounds and payments for group2...");
                for (int i = 1; i <= 3; i++) {
                    Round round = new Round();
                    round.setGroup(group2);
                    round.setRoundNumber(i);
                    round.setStatus(RoundStatus.ACTIVE);  // Set to ACTIVE since group is PENDING_APPROVAL
                    round.setStartDate(startDate.plusMonths(i - 1));
                    round.setEndDate(startDate.plusMonths(i));
                    round.setAmount(new BigDecimal("3000"));
                    
                    // Set winner for this round
                    List<Participant> participants = participantService.getByGroup(group2);
                    Participant winner = null;
                    for (Participant p : participants) {
                        if (p.getTurnOrder() == i) {
                            winner = p;
                            round.setWinnerParticipant(p);
                            break;
        }
    }

                    roundService.createRound(round);
                    System.out.println("Created round " + i + " for group2 with winner: " + (winner != null ? winner.getUser().getUsername() : "none"));

                    // Create payments for this round
                    for (Participant p : participants) {
                        Payment payment = new Payment();
                        payment.setGroup(group2);
                        payment.setCreator(p.getUser());
                        payment.setAmount(new BigDecimal("3000"));
                        
                        // Set payment status based on round number and participant
                        if (i == 1) {
                            // First round: all payments are PAID
                            payment.setStatus(PaymentStatus.PAID);
                            payment.setPaidAt(LocalDateTime.now().minusDays(2));
                        } else if (i == 2) {
                            // Second round: winner's payment is PAID, others are PENDING
                            if (p.getTurnOrder() == 2) {
                                payment.setStatus(PaymentStatus.PAID);
                                payment.setPaidAt(LocalDateTime.now().minusDays(1));
                            } else {
                                payment.setStatus(PaymentStatus.PENDING);
            }
                        } else {
                            // Other rounds: all payments are PENDING
                            payment.setStatus(PaymentStatus.PENDING);
                        }
                        
                        payment.setPaymentBy(PaymentBy.USER_PAYMENT);
                        payment.setCreatedAt(LocalDateTime.now());
                        payment.setRound(round);
                        payment.setDueDate(round.getStartDate().plusDays(5));
                        payment.setPaymentPlan(plan2);
                        paymentService.createPayment(payment);
                    }
                    System.out.println("Created payments for round " + i + " in group2");
                }

                transaction.commit();
                System.out.println("\nDemo data generation completed successfully!");
                
                // Final verification
                System.out.println("\nFinal database state:");
                System.out.println("Users: " + userService.getAllUsers().size());
                System.out.println("Groups: " + groupService.getAllGroups().size());
                System.out.println("Payments: " + paymentService.getAllPayments().size());
                System.out.println("Rounds: " + roundService.getAllRounds().size());
                System.out.println("Month Options: " + monthOptionService.getAll().size());
                System.out.println("Payment Options: " + paymentOptionService.getAll().size());
                
            } catch (Exception e) {
                transaction.rollback();
                System.err.println("Error generating demo data: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
    }
}







