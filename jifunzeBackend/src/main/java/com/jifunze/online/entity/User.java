package com.jifunze.online.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String fullName;
    
    @Column(unique = true)
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.STUDENT;
    
    @Column(nullable = false)
    private boolean isActive = true;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime lastLoginAt;
    
    @Column
    private String refreshToken;
    
    @Column
    private LocalDateTime refreshTokenExpiry;
    
    // Payment and subscription fields
    @Column
    private String mpesaCustomerId;
    
    @Column
    private boolean hasActiveSubscription = false;
    
    @Column
    private LocalDateTime subscriptionExpiry;
    
    @Column
    private String subscriptionPlan;
    
    // Instructor-specific fields
    @Column
    private String mpesaPaybillNumber;
    
    @Column
    private String mpesaAccountNumber;
    
    @Column
    private BigDecimal totalEarnings = BigDecimal.ZERO;
    
    @Column
    private BigDecimal commissionRate = new BigDecimal("0.70"); // 70% commission by default
    
    @Column
    private boolean isInstructorVerified = false;
    
    @Column
    private String instructorBio;
    
    @Column
    private String instructorSpecialization;
    
    public enum UserRole {
        STUDENT, ADMIN, INSTRUCTOR
    }
} 