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
        System.out.println("\n=== Starting Demo Data Generation (Users Only) ===");
        if (!isDatabaseEmpty()) {
            System.out.println("Demo data already exists. Skipping generation.");
            return;
        }
        System.out.println("Database is empty. Generating demo users only...");

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Create month options
                MonthOption monthOption2 = new MonthOption();
                monthOption2.setMonthsCount(2);
                monthOptionService.save(monthOption2);

                MonthOption monthOption4 = new MonthOption();
                monthOption4.setMonthsCount(4);
                monthOptionService.save(monthOption4);

                MonthOption monthOption6 = new MonthOption();
                monthOption6.setMonthsCount(6);
                monthOptionService.save(monthOption6);

                // Create payment options
                PaymentOption paymentOption1000 = new PaymentOption();
                paymentOption1000.setMonthlyPayment(1000);
                paymentOptionService.save(paymentOption1000);

                PaymentOption paymentOption2000 = new PaymentOption();
                paymentOption2000.setMonthlyPayment(2000);
                paymentOptionService.save(paymentOption2000);

                PaymentOption paymentOption3000 = new PaymentOption();
                paymentOption3000.setMonthlyPayment(3000);
                paymentOptionService.save(paymentOption3000);

                // Only create users
                System.out.println("\nCreating users...");

                User admin = new User("admin", "123", "admin@demo.com", "123456-7890", "Admin", "User", "0701234567", "1234567890", "1234", new BigDecimal("0"), 0, Role.ADMIN);
                userService.createUser(admin);
                admin.setCurrentBalance(new BigDecimal("10000.00"));
                userService.updateUser(admin);
                System.out.println("Created admin user: " + admin.getUsername() + " with ID: " + admin.getId() + ", Balance: " + admin.getCurrentBalance() + " SEK");

                User user1 = new User("user1", "123", "user1@demo.com", "123456-7891", "John", "Doe", "0701234568", "1234567891", "1234", new BigDecimal("2000"), 5, Role.USER);
                userService.createUser(user1);
                user1.setCurrentBalance(new BigDecimal("5000.00"));
                userService.updateUser(user1);
                System.out.println("Created user1: " + user1.getUsername() + " with ID: " + user1.getId() + ", Balance: " + user1.getCurrentBalance() + " SEK");

                User user2 = new User("user2", "123", "user2@demo.com", "123456-7892", "Jane", "Smith", "0701234569", "1234567892", "1234", new BigDecimal("3000"), 3, Role.USER);
                userService.createUser(user2);
                user2.setCurrentBalance(new BigDecimal("7500.00"));
                userService.updateUser(user2);
                System.out.println("Created user2: " + user2.getUsername() + " with ID: " + user2.getId() + ", Balance: " + user2.getCurrentBalance() + " SEK");

                User user3 = new User("user3", "123", "user3@demo.com", "123456-7893", "Bob", "Johnson", "0701234570", "1234567893", "1234", new BigDecimal("2000"), 5, Role.USER);
                userService.createUser(user3);
                user3.setCurrentBalance(new BigDecimal("6000.00"));
                userService.updateUser(user3);
                System.out.println("Created user3: " + user3.getUsername() + " with ID: " + user3.getId() + ", Balance: " + user3.getCurrentBalance() + " SEK");

                User user4 = new User("user4", "123", "user4@demo.com", "123456-7894", "Alice", "Brown", "0701234571", "1234567894", "1234", new BigDecimal("3000"), 3, Role.USER);
                userService.createUser(user4);
                user4.setCurrentBalance(new BigDecimal("8000.00"));
                userService.updateUser(user4);
                System.out.println("Created user4: " + user4.getUsername() + " with ID: " + user4.getId() + ", Balance: " + user4.getCurrentBalance() + " SEK");

                transaction.commit();
                System.out.println("\nDemo user data generation completed successfully!");
                // Final verification
                System.out.println("\nFinal database state:");
                System.out.println("Users: " + userService.getAllUsers().size());
            } catch (Exception e) {
                transaction.rollback();
                System.err.println("Error generating demo users: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
    }
}







