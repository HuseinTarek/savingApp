package com.swedenrosca.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_plan")
public class PaymentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "months_count", nullable = false)
    private int monthsCount;

    @Column(name = "monthly_payment", nullable = false)
    private BigDecimal monthlyPayment;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // English comment: Default constructor required by JPA
    public PaymentPlan() {}

    // English comment: Convenience constructor
    public PaymentPlan(int monthsCount, BigDecimal monthlyPayment) {
        this.monthsCount = monthsCount;
        this.monthlyPayment = monthlyPayment;
    }

    // English comment: Auto-set createdAt before persisting
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // English comment: Getter for id
    public Long getId() {
        return id;
    }

    // English comment: Setter for id (usually not needed)
    public void setId(Long id) {
        this.id = id;
    }

    // English comment: Getter for monthsCount
    public int getMonthsCount() {
        return monthsCount;
    }

    // English comment: Setter for monthsCount
    public void setMonthsCount(int monthsCount) {
        this.monthsCount = monthsCount;
    }

    // English comment: Getter for monthlyPayment
    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    // English comment: Setter for monthlyPayment
    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    // English comment: Getter for group
    public Group getGroup() {
        return group;
    }

    // English comment: Setter for group
    public void setGroup(Group group) {
        this.group = group;
    }

    // English comment: Getter for createdAt
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // English comment: No setter for createdAt to keep it immutable
}






