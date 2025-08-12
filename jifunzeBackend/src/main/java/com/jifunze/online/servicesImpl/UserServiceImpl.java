package com.jifunze.online.servicesImpl;

import com.jifunze.online.entity.User;
import com.jifunze.online.repos.UserRepository;
import com.jifunze.online.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User registerUser(User user) {
        // Set default values
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        user.setHasActiveSubscription(false);
        
        // Set default password as phone number if password is not provided
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword(user.getPhoneNumber());
        }
        
        // Set commission rate based on role
        if (user.getRole() == User.UserRole.STUDENT) {
            user.setCommissionRate(new java.math.BigDecimal("0.00")); // Students have no commission
        } else if (user.getRole() == User.UserRole.INSTRUCTOR) {
            user.setCommissionRate(new java.math.BigDecimal("0.70")); // Instructors get 70% commission
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }
    
    @Override
    public Optional<User> loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<User> loginUserByPhone(String phoneNumber, String password) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    
    @Override
    public User updateRefreshToken(Long userId, String refreshToken, LocalDateTime expiry) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRefreshToken(refreshToken);
            user.setRefreshTokenExpiry(expiry);
            return userRepository.save(user);
        }
        return null;
    }
    
    @Override
    public void clearRefreshToken(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userRepository.save(user);
        }
    }
    
    @Override
    public boolean hasActiveSubscription(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(User::isHasActiveSubscription).orElse(false);
    }
    
    @Override
    public void updateSubscriptionStatus(Long userId, boolean hasActiveSubscription, LocalDateTime expiry, String plan) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setHasActiveSubscription(hasActiveSubscription);
            user.setSubscriptionExpiry(expiry);
            user.setSubscriptionPlan(plan);
            userRepository.save(user);
        }
    }
} 