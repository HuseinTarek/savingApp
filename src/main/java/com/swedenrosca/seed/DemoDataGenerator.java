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

    public void generateAllDemoData() {
        System.out.println("Checking if demo data needs to be generated...");
        if (!isDatabaseEmpty()) {
            System.out.println("Demo data already exists. Skipping generation.");
            return;
        }
        System.out.println("Generating demo data...");

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Save month options and payment options
            MonthOption month2 = new MonthOption(2);
            MonthOption month4 = new MonthOption(4);
            MonthOption month6 = new MonthOption(6);
            monthOptionService.save(month2);
            monthOptionService.save(month4);
            monthOptionService.save(month6);

            PaymentOption payment1000 = new PaymentOption(1000);
            PaymentOption payment2000 = new PaymentOption(2000);
            PaymentOption payment3000 = new PaymentOption(3000);
            paymentOptionService.save(payment1000);
            paymentOptionService.save(payment2000);
            paymentOptionService.save(payment3000);

            // Create and save demo users
            User admin = new User("admin", "admin", "admin@demo.com", "199001010001", "Admin", "User", "070100001", "123456781", "8901", new BigDecimal("1000"), 6, Role.ADMIN);
            User user1 = new User("user1", "123", "user1@demo.com", "199001010002", "User", "One", "070100002", "123456782", "8902", new BigDecimal("1000"), 2, Role.USER);
            User user2 = new User("user2", "123", "user2@demo.com", "199001010003", "User", "Two", "070100003", "123456783", "8903", new BigDecimal("2000"), 2, Role.USER);
            User company = new User("company", "admin", "company@rosca.com", "199000000000", "Company", "Admin", "0700000000", "999999999", "9999", BigDecimal.ZERO, 0, Role.COMPANY);
            
            System.out.println("Creating demo users:");
            System.out.println("user1 - Username: " + user1.getUsername() + ", Password: " + user1.getPassword() + ", Role: " + user1.getRole());
            
            userService.createUser(admin);
            userService.createUser(user1);
            userService.createUser(user2);
            userService.createUser(company);
            
            System.out.println("Demo users created successfully");

            // Save payment plans
            PaymentPlan plan1 = new PaymentPlan(2, new BigDecimal("1000"));
            PaymentPlan plan2 = new PaymentPlan(4, new BigDecimal("2000"));
            PaymentPlan plan3 = new PaymentPlan(6, new BigDecimal("3000"));
            paymentPlanService.createPaymentPlan(plan1);
            paymentPlanService.createPaymentPlan(plan2);
            paymentPlanService.createPaymentPlan(plan3);

            // Create a payment plan for the mock group
            PaymentPlan mockPlan = new PaymentPlan();
            mockPlan.setMonthsCount(4);
            mockPlan.setMonthlyPayment(new BigDecimal("2000"));
            paymentPlanService.createPaymentPlan(mockPlan);

            // Create the mock group
            Group mockGroup = new Group();
            mockGroup.setPaymentPlan(mockPlan);
            mockGroup.setMonthlyContribution(new BigDecimal("2000"));
            mockGroup.setMaxMembers(4);
            mockGroup.setStatus(GroupStatus.WAITING_FOR_MEMBERS);
            mockGroup.setPaymentBy(PaymentBy.USER_PAYMENT);
            mockGroup.setStartDate(java.time.LocalDateTime.now());
            mockGroup.setEndDate(java.time.LocalDateTime.now().plusMonths(4));
            mockGroup.setTotalAmount(new BigDecimal("8000"));
            mockGroup.setGroupName(mockGroup.generateGroupName(mockGroup.getStartDate(), mockGroup.getEndDate(), mockGroup.getTotalAmount()));
            groupService.createGroup(mockPlan, mockGroup.getMonthlyContribution(), mockGroup.getMaxMembers());

            // Create and save mock users
            User userA = new User("mockuserA", "123", "a@mock.com", "1111", "A", "Mock", "0709999000", "999111", "9222", new BigDecimal("0"), 0, Role.USER);
            User userB = new User("mockuserB", "123", "b@mock.com", "2222", "B", "Mock", "0709999001", "999112", "9223", new BigDecimal("0"), 0, Role.USER);
            User userC = new User("mockuserC", "123", "c@mock.com", "3333", "C", "Mock", "0709999002", "999113", "9224", new BigDecimal("0"), 0, Role.USER);
            User userD = new User("mockuserD", "123", "d@mock.com", "4444", "D", "Mock", "0709999003", "999114", "9225", new BigDecimal("0"), 0, Role.USER);
            userService.createUser(userA);
            userService.createUser(userB);
            userService.createUser(userC);
            userService.createUser(userD);

            // Create and save participants
            Participant participantA = new Participant();
            participantA.setUser(userA);
            participantA.setGroup(mockGroup);
            participantA.setPaymentBy(PaymentBy.USER_PAYMENT);
            participantA.setRole(GroupRole.PAYER);
            participantA.setTurnOrder(1);
            participantService.addParticipant(participantA);

            Participant participantB = new Participant();
            participantB.setUser(userB);
            participantB.setGroup(mockGroup);
            participantB.setPaymentBy(PaymentBy.USER_PAYMENT);
            participantB.setRole(GroupRole.PAYER);
            participantB.setTurnOrder(2);
            participantService.addParticipant(participantB);

            Participant participantC = new Participant();
            participantC.setUser(userC);
            participantC.setGroup(mockGroup);
            participantC.setPaymentBy(PaymentBy.USER_PAYMENT);
            participantC.setRole(GroupRole.PAYER);
            participantC.setTurnOrder(3);
            participantService.addParticipant(participantC);

            Participant participantD = new Participant();
            participantD.setUser(userD);
            participantD.setGroup(mockGroup);
            participantD.setPaymentBy(PaymentBy.USER_PAYMENT);
            participantD.setRole(GroupRole.PAYER);
            participantD.setTurnOrder(4);
            participantService.addParticipant(participantD);

            // Update group status to PENDING_APPROVAL
            mockGroup.setStatus(GroupStatus.PENDING_APPROVAL);
            groupService.updateGroup(mockGroup);

            // Create and save mock payments
            LocalDateTime now = java.time.LocalDateTime.now();
            List<Participant> mockParticipants = participantService.getByGroup(mockGroup);
            for (Participant participant : mockParticipants) {
                Payment payment = new Payment();
                payment.setGroup(mockGroup);
                payment.setCreator(participant.getUser());
                payment.setAmount(new BigDecimal("2000"));
                payment.setStatus(PaymentStatus.PENDING);
                payment.setPaymentBy(PaymentBy.USER_PAYMENT);
                payment.setCreatedAt(now);
                payment.setDueDate(now.plusDays(5));
                paymentService.save(payment);
            }

            session.getTransaction().commit();
            System.out.println("Created mock group with 4 members, status PENDING_APPROVAL, and mock payments");
            System.out.println("Demo data generation completed.");
        }
    }

    private boolean isDatabaseEmpty() {
        try (Session session = sessionFactory.openSession()) {
            // Check if any data exists in any of the repositories
            if (!userService.getAllUsers().isEmpty()) {
                return false;
            }
            if (!groupService.getAllGroups().isEmpty()) {
                return false;
            }
            if (!paymentService.getAllPayments().isEmpty()) {
                return false;
            }
            if (!paymentPlanService.getAllPaymentPlans().isEmpty()) {
                return false;
            }
            if (!roundService.getAllRounds().isEmpty()) {
                return false;
            }
            if (!monthOptionService.getAll().isEmpty()) {
                return false;
            }
            if (!paymentOptionService.getAll().isEmpty()) {
                return false;
            }
            return true;
        }
    }
}







