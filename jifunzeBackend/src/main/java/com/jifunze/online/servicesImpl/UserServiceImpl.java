package com.jifunze.online.servicesImpl;

import com.jifunze.online.entity.User;
import com.jifunze.online.repos.UserRepository;
import com.jifunze.online.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public User registerUser(User user) {
        // Set default values
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        user.setHasActiveSubscription(false);
        return userRepository.save(user);
    }
    
    @Override
    public Optional<User> loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
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