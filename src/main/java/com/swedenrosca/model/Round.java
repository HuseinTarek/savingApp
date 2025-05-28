package com.swedenrosca.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "rounds")
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "winner_participant_id", nullable = true)
    private Participant winnerParticipant;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoundStatus status;

    // Default constructor
    public Round() {
        this.status = RoundStatus.PENDING_APPROVAL; // Default status
        this.startDate = LocalDateTime.now();
    }



    public Round(Group group, Participant winnerParticipant, List<Payment> payments, int roundNumber, BigDecimal amount, LocalDateTime startDate, LocalDateTime endDate, RoundStatus status) {
        this.group = group;
        this.winnerParticipant = winnerParticipant;
        this.payments = payments;
        this.roundNumber = roundNumber;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Constructor with winner user
    public Round(Group group, Participant winnerParticipant) {
        this();
        this.group = group;
        this.winnerParticipant = winnerParticipant;
    }

    public void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startDate)) {
            status = RoundStatus.PENDING_APPROVAL;
        } else if (now.isAfter(endDate)) {
            status = RoundStatus.COMPLETED;
        } else {
            status = RoundStatus.ACTIVE;
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Group getGroup() {
        return group;
    }

    public Participant getWinnerParticipant() {
        return winnerParticipant;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public RoundStatus getStatus() {
        return status;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setWinnerParticipant(Participant winnerUser) {
        this.winnerParticipant = winnerUser;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public void setStatus(RoundStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Round{" +
                "id=" + id +
                ", roundNumber=" + roundNumber +
                ", amount=" + amount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, group, winnerParticipant, roundNumber, amount, startDate, endDate, status);
    }

    public void setTurnOrder(int turnOrder) {
        this.roundNumber = turnOrder;
    }
}