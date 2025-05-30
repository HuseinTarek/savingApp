package com.swedenrosca.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "saving_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "monthly_contribution")
    private BigDecimal monthlyContribution;

    @Column(name = "max_members")
    private int maxMembers;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private GroupStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_by")
    private PaymentBy paymentBy;

    @ManyToOne
    @JoinColumn(name = "payment_plan_id")
    private PaymentPlan paymentPlan;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Participant> participants = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    // No-arg constructor
    public Group() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getMonthlyContribution() { return monthlyContribution; }
    public void setMonthlyContribution(BigDecimal monthlyContribution) { this.monthlyContribution = monthlyContribution; }

    public int getMaxMembers() { return maxMembers; }
    public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public GroupStatus getStatus() { return status; }
    public void setStatus(GroupStatus status) { this.status = status; }

    public PaymentBy getPaymentBy() { return paymentBy; }
    public void setPaymentBy(PaymentBy paymentBy) { this.paymentBy = paymentBy; }

    public PaymentPlan getPaymentPlan() { return paymentPlan; }
    public void setPaymentPlan(PaymentPlan paymentPlan) { this.paymentPlan = paymentPlan; }

    public List<Participant> getParticipants() { return participants; }
    public void setParticipants(List<Participant> participants) { this.participants = participants; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public String generateGroupName(LocalDateTime startDate, LocalDateTime endDate, BigDecimal totalAmount) {
        return String.format("Group %s-%s (%s SEK)", 
            startDate.toLocalDate(), 
            endDate.toLocalDate(), 
            totalAmount.toPlainString());
    }
}
