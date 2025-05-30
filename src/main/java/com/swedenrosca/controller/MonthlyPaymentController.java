package com.swedenrosca.controller;

import com.swedenrosca.model.*;
import com.swedenrosca.service.MonthlyPaymentService;

import java.util.List;
import java.util.Scanner;

public class MonthlyPaymentController {

    private final Scanner scanner = new Scanner(System.in);
    private final MonthlyPaymentService monthlyPaymentService;

    public MonthlyPaymentController(MonthlyPaymentService monthlyPaymentService) {
        this.monthlyPaymentService = monthlyPaymentService;
    }

    // Show all payments for a group
    public void showPaymentsByGroup() {
        System.out.print("Enter group ID: ");
        Long groupId = scanner.nextLong();
        List<MonthlyPayment> payments = monthlyPaymentService.getByGroupId(groupId);
        if (payments.isEmpty()) {
            System.out.println("No payments found for this group.");
        } else {
            payments.forEach(System.out::println);
        }
    }

    // Show all payments for a group and specific month
    public void showPaymentsByGroupAndMonth() {
        System.out.print("Enter group ID: ");
        Long groupId = scanner.nextLong();
        System.out.print("Enter month number (1-12): ");
        int month = scanner.nextInt();
        List<MonthlyPayment> payments = monthlyPaymentService.getByGroupIdAndMonth(groupId, month);
        if (payments.isEmpty()) {
            System.out.println("No payments found for this month.");
        } else {
            payments.forEach(System.out::println);
        }
    }

    // Mark a payment as paid
    public void markPaymentAsPaid() {
        System.out.print("Enter payment ID: ");
        Long paymentId = scanner.nextLong();
        MonthlyPayment monthlyPayment = monthlyPaymentService.getById(paymentId);
        if (monthlyPayment == null) {
            System.out.println("Payment not found.");
            return;
        }
        System.out.print("Was it paid by company? (true/false): ");
        boolean paidByCompany = scanner.nextBoolean();
        monthlyPayment.setPaymentBy(PaymentBy.USER_PAYMENT);
        monthlyPaymentService.updateMonthlyPayment(monthlyPayment);
        System.out.println("Payment marked as paid.");
    }
}
