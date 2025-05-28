package com.swedenrosca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "monthly_payments")
public class MonthlyPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The participant this payment record belongs to
    @ManyToOne(optional = false)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    // The month number in the round (e.g., 1 to 12)
    @Column(nullable = false)
    private int monthNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    // Whether the company paid on behalf of the participant
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentBy paymentBy ;


    // Constructors
    public MonthlyPayment() {}

    public MonthlyPayment(Participant participant, int monthNumber) {
        this.participant = participant;
        this.monthNumber = monthNumber;
    }

    public MonthlyPayment(Participant participant, int monthNumber, PaymentStatus status, PaymentBy paymentBy) {
        this.participant = participant;
        this.monthNumber = monthNumber;
        this.status = status;
        this.paymentBy = paymentBy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public int getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentBy getPaymentBy() {
        return paymentBy;
    }

    public void setPaymentBy(PaymentBy paymentBy) {
        this.paymentBy = paymentBy;
    }

    @Override
    public String toString() {
        return "MonthlyPayment{" +
                "id=" + id +
                ", participant=" + participant +
                ", monthNumber=" + monthNumber +
                ", status=" + status +
                ", paymentBy=" + paymentBy +
                '}';
    }
}

