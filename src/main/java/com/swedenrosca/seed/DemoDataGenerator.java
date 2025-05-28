package com.swedenrosca.seed;

import com.swedenrosca.controller.GroupController;
import com.swedenrosca.model.*;
import com.swedenrosca.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class DemoDataGenerator {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ParticipantRepository participantRepository;
    private final GroupController groupController;
    private final RoundRepository roundRepository;
    private final PaymentRepository paymntRepository;
    private final PaymentPlanRepository paymentPlanRepository;
    private final MonthOptionRepository monthOptionRepository;
    private final PaymentOptionRepository paymentOptionRepository;

    public DemoDataGenerator(RoundRepository roundRepository, PaymentRepository paymntRepository, PaymentPlanRepository paymentPlanRepository, MonthOptionRepository monthOptionRepository, PaymentOptionRepository paymentOptionRepository) {
        this.roundRepository = roundRepository;
        this.paymntRepository = paymntRepository;
        this.paymentPlanRepository = paymentPlanRepository;
        this.monthOptionRepository = monthOptionRepository;
        this.paymentOptionRepository = paymentOptionRepository;
        this.userRepository = new UserRepository();
        this.groupRepository = new GroupRepository();
        this.participantRepository = new ParticipantRepository();
        this.groupController = new GroupController(participantRepository, groupRepository, userRepository, roundRepository, paymntRepository, paymentPlanRepository);
    }

    public void generateAllDemoData() {
        System.out.println("Checking if demo data needs to be generated...");
        
        // Check if we already have demo data
        if (!isDatabaseEmpty()) {
            System.out.println("Demo data already exists. Skipping generation.");
            return;
        }

        System.out.println("Generating demo data...");

        // Save users
        saveUserIfNotExists(new User("admin", "admin", "admin@demo.com", "199001010001", "Admin", "User", "070100001", "123456781", "8901", new BigDecimal("1000"), 6, Role.ADMIN));
        saveUserIfNotExists(new User("user1", "123", "user1@demo.com", "199001010002", "User", "One", "070100002", "123456782", "8902", new BigDecimal("1000"), 6, Role.USER));
        saveUserIfNotExists(new User("user2", "123", "user2@demo.com", "199001010003", "User", "Two", "070100003", "123456783", "8903", new BigDecimal("2000"), 10, Role.USER));
        saveUserIfNotExists(new User("user3", "123", "user3@demo.com", "199001010004", "User", "Three", "070100004", "123456784", "8904", new BigDecimal("2000"), 10, Role.USER));
        saveUserIfNotExists(new User("user4", "123", "user4@demo.com", "199001010005", "User", "Four", "070100005", "123456785", "8905", new BigDecimal("3000"), 12, Role.USER));
        saveUserIfNotExists(new User("user5", "123", "user5@demo.com", "199001010006", "User", "Five", "070100006", "123456786", "8906", new BigDecimal("3000"), 12, Role.USER));
        saveUserIfNotExists(new User("company", "admin", "company@rosca.com", "199000000000", "Company", "Admin", "0700000000", "999999999", "9999", BigDecimal.ZERO, 0, Role.COMPANY));

        // Save payment plans - one for each combination
        paymentPlanRepository.save(new PaymentPlan(2, new BigDecimal("1000")));
        paymentPlanRepository.save(new PaymentPlan(4, new BigDecimal("2000")));
        paymentPlanRepository.save(new PaymentPlan(6, new BigDecimal("3000")));

        // Save month options - one for each duration
        monthOptionRepository.save(new MonthOption(2));
        monthOptionRepository.save(new MonthOption(4));
        monthOptionRepository.save(new MonthOption(6));

        // Save payment options - one for each amount
        paymentOptionRepository.save(new PaymentOption(1000));
        paymentOptionRepository.save(new PaymentOption(2000));
        paymentOptionRepository.save(new PaymentOption(3000));

        System.out.println("Demo data generation completed.");
    }

    private boolean isDatabaseEmpty() {
        try {
            // Check if we have any users (excluding admin and company)
            List<User> users = userRepository.getAll();
            if (users.size() > 2) { // More than just admin and company
                return false;
            }

            // Check if we have any payment plans
            if (!paymentPlanRepository.getAll().isEmpty()) {
                return false;
            }

            // Check if we have any month options
            if (!monthOptionRepository.getAll().isEmpty()) {
                return false;
            }

            // Check if we have any payment options
            if (!paymentOptionRepository.getAllMonthlyPayments().isEmpty()) {
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error checking database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void saveUserIfNotExists(User user) {
        if (!userRepository.existsByUsername(user.getUsername()) &&
                !userRepository.existsByBankAccount(user.getBankAccount())) {
            userRepository.save(user);
        }
    }
}







