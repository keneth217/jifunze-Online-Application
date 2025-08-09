package com.jifunze.online.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_resource_access")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResourceAccess {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessType accessType;
    
    @Column(nullable = false)
    private LocalDateTime grantedAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime lastAccessedAt;
    
    public enum AccessType {
        PURCHASED, SUBSCRIPTION, FREE
    }
}
