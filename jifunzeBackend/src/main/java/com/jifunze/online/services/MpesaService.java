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
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
    
    // In-memory storage for callback responses (for polling)
    private final Map<String, Map<String, Object>> callbackResponses = new ConcurrentHashMap<>();
    
    @Value("${safaricom.api.url}")
    private String apiUrl;
    
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
     * generate auth token
     *
     * this is a high order  method that we will use in subsequent methods
     */

    public String generateToken() throws IOException {
        OkHttpClient client = new OkHttpClient();
        String tokenUrl = apiUrl + "/oauth/v1/generate?grant_type=client_credentials";
        String credentials = Credentials.basic(username, password);

        System.out.println("Generating token from URL: " + tokenUrl);
        System.out.println("Using username: " + username);
        System.out.println("Using password: " + password);

        Request request = new Request.Builder()
                .url(tokenUrl)
                .get()
                .addHeader("Authorization", credentials)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Token generation response status: " + response.code());

            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                System.out.println("Token generation response: " + responseBody);
                JSONObject jsonObject = new JSONObject(responseBody);
                String token = jsonObject.getString("access_token");
                System.out.println("Generated token: " + token);
                return token;
            } else {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                System.err.println("Token generation failed. Status: " + response.code() + ", Error: " + errorBody);
                throw new RuntimeException("Failed to retrieve access token: " + response.message() + ", Status: " + response.code());
            }
        }
    }


    
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
                apiUrl + "/mpesa/stkpush/v1/processrequest",
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
    @Transactional
    public boolean processCallback(String callbackResponse) {
        try {
            System.out.println("Callback Response: " + callbackResponse);
            JSONObject jsonResponse = new JSONObject(callbackResponse);

            if (!jsonResponse.has("Body") || !jsonResponse.getJSONObject("Body").has("stkCallback")) {
                System.out.println("Invalid callback data received.");
                return false;
            }

            JSONObject stkCallback = jsonResponse.getJSONObject("Body").getJSONObject("stkCallback");
            String resultCode = stkCallback.get("ResultCode").toString();
            String resultDesc = stkCallback.getString("ResultDesc");
            String checkoutRequestId = stkCallback.getString("CheckoutRequestID");

            if (checkoutRequestId == null || checkoutRequestId.isEmpty()) {
                System.out.println("CheckoutRequestID is null or empty, skipping database update.");
                return false;
            }

            // Find the payment
            List<Payment> payments = paymentRepository.findByCheckoutRequestIdOrderByCreatedAtDesc(checkoutRequestId);
            if (payments.isEmpty()) {
                System.out.println("No payment found for checkout ID: " + checkoutRequestId);
                return false;
            }

            Payment payment = payments.getFirst();

            // Comprehensive validation of callback data against original payment
            System.out.println("=== Starting comprehensive callback validation ===");
            System.out.println("Original payment - Amount: " + payment.getAmount() + ", Phone: " + payment.getPhoneNumber());
            System.out.println("Callback - ResultCode: " + resultCode + ", ResultDesc: " + resultDesc);

            Map<String, Object> validationResult = validateCallbackDataComprehensive(payment, stkCallback, resultCode, resultDesc);

            boolean isValid = (Boolean) validationResult.get("isValid");
            String validationMessage = (String) validationResult.get("message");

            System.out.println("Validation result: " + (isValid ? "PASSED" : "FAILED"));
            System.out.println("Validation message: " + validationMessage);
            System.out.println("=== End callback validation ===");

            if (isValid) {
                // Only save to database if result code is 0 (success)
                if ("0".equals(resultCode)) {
                    // Update payment with validated data
                    payment.setResultCode(resultCode);
                    payment.setResultDesc(resultDesc);
                    payment.setStatus(Payment.PaymentStatus.SUCCESS);
                    payment.setCompletedAt(LocalDateTime.now());

                    // Extract M-Pesa receipt number
                    if (stkCallback.has("CallbackMetadata")) {
                        extractMpesaReceiptNumber(stkCallback, payment);
                    }

                    paymentRepository.save(payment);
                    
                    // Unlock resource access for successful payment
                    unlockResourceAccess(payment.getUser().getId(), payment.getResource().getId(), payment);
                    System.out.println("Payment validated and saved successfully: " + payment.getId());
                    System.out.println("Validation message: " + validationMessage);
                } else {
                    // Valid callback but not successful - update payment status
                    String failureReason = getFailureReason(resultCode);
                    payment.setStatus(Payment.PaymentStatus.FAILED);
                    payment.setResultCode(resultCode);
                    payment.setResultDesc(resultDesc);
                    payment.setFailureReason(failureReason);
                    paymentRepository.save(payment);
                    
                    System.out.println("Callback validation passed but payment failed with result code: " + resultCode + " - " + failureReason);
                    System.out.println("Validation message: " + validationMessage);

                }
            } else {
                // Validation failed - don't save to database
                System.out.println("Callback validation failed for checkout ID: " + checkoutRequestId);
                System.out.println("Validation message: " + validationMessage);
            }

            // Store callback response for polling (for all payment results)
            Map<String, Object> callbackStatus = new HashMap<>();
            callbackStatus.put("resultCode", resultCode);
            callbackStatus.put("resultDescription", resultDesc);
            callbackStatus.put("status", "0".equals(resultCode) ? "SUCCESS" : "FAILED");
            callbackStatus.put("mpesaReceiptNumber", payment.getMpesaTransactionId());
            callbackStatus.put("timestamp", LocalDateTime.now());
            callbackResponses.put(checkoutRequestId, callbackStatus);

            System.out.println("=== Stored callback response for polling ===");
            System.out.println("CheckoutRequestID: " + checkoutRequestId);
            System.out.println("ResultCode: " + resultCode);
            System.out.println("Status: " + ("0".equals(resultCode) ? "SUCCESS" : "FAILED"));
            System.out.println("Callback response stored: " + callbackStatus);

            return isValid; // Return true if validation passed, regardless of success/failure

        } catch (Exception e) {
            System.err.println("Failed to process callback: " + e.getMessage());
            return false;
        }
    }


    // Method to get callback status (from memory or database)
    public Map<String, Object> getCallbackStatus(String checkoutRequestId) {
        // First check in-memory storage
        Map<String, Object> callbackStatus = callbackResponses.get(checkoutRequestId);
        if (callbackStatus != null) {
            return callbackStatus;
        }
        
        // If not in memory, check database
        List<Payment> payments = paymentRepository.findByCheckoutRequestIdOrderByCreatedAtDesc(checkoutRequestId);
        if (!payments.isEmpty()) {
            Payment payment = payments.get(0);
            Map<String, Object> status = new HashMap<>();
            status.put("resultCode", payment.getResultCode());
            status.put("resultDescription", payment.getResultDesc());
            status.put("status", payment.getStatus().toString());
            status.put("mpesaReceiptNumber", payment.getMpesaTransactionId());
            status.put("timestamp", payment.getCompletedAt());
            return status;
        }
        
        // Return null if not found
        return null;
    }



    /**
     * Store callback response for testing polling
     */
    public void storeCallbackResponse(String checkoutRequestId, Map<String, Object> callbackStatus) {
        callbackResponses.put(checkoutRequestId, callbackStatus);
    }

    /**
     * Extract M-Pesa receipt number from callback metadata
     */
    private void extractMpesaReceiptNumber(JSONObject stkCallback, Payment payment) {
        try {
            JSONObject metadata = stkCallback.getJSONObject("CallbackMetadata");
            if (metadata.has("Item")) {
                for (Object item : metadata.getJSONArray("Item")) {
                    JSONObject itemObj = (JSONObject) item;
                    String name = itemObj.getString("Name");
                    Object value = itemObj.get("Value");

                    if ("MpesaReceiptNumber".equals(name)) {
                        payment.setMpesaTransactionId(value.toString());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to extract M-Pesa receipt number: " + e.getMessage());
        }
    }

    /**
     * Comprehensive validation of callback data against original payment
     */
    private Map<String, Object> validateCallbackDataComprehensive(Payment originalPayment, JSONObject stkCallback, String resultCode, String resultDesc) {
        Map<String, Object> validationResult = new HashMap<>();
        StringBuilder validationMessage = new StringBuilder();
        boolean isValid = true;

        // Extract data from callback
        String callbackPhoneNumber = null;
        Double callbackAmount = null;
        String mpesaReceiptNumber = null;

        if (stkCallback.has("CallbackMetadata")) {
            JSONObject metadata = stkCallback.getJSONObject("CallbackMetadata");
            if (metadata.has("Item")) {
                for (Object item : metadata.getJSONArray("Item")) {
                    JSONObject itemObj = (JSONObject) item;
                    String name = itemObj.getString("Name");
                    Object value = itemObj.get("Value");

                    switch (name) {
                        case "PhoneNumber":
                            callbackPhoneNumber = value.toString();
                            break;
                        case "Amount":
                            callbackAmount = Double.parseDouble(value.toString());
                            break;
                        case "MpesaReceiptNumber":
                            mpesaReceiptNumber = value.toString();
                            break;
                    }
                }
            }
        }

        // 1. Validate CheckoutRequestID
        String callbackCheckoutId = stkCallback.getString("CheckoutRequestID");
        String originalCheckoutId = originalPayment.getCheckoutRequestId();

        if (originalCheckoutId == null || originalCheckoutId.isEmpty()) {
            validationMessage.append("Original payment has no checkout ID; ");
            isValid = false;
        } else if (!originalCheckoutId.equals(callbackCheckoutId)) {
            validationMessage.append("CheckoutRequestID mismatch. Expected: ").append(originalCheckoutId)
                    .append(", Received: ").append(callbackCheckoutId).append("; ");
            isValid = false;
        }

        // 2. Validate Phone Number (if available in callback)
        if (callbackPhoneNumber != null) {
            String originalPhone = originalPayment.getPhoneNumber();
            if (originalPhone == null || originalPhone.isEmpty()) {
                validationMessage.append("Original payment has no phone number; ");
                isValid = false;
            } else {
                // Format callback phone number to match original format
                String formattedCallbackPhone = formatPhoneNumber(callbackPhoneNumber);
                String formattedOriginalPhone = formatPhoneNumber(originalPhone);

                if (!formattedOriginalPhone.equals(formattedCallbackPhone)) {
                    validationMessage.append("Phone number mismatch. Expected: ").append(formattedOriginalPhone)
                            .append(", Received: ").append(formattedCallbackPhone).append("; ");
                    isValid = false;
                }
            }
        }

        // 3. Validate Amount (if available in callback)
        if (callbackAmount != null) {
            BigDecimal originalAmount = originalPayment.getAmount();
            if (originalAmount == null) {
                validationMessage.append("Original payment has no amount; ");
                isValid = false;
            } else {
                // Allow small tolerance for rounding differences (0.01)
                double difference = Math.abs(originalAmount.doubleValue() - callbackAmount);
                if (difference > 0.01) {
                    validationMessage.append("Amount mismatch. Expected: ").append(originalAmount)
                            .append(", Received: ").append(callbackAmount).append("; ");
                    isValid = false;
                }
            }
        }

        // 4. Validate Result Code and handle different scenarios
        switch (resultCode) {
            case "0": // Success
                // Success case - additional validations
                if (callbackPhoneNumber == null) {
                    validationMessage.append("Phone number missing in successful callback; ");
                    isValid = false;
                }
                if (callbackAmount == null) {
                    validationMessage.append("Amount missing in successful callback; ");
                    isValid = false;
                }
                if (mpesaReceiptNumber == null) {
                    validationMessage.append("M-Pesa receipt number missing in successful callback; ");
                    isValid = false;
                }
                break;
            case "1": // Insufficient Funds
                validationMessage.append("Payment failed: Insufficient Funds; ");
                break;
            case "1032": // Request Cancelled by User
                validationMessage.append("Payment failed: Request Cancelled by User; ");
                break;
            case "1037": // Timeout in Processing
                validationMessage.append("Payment failed: Timeout in Processing; ");
                break;
            case "2001": // Wrong PIN / Authentication Error
                validationMessage.append("Payment failed: Wrong PIN / Authentication Error; ");
                break;
            case "4002": // Missing Parameters
                validationMessage.append("Payment failed: Missing Parameters; ");
                break;
            case "5000": // General Error
                validationMessage.append("Payment failed: General Error; ");
                break;
            default:
                validationMessage.append("Payment failed: Unknown result code ").append(resultCode).append("; ");
                break;
        }

        // 5. Validate that this is not a duplicate callback
        if (originalPayment.getResultCode() != null && !originalPayment.getResultCode().isEmpty()) {
            validationMessage.append("Duplicate callback detected. Payment already processed; ");
            isValid = false;
        }

        // 6. Validate callback timestamp (should be recent)
        // This is a basic check - you might want to add more sophisticated timestamp validation

        // Set validation result
        if (isValid) {
            validationMessage.append("All validations passed successfully");
        }

        validationResult.put("isValid", isValid);
        validationResult.put("message", validationMessage.toString());
        validationResult.put("callbackPhoneNumber", callbackPhoneNumber);
        validationResult.put("callbackAmount", callbackAmount);
        validationResult.put("mpesaReceiptNumber", mpesaReceiptNumber);
        validationResult.put("resultCode", resultCode);

        return validationResult;
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

    private String formatPhoneNumber(String phoneNumber) {
        // Remove any spaces or special characters
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");

        // If it starts with 0, replace with 254
        if (cleaned.startsWith("0")) {
            return "254" + cleaned.substring(1);
        }

        // If it already starts with 254, return as is
        if (cleaned.startsWith("254")) {
            return cleaned;
        }

        // If it's 9 digits, add 254 prefix
        if (cleaned.length() == 9) {
            return "254" + cleaned;
        }

        // Return as is if it doesn't match any pattern
        return cleaned;
    }

    private String getErrorMessageByCode(String resultCode) {
        switch (resultCode) {
            case "0":
                return "Success - The STK Push was successful.";
            case "1":
                return "Insufficient Funds - Customer did not have enough money in their M-Pesa account.";
            case "1032":
                return "Request Cancelled by User - The user dismissed or cancelled the STK prompt on their phone.";
            case "1037":
                return "Timeout - User did not respond to the STK prompt within the allowed time (~60 secs).";
            case "2001":
                return "Transaction Failed - Generic failure. Can happen for various reasons (e.g. system errors).";
            default:
                return "Unknown error occurred. Result Code: " + resultCode;
        }
    }

    /**
     * Get human-readable failure reason for result codes
     */
    private String getFailureReason(String resultCode) {
        switch (resultCode) {
            case "1":
                return "Insufficient Funds - Customer has no funds in their M-Pesa account";
            case "1032":
                return "Request Cancelled by User - User dismissed/cancelled the STK prompt";
            case "1037":
                return "Timeout in Processing - M-Pesa didn't respond in time, may still be processed later";
            case "2001":
                return "Wrong PIN / Authentication Error - User entered wrong PIN or failed verification";
            case "4002":
                return "Missing Parameters - Safaricom backend failed due to missing request details";
            case "5000":
                return "General Error - Catch-all for other processing errors";
            default:
                return "Unknown Error - Unrecognized result code: " + resultCode;
        }
    }
    
    /**
     * Process commission payout to instructor via M-Pesa
     */
    public Map<String, Object> processPayout(String paybillNumber, String accountNumber, BigDecimal amount, String description) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Generate unique transaction ID
            String transactionId = "PAYOUT_" + System.currentTimeMillis();
            
            // For now, simulate successful payout
            // In a real implementation, you would call the M-Pesa Business to Customer API
            // This is a placeholder for the actual M-Pesa B2C API integration
            
            response.put("status", "success");
            response.put("transactionId", transactionId);
            response.put("reference", "REF_" + System.currentTimeMillis());
            response.put("message", "Payout processed successfully");
            response.put("amount", amount);
            
            // TODO: Implement actual M-Pesa B2C API call here
            // The actual implementation would involve:
            // 1. Calling the M-Pesa B2C API endpoint
            // 2. Passing the instructor's phone number, amount, and description
            // 3. Handling the response and updating the commission status
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Payout processing failed: " + e.getMessage());
        }
        
        return response;
    }
}
