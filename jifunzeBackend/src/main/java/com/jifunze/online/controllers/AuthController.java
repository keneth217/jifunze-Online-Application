package com.jifunze.online.controllers;

import com.jifunze.online.config.JwtUtil;
import com.jifunze.online.config.CookieUtil;
import com.jifunze.online.dtos.LoginRequest;
import com.jifunze.online.dtos.LoginResponse;
import com.jifunze.online.dtos.LogoutResponse;
import com.jifunze.online.dtos.RegisterRequest;
import com.jifunze.online.dtos.RegisterResponse;
import com.jifunze.online.dtos.RefreshTokenRequest;
import com.jifunze.online.dtos.RefreshTokenResponse;
import com.jifunze.online.entity.User;
import com.jifunze.online.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CookieUtil cookieUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        var userOpt = userService.loginUserByPhone(loginRequest.getPhoneNumber(), loginRequest.getPassword());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Generate JWT access token and refresh token
            String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getRole().name(), user.getId());
            
            // Create HTTP-only cookies
            ResponseCookie accessCookie = cookieUtil.createJwtCookie(accessToken);
            ResponseCookie refreshCookie = ResponseCookie.from("refresh-token", refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 days
                    .sameSite("Lax")
                    .build();
            
            LoginResponse response = LoginResponse.builder()
                    .message("User login successful")
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole().name())
                    .status("success")
                    .userId(user.getId())
                    .build();
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(response);
        } else {
            LoginResponse response = LoginResponse.builder()
                    .message("Invalid credentials")
                    .status("error")
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Generate email from phone number if not provided
        String email = registerRequest.getEmail();
        if (email == null || email.trim().isEmpty()) {
            email = registerRequest.getPhoneNumber() + "@jifunze.online";
        }
        
        // Check if user already exists by email
        if (userService.existsByEmail(email)) {
            RegisterResponse response = RegisterResponse.builder()
                    .message("User with this email already exists")
                    .status("error")
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
        
        if (registerRequest.getPhoneNumber() != null && userService.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            RegisterResponse response = RegisterResponse.builder()
                    .message("User with this phone number already exists")
                    .status("error")
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
        
        // Create User entity from request
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(email);
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setPassword(registerRequest.getPassword());
        user.setRole(User.UserRole.valueOf(registerRequest.getRole()));
        
        User registeredUser = userService.registerUser(user);
        
        RegisterResponse response = RegisterResponse.builder()
                .message("User registration successful")
                .userId(registeredUser.getId())
                .email(registeredUser.getEmail())
                .fullName(registeredUser.getFullName())
                .status("success")
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            String refreshToken = refreshTokenRequest.getRefreshToken();
            
            if (jwtUtil.validateToken(refreshToken) && "REFRESH".equals(jwtUtil.extractTokenType(refreshToken))) {
                String email = jwtUtil.extractEmail(refreshToken);
                String role = jwtUtil.extractRole(refreshToken);
                Long userId = jwtUtil.extractUserId(refreshToken);
                
                // Generate new access token
                String newAccessToken = jwtUtil.generateToken(email, role, userId);
                
                RefreshTokenResponse response = RefreshTokenResponse.builder()
                        .message("Token refreshed successfully")
                        .status("success")
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .expiresIn(86400000L) // 24 hours
                        .build();
                
                // Set new access token cookie
                ResponseCookie accessCookie = cookieUtil.createJwtCookie(newAccessToken);
                
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                        .body(response);
            } else {
                RefreshTokenResponse response = RefreshTokenResponse.builder()
                        .message("Invalid refresh token")
                        .status("error")
                        .build();
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            RefreshTokenResponse response = RefreshTokenResponse.builder()
                    .message("Token refresh failed: " + e.getMessage())
                    .status("error")
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout() {
        // Create empty cookies to clear both JWT tokens
        ResponseCookie accessCookie = cookieUtil.clearJwtCookie();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh-token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // Expire immediately
                .sameSite("Lax")
                .build();
        
        LogoutResponse response = LogoutResponse.builder()
                .message("Logout successful")
                .status("success")
                .build();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }
}