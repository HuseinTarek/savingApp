package com.swedenrosca.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "saving_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "max_members")
    private int maxMembers;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupStatus status;

    @Column(name = "monthly_contribution", nullable = false)
    private BigDecimal monthlyContribution;

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "group")
    private List<Round> rounds = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "payment_plan_id")
    private PaymentPlan paymentPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentBy paymentBy;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<PaymentPlan> paymentPlans = new ArrayList<>();

    // No-arg constructor
    public Group() {}

    // All-args constructor (for convenience)
    public Group(String groupName, BigDecimal totalAmount, int maxMembers, LocalDateTime startDate, LocalDateTime endDate, GroupStatus status, BigDecimal monthlyContribution, List<Participant> participants, List<Round> rounds, User creator, PaymentBy paymentBy) {
        this.groupName = groupName;
        this.totalAmount = totalAmount;
        this.maxMembers = maxMembers;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.monthlyContribution = monthlyContribution;
        this.participants = participants;
        this.rounds = rounds;
        this.creator = creator;
        this.paymentBy = paymentBy;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public int getMaxMembers() { return maxMembers; }
    public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public GroupStatus getStatus() { return status; }
    public void setStatus(GroupStatus status) { this.status = status; }

    public BigDecimal getMonthlyContribution() { return monthlyContribution; }
    public void setMonthlyContribution(BigDecimal monthlyContribution) { this.monthlyContribution = monthlyContribution; }

    public List<Participant> getParticipants() { return participants; }
    public void setParticipants(List<Participant> participants) { this.participants = participants; }

    public List<Round> getRounds() { return rounds; }
    public void setRounds(List<Round> rounds) { this.rounds = rounds; }

    public PaymentPlan getPaymentPlan() { return paymentPlan; }
    public void setPaymentPlan(PaymentPlan paymentPlan) { this.paymentPlan = paymentPlan; }

    public PaymentBy getPaymentBy() { return paymentBy; }
    public void setPaymentBy(PaymentBy paymentBy) { this.paymentBy = paymentBy; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public List<PaymentPlan> getPaymentPlans() { return paymentPlans; }
    public void setPaymentPlans(List<PaymentPlan> paymentPlans) { this.paymentPlans = paymentPlans; }

    public String generateGroupName(LocalDateTime startDate, LocalDateTime endDate, BigDecimal totalAmount) {
        String startYearMonth = startDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String endYearMonth = endDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
        return String.format("Group-%s-%s-%.0f", startYearMonth, endYearMonth, totalAmount);
    }

    public String getName() {
        return "Group " + id;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", totalAmount=" + totalAmount +
                ", maxMembers=" + maxMembers +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", monthlyContribution=" + monthlyContribution +
                ", participants=" + participants +
                ", rounds=" + rounds +
                ", paymentType=" + paymentBy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id) &&
                Objects.equals(groupName, group.groupName) &&
                Objects.equals(totalAmount, group.totalAmount) &&
                Objects.equals(startDate, group.startDate) &&
                Objects.equals(endDate, group.endDate) &&
                maxMembers == group.maxMembers &&
                status == group.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupName, totalAmount, startDate, endDate, maxMembers, status);
    }
}
