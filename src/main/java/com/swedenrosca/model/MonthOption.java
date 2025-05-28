package com.swedenrosca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "month_option")
public class MonthOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "months_count", nullable = false)
    private int monthsCount;

    // Default constructor for JPA
    public MonthOption() {}

    // Convenience constructor
    public MonthOption(int monthsCount) {
        this.monthsCount = monthsCount;
    }

    public Long getId() {
        return id;
    }

    public void setMonthsCount(int monthsCount) {
        this.monthsCount = monthsCount;
    }

    public int getMonthsCount() {
        return monthsCount;
    }
}

