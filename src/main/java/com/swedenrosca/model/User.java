package com.swedenrosca.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "personal_number", nullable = false, unique = true)
    private String personalNumber;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "mobile_number", nullable = false, unique = true)
    private String mobileNumber;

    @Column(name= "bank_account",nullable=true,unique = true)
    private String bankAccount;

    @Column(name= "clearing_number",nullable = true)
    private String clearingNumber;

    @Column(name = "monthly_contribution")
    private BigDecimal monthlyContribution;

    @Column(name = "number_of_members")
    private int numberOfMembers;

    @Column(name="Current Balance")
    private BigDecimal currentBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // ADMIN, USER, CUSTOMER_SERVICE, COMPANY

    @OneToMany(mappedBy="user")
    private List<Participant> participants;

    // Default constructor
    public User() {
    }


    public User(String username, String password, String email, String personalNumber, String firstName, String lastName, String mobileNumber, String bankAccount, String clearingNumber, BigDecimal monthlyContribution, int numberOfMembers, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.personalNumber = personalNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.bankAccount = bankAccount;
        this.clearingNumber = clearingNumber;
        this.monthlyContribution = monthlyContribution;
        this.numberOfMembers = numberOfMembers;
        this.role = role;
    }


    public BigDecimal getMonthlyContribution() {
        return monthlyContribution;
    }

    public void setMonthlyContribution(BigDecimal monthlyChoice) {
        this.monthlyContribution = monthlyChoice;
    }

    public int getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(int monthsChoice) {
        this.numberOfMembers = monthsChoice;
    }

    public User(String username, String password, String email,
                String personalNumber, String firstName, String lastName,
                String mobileNumber, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.personalNumber = personalNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.role = role;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getClearingNumber() {
        return clearingNumber;
    }

    public void setClearingNumber(String clearingNumber) {
        this.clearingNumber = clearingNumber;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participations) {
        this.participants = participations;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public Role getRole() {
        return role;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setRole(Role role) {
        this.role = role;
    }



    // toString method


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", personalNumber='" + personalNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", clearingNumber='" + clearingNumber + '\'' +
                ", role=" + role +
                ", participations=" + participants +
                '}';
    }

    // equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                Objects.equals(personalNumber, user.personalNumber) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(mobileNumber, user.mobileNumber) &&
                role == user.role&&
                Objects.equals(participants, user.participants)&&
                Objects.equals(bankAccount, user.bankAccount)&&
                Objects.equals(clearingNumber, user.clearingNumber);
    }

    // hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, personalNumber, firstName, lastName, mobileNumber, role,bankAccount,clearingNumber, participants);
    }
} 