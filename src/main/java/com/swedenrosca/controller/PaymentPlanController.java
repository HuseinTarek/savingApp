package com.swedenrosca.controller;

import com.swedenrosca.model.PaymentPlan;
import com.swedenrosca.repository.PaymentPlanRepository;
import com.swedenrosca.repository.PaymentRepository;

import java.util.List;

public class PaymentPlanController {

    private final PaymentPlanRepository paymentPlanRepository;
    private final PaymentRepository paymentRepository;

    /**
     * Constructor injects SessionFactory to initialize repositories
     */
    public PaymentPlanController() {
        this.paymentPlanRepository = new PaymentPlanRepository();
        this.paymentRepository = new PaymentRepository();
    }

    /**
     * Creates a new PaymentPlan and updates existing Payment records accordingly
     */
    public PaymentPlan decideMonthlyPaymentAndMonthsCount(int monthlyPayment, int monthsCount) {
        // Instantiate and save a new payment plan
        PaymentPlan plan = new PaymentPlan(monthsCount, java.math.BigDecimal.valueOf(monthlyPayment));
        paymentPlanRepository.save(plan);
        // Bulk update Payments based on the new plan
     //   int updatedRows = paymentRepository.decideMonthlyPaymentAndMonthsCount(monthlyPayment, monthsCount);
        return plan;
    }

    /**
     * Retrieves all available payment plans for selection
     */
    public List<PaymentPlan> getAll() {
        return paymentPlanRepository.getAll();
    }

    public PaymentPlan save(PaymentPlan plan) {
        paymentPlanRepository.save(plan);
        return plan;
    }
    /**
     * Retrieves payment plans created by a specific admin user
     */
    public List<PaymentPlan> getPlansByCreator(String creator) {
        return paymentPlanRepository.findByCreatedBy(creator);
    }
}