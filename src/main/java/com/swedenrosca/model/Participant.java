package com.swedenrosca.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentBy paymentBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole role; // PAYER or RECIPIENT

    @Column(nullable = false)
    private int turnOrder; // in which order number the user will get his money 1, 2 etc

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "receive_status", nullable = false)
    private ReceiveStatus receiveStatus = ReceiveStatus.NOT_RECEIVED;


    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    // Constructors
    public Participant() {}

    public Participant(User user, Group group, PaymentBy paymentBy, GroupRole role, int turnOrder, PaymentStatus paymentStatus, LocalDateTime paidAt, ReceiveStatus receiveStatus, LocalDateTime receivedAt, PaymentBy paymentBy1) {
        this.user = user;
        this.group = group;
        this.paymentBy = paymentBy;
        this.role = role;
        this.turnOrder = turnOrder;
        this.paymentStatus = paymentStatus;
        this.paidAt = paidAt;
        this.receiveStatus = receiveStatus;
        this.receivedAt = receivedAt;
        this.paymentBy = paymentBy1;
    }

    // Getters and Setters

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public ReceiveStatus getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(ReceiveStatus receiveStatus) {
        this.receiveStatus = ReceiveStatus.NOT_RECEIVED;;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public GroupRole getRole() {
        return role;
    }

    public void setRole(GroupRole role) {
        this.role = role;
    }

    public int getTurnOrder() {
        return turnOrder;
    }

    public void setTurnOrder(int turnOrder) {
        this.turnOrder = turnOrder;
    }

    public PaymentStatus getStatus() {
        return paymentStatus;
    }

    public void setStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public PaymentBy getPaymentBy() {
        return paymentBy;
    }

    public void setPaymentBy(PaymentBy paymentBy) {
        this.paymentBy = paymentBy;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", user=" + user +
                ", group=" + group +
                ", paymentType=" + paymentBy +
                ", role=" + role +
                ", turnOrder=" + turnOrder +
                ", paymentStatus=" + paymentStatus +
                ", paidAt=" + paidAt +
                ", receiveStatus=" + receiveStatus +
                ", receivedAt=" + receivedAt +
                ", paymentType=" + paymentBy +
                '}';
    }
}

