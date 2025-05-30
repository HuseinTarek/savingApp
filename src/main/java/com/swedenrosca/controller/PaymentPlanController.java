package com.swedenrosca.controller;

import com.swedenrosca.model.PaymentPlan;
import com.swedenrosca.service.PaymentPlanService;
import java.util.List;

public class PaymentPlanController {
    private final PaymentPlanService paymentPlanService;

    public PaymentPlanController(PaymentPlanService paymentPlanService) {
        this.paymentPlanService = paymentPlanService;
    }

    /**
     * Creates a new PaymentPlan and updates existing Payment records accordingly
     */
    public PaymentPlan decideMonthlyPaymentAndMonthsCount(int monthlyPayment, int monthsCount) {
        return paymentPlanService.createPaymentPlan(monthlyPayment, monthsCount);
    }

    /**
     * Retrieves all available payment plans for selection
     */
    public List<PaymentPlan> getAll() {
        return paymentPlanService.getAllPaymentPlans();
    }

    public PaymentPlan save(PaymentPlan plan) {
        paymentPlanService.createPaymentPlan(plan);
        return plan;
    }

    /**
     * Retrieves payment plans created by a specific admin user
     */
    public List<PaymentPlan> getPlansByCreator(String creator) {
        return paymentPlanService.getPlansByCreator(creator);
    }
}