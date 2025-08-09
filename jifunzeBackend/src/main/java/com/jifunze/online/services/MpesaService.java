package com.jifunze.online.services;

import com.jifunze.online.dtos.MpesaSTKPushRequest;
import com.jifunze.online.dtos.MpesaSTKPushResponse;
import com.jifunze.online.dtos.MpesaCallbackRequest;
import com.jifunze.online.entity.Payment;
import com.jifunze.online.entity.Resource;
import com.jifunze.online.entity.User;
import com.jifunze.online.entity.UserResourceAccess;
import com.jifunze.online.repos.PaymentRepository;
import com.jifunze.online.repos.ResourceRepository;
import com.jifunze.online.repos.UserRepository;
import com.jifunze.online.repos.UserResourceAccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MpesaService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
    private UserResourceAccessRepository userResourceAccessRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${safaricom.api.url}")
    private String safaricomApiUrl;
    
    @Value("${safaricom.api.username}")
    private String username;
    
    @Value("${safaricom.api.password}")
    private String password;
    
    @Value("${safaricom.api.lipa_na_mpesa_online_shortcode}")
    private String businessShortCode;
    
    @Value("${safaricom.api.lipa_na_mpesa_online_passkey}")
    private String passkey;
    
    @Value("${safaricom.api.callback_url}")
    private String callbackUrl;
    
    /**
     * Initiate STK push for resource purchase
     */
    public MpesaSTKPushResponse initiateSTKPush(Long userId, Long resourceId, String phoneNumber) {
        try {
            // Validate user and resource
            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
            
            if (userOpt.isEmpty() || resourceOpt.isEmpty()) {
                throw new RuntimeException("User or resource not found");
            }
            
            User user = userOpt.get();
            Resource resource = resourceOpt.get();
            
            // Validate phone number format
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                throw new RuntimeException("Phone number is required");
            }
            
            // Format phone number to Kenyan format if needed
            if (!phoneNumber.startsWith("254")) {
                if (phoneNumber.startsWith("0")) {
                    phoneNumber = "254" + phoneNumber.substring(1);
                } else if (phoneNumber.startsWith("+254")) {
                    phoneNumber = phoneNumber.substring(1);
                }
            }
            
            // Check if user already has access to this resource
            if (hasResourceAccess(userId, resourceId)) {
                throw new RuntimeException("User already has access to this resource");
            }
            
            // Create payment record
            Payment payment = new Payment();
            payment.setUser(user);
            payment.setResource(resource);
            payment.setAmount(resource.getPrice());
            payment.setPhoneNumber(phoneNumber);
            payment.setType(Payment.PaymentType.RESOURCE_PURCHASE);
            payment.setStatus(Payment.PaymentStatus.PENDING);
            payment.setCreatedAt(LocalDateTime.now());
            payment.setMpesaTransactionId("TXN_" + System.currentTimeMillis());
            
            payment = paymentRepository.save(payment);
            
            // Prepare STK push request
            MpesaSTKPushRequest request = new MpesaSTKPushRequest();
            request.setBusinessShortCode(businessShortCode);
            request.setPassword(generatePassword());
            request.setTimestamp(generateTimestamp());
            request.setTransactionType("CustomerPayBillOnline");
            request.setAmount(resource.getPrice().toString());
            request.setPartyA(phoneNumber);
            request.setPartyB(businessShortCode);
            request.setPhoneNumber(phoneNumber);
            request.setCallBackURL(callbackUrl);
            request.setAccountReference("Jifunze_" + resource.getId());
            request.setTransactionDesc("Payment for " + resource.getTitle());
            
            // Make API call to Safaricom
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBasicAuth(username, password);
            
            HttpEntity<MpesaSTKPushRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<MpesaSTKPushResponse> response = restTemplate.postForEntity(
                safaricomApiUrl + "/mpesa/stkpush/v1/processrequest",
                entity,
                MpesaSTKPushResponse.class
            );
            
            MpesaSTKPushResponse stkResponse = response.getBody();
            
            if (stkResponse != null && "0".equals(stkResponse.getResponseCode())) {
                // Update payment with checkout request ID
                payment.setCheckoutRequestId(stkResponse.getCheckoutRequestID());
                payment.setMerchantRequestId(stkResponse.getMerchantRequestID());
                paymentRepository.save(payment);
            } else {
                // If STK push failed, update payment status
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason(stkResponse != null ? stkResponse.getResponseDescription() : "STK push failed");
                paymentRepository.save(payment);
            }
            
            return stkResponse;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initiate STK push: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get callback URL for M-Pesa
     */
    public String getCallbackUrl() {
        return callbackUrl;
    }
    
    /**
     * Query callback data and process payment
     */
    public Payment queryCallbackData(String checkoutRequestId) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findByCheckoutRequestId(checkoutRequestId);
            
            if (paymentOpt.isEmpty()) {
                throw new RuntimeException("Payment not found for checkout request ID: " + checkoutRequestId);
            }
            
            Payment payment = paymentOpt.get();
            
            // Query Safaricom API for transaction status
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBasicAuth(username, password);
            
            Map<String, String> queryRequest = new HashMap<>();
            queryRequest.put("BusinessShortCode", businessShortCode);
            queryRequest.put("Password", generatePassword());
            queryRequest.put("Timestamp", generateTimestamp());
            queryRequest.put("CheckoutRequestID", checkoutRequestId);
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(queryRequest, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                safaricomApiUrl + "/mpesa/stkpushquery/v1/query",
                entity,
                Map.class
            );
            
            Map<String, Object> queryResponse = response.getBody();
            
            if (queryResponse != null) {
                String resultCode = (String) queryResponse.get("ResultCode");
                String resultDesc = (String) queryResponse.get("ResultDesc");
                
                return updatePaymentStatus(payment, resultCode, resultDesc);
            }
            
            return payment;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to query callback data: " + e.getMessage(), e);
        }
    }
    
    /**
     * Process callback from Safaricom
     */
    public Payment processCallback(MpesaCallbackRequest callbackRequest) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findByCheckoutRequestId(callbackRequest.getCheckoutRequestID());
            
            if (paymentOpt.isEmpty()) {
                throw new RuntimeException("Payment not found for checkout request ID: " + callbackRequest.getCheckoutRequestID());
            }
            
            Payment payment = paymentOpt.get();
            
            return updatePaymentStatus(payment, callbackRequest.getResultCode(), callbackRequest.getResultDesc());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to process callback: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update payment status and unlock resource if payment is successful
     */
    private Payment updatePaymentStatus(Payment payment, String resultCode, String resultDesc) {
        payment.setResultCode(resultCode);
        payment.setResultDesc(resultDesc);
        
        if ("0".equals(resultCode)) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setCompletedAt(LocalDateTime.now());
            
            // Unlock resource access
            if (payment.getType() == Payment.PaymentType.RESOURCE_PURCHASE && payment.getResource() != null) {
                unlockResourceAccess(payment.getUser().getId(), payment.getResource().getId(), payment);
            }
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason(resultDesc);
        }
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Unlock resource access for successful payment
     */
    private UserResourceAccess unlockResourceAccess(Long userId, Long resourceId, Payment payment) {
        // Check if access already exists
        Optional<UserResourceAccess> existingAccess = userResourceAccessRepository
            .findByUserIdAndResourceId(userId, resourceId);
        
        if (existingAccess.isPresent()) {
            // Update existing access
            UserResourceAccess access = existingAccess.get();
            access.setPayment(payment);
            access.setLastAccessedAt(LocalDateTime.now());
            return userResourceAccessRepository.save(access);
        } else {
            // Create new access
            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
            
            if (userOpt.isPresent() && resourceOpt.isPresent()) {
                UserResourceAccess access = new UserResourceAccess();
                access.setUser(userOpt.get());
                access.setResource(resourceOpt.get());
                access.setPayment(payment);
                access.setAccessType(UserResourceAccess.AccessType.PURCHASED);
                access.setGrantedAt(LocalDateTime.now());
                access.setLastAccessedAt(LocalDateTime.now());
                
                return userResourceAccessRepository.save(access);
            }
        }
        
        return null;
    }
    
    /**
     * Check if user has access to resource
     */
    public boolean hasResourceAccess(Long userId, Long resourceId) {
        return userResourceAccessRepository.existsByUserIdAndResourceId(userId, resourceId);
    }
    
    /**
     * Generate password for Safaricom API
     */
    private String generatePassword() {
        String timestamp = generateTimestamp();
        String passwordString = businessShortCode + passkey + timestamp;
        return Base64.getEncoder().encodeToString(passwordString.getBytes());
    }
    
    /**
     * Generate timestamp in required format
     */
    private String generateTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
