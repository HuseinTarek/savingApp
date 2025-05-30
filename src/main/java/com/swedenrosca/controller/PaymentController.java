package com.swedenrosca.controller;

import com.swedenrosca.model.*;
import com.swedenrosca.service.PaymentService;
import java.math.BigDecimal;
import java.util.List;

public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public List<Payment> getGroupPayments(Long groupId) {
        return paymentService.getByGroupId(groupId);
    }

    public BigDecimal getTotalPaymentsByGroup(Long groupId) {
        List<Payment> payments = paymentService.getByGroupId(groupId);
        if (payments == null || payments.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}