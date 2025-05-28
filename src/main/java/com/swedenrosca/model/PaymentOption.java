package com.swedenrosca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_option")
public class PaymentOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "monthly_payment", nullable = false)
    private int monthlyPayment;

    // Default constructor for JPA
    public PaymentOption() {}

    // Convenience constructor
    public PaymentOption(int monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public Long getId() {
        return id;
    }

    public int getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(int monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }
}

