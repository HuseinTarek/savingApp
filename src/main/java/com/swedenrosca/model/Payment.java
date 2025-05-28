package com.swedenrosca.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "payer_id", nullable = false)
    private Participant payer;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = true)
    private Participant participant;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "service_fee")
    private BigDecimal serviceFee;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "round_id")
    private Round round;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentBy paymentBy;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "payment_plan_id")
    private PaymentPlan paymentPlan;

    // No-arg constructor
    public Payment() {}

    // All-args constructor (for convenience)
    public Payment(Long id, Group group, Participant payer, Participant participant, BigDecimal amount, PaymentStatus status, LocalDateTime dueDate, BigDecimal serviceFee, LocalDateTime createdAt, LocalDateTime paidAt, Round round, PaymentBy paymentBy, User creator, PaymentPlan paymentPlan) {
        this.id = id;
        this.group = group;
        this.payer = payer;
        this.participant = participant;
        this.amount = amount;
        this.status = status;
        this.dueDate = dueDate;
        this.serviceFee = serviceFee;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.round = round;
        this.paymentBy = paymentBy;
        this.creator = creator;
        this.paymentPlan = paymentPlan;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }

    public Participant getPayer() { return payer; }
    public void setPayer(Participant payer) { this.payer = payer; }

    public Participant getParticipant() { return participant; }
    public void setParticipant(Participant participant) { this.participant = participant; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public BigDecimal getServiceFee() { return serviceFee; }
    public void setServiceFee(BigDecimal serviceFee) { this.serviceFee = serviceFee; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public PaymentStatus getPaymentStatus() { return status; }
    public void setPaymentStatus(PaymentStatus status) { this.status = status; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public Round getRound() { return round; }
    public void setRound(Round round) { this.round = round; }

    public PaymentBy getPaymentBy() { return paymentBy; }
    public void setPaymentBy(PaymentBy paymentBy) { this.paymentBy = paymentBy; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public PaymentPlan getPaymentPlan() { return paymentPlan; }
    public void setPaymentPlan(PaymentPlan paymentPlan) { this.paymentPlan = paymentPlan; }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", group=" + group +
                ", payer=" + payer +
                ", recipient=" + participant +
                ", amount=" + amount +
                ", status=" + status +
                ", dueDate=" + dueDate +
                ", serviceFee=" + serviceFee +
                ", createdAt=" + createdAt +
                ", paidAt=" + paidAt +
                ", paymentBy=" + paymentBy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id) &&
                Objects.equals(group, payment.group) &&
                Objects.equals(payer, payment.payer) &&
                Objects.equals(amount, payment.amount) &&
                status == payment.status &&
                Objects.equals(dueDate, payment.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, group, payer, amount, status, dueDate);
    }
}