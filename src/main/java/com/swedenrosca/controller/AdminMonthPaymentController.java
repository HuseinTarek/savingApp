package com.swedenrosca.controller;

import com.swedenrosca.model.MonthOption;
import com.swedenrosca.model.PaymentOption;
import com.swedenrosca.repository.MonthOptionRepository;
import com.swedenrosca.repository.PaymentOptionRepository;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Scanner;

public class AdminMonthPaymentController {

    private final MonthOptionRepository monthRepo;
    private final PaymentOptionRepository paymentRepo;

    public AdminMonthPaymentController() {
        this.monthRepo   = new MonthOptionRepository();
        this.paymentRepo = new PaymentOptionRepository();
    }

    /**
     * Main admin menu loop
     */
    public void adminMonthPaymentMenu(Scanner scanner) {
        while (true) {
            System.out.println("1) Manage Month Options");
            System.out.println("2) Manage Payment Options");
            System.out.println("0) Exit");
            System.out.print("Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> manageMonthOptions(scanner);
                case 2 -> managePaymentOptions(scanner);
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    /**
     * Manage CRUD for MonthOption
     */
    private void manageMonthOptions(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Month Options ---");
            System.out.println("1) List all");
            System.out.println("2) Add new");
            System.out.println("3) Edit");
            System.out.println("4) Delete");
            System.out.println("0) Back");
            System.out.print("Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> listMonths();
                case 2 -> addMonth(scanner);
                case 3 -> editMonth(scanner);
                case 4 -> deleteMonth(scanner);
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void listMonths() {
        List<MonthOption> options = monthRepo.getAll();
        options.forEach(opt ->
                System.out.printf("ID: %d → %d months%n", opt.getId(), opt.getMonthsCount())
        );
    }

    private void addMonth(Scanner scanner) {
        System.out.print("Enter months count to add: ");
        int count = scanner.nextInt();
        scanner.nextLine();
        monthRepo.save(new MonthOption(count));
        System.out.println("Month option added.");
    }

    private void editMonth(Scanner scanner) {
        System.out.print("Enter ID of month option to edit: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        MonthOption opt = monthRepo.findById(id);
        if (opt != null) {
            System.out.print("Enter new months count: ");
            int newCount = scanner.nextInt();
            scanner.nextLine();
            opt.setMonthsCount(newCount);
            monthRepo.update(opt);
            System.out.println("Month option updated.");
        } else {
            System.out.println("Option not found.");
        }
    }

    private void deleteMonth(Scanner scanner) {
        System.out.print("Enter ID of month option to delete: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        monthRepo.deleteById(id);
        System.out.println("Month option deleted.");
    }

    /**
     * Manage CRUD for PaymentOption
     */
    private void managePaymentOptions(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Payment Options ---");
            System.out.println("1) List all");
            System.out.println("2) Add new");
            System.out.println("3) Edit");
            System.out.println("4) Delete");
            System.out.println("0) Back");
            System.out.print("Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> listPayments();
                case 2 -> addPayment(scanner);
                case 3 -> editPayment(scanner);
                case 4 -> deletePayment(scanner);
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void listPayments() {
        List<PaymentOption> options = paymentRepo.getAllMonthlyPayments();
        options.forEach(opt ->
                System.out.printf("ID: %d → %d SEK per month%n", opt.getId(), opt.getMonthlyPayment())
        );
    }

    private void addPayment(Scanner scanner) {
        System.out.print("Enter monthly payment to add: ");
        int amount = scanner.nextInt();
        scanner.nextLine();
        paymentRepo.save(new PaymentOption(amount));
        System.out.println("Payment option added.");
    }

    private void editPayment(Scanner scanner) {
        System.out.print("Enter ID of payment option to edit: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        PaymentOption opt = paymentRepo.findById(id);
        if (opt != null) {
            System.out.print("Enter new monthly payment: ");
            int newAmount = scanner.nextInt();
            scanner.nextLine();
            opt.setMonthlyPayment(newAmount);
            paymentRepo.update(opt);
            System.out.println("Payment option updated.");
        } else {
            System.out.println("Option not found.");
        }
    }

    private void deletePayment(Scanner scanner) {
        System.out.print("Enter ID of payment option to delete: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        paymentRepo.deleteById(id);
        System.out.println("Payment option deleted.");
    }
}

