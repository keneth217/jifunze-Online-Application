package com.jifunze.online.servicesImpl;

import com.jifunze.online.entity.Commission;
import com.jifunze.online.entity.Instructor;
import com.jifunze.online.entity.Payment;
import com.jifunze.online.entity.Resource;
import com.jifunze.online.repos.CommissionRepository;
import com.jifunze.online.repos.InstructorRepository;
import com.jifunze.online.services.CommissionService;
import com.jifunze.online.services.MpesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommissionServiceImpl implements CommissionService {
    
    @Autowired
    private CommissionRepository commissionRepository;
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private MpesaService mpesaService;
    
    @Override
    public Commission calculateCommission(Payment payment, Resource resource) {
        // Get the instructor for this resource
        Instructor instructor = resource.getInstructor();
        if (instructor == null) {
            throw new RuntimeException("Resource has no instructor assigned");
        }
        
        // Calculate commission based on instructor's commission rate
        BigDecimal totalAmount = payment.getAmount();
        BigDecimal commissionRate = instructor.getCommissionRate();
        BigDecimal commissionAmount = totalAmount.multiply(commissionRate);
        
        // Create commission record
        Commission commission = new Commission();
        commission.setInstructor(instructor);
        commission.setPayment(payment);
        commission.setResource(resource);
        commission.setTotalAmount(totalAmount);
        commission.setCommissionAmount(commissionAmount);
        commission.setCommissionRate(commissionRate);
        commission.setStatus(Commission.CommissionStatus.PENDING);
        commission.setCreatedAt(LocalDateTime.now());
        
        return commissionRepository.save(commission);
    }
    
    @Override
    public Commission saveCommission(Commission commission) {
        return commissionRepository.save(commission);
    }
    
    @Override
    public List<Commission> getCommissionsByInstructorId(Long instructorId) {
        return commissionRepository.findByInstructorId(instructorId);
    }
    
    @Override
    public List<Commission> getPendingCommissions() {
        return commissionRepository.findByStatus(Commission.CommissionStatus.PENDING);
    }
    
    @Override
    public Map<String, Object> processCommissionPayout(Long commissionId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Commission> commissionOpt = commissionRepository.findById(commissionId);
            if (commissionOpt.isEmpty()) {
                response.put("message", "Commission not found");
                response.put("status", "error");
                return response;
            }
            
            Commission commission = commissionOpt.get();
            if (commission.getStatus() != Commission.CommissionStatus.PENDING) {
                response.put("message", "Commission is not in pending status");
                response.put("status", "error");
                return response;
            }
            
            // Process M-Pesa payout
            Instructor instructor = commission.getInstructor();
            Map<String, Object> mpesaResponse = mpesaService.processPayout(
                instructor.getMpesaPaybillNumber(),
                instructor.getMpesaAccountNumber(),
                commission.getCommissionAmount(),
                "Commission payout for " + commission.getResource().getTitle()
            );
            
            if ("success".equals(mpesaResponse.get("status"))) {
                // Update commission status
                commission.setStatus(Commission.CommissionStatus.PAID);
                commission.setPaidAt(LocalDateTime.now());
                commission.setMpesaTransactionId((String) mpesaResponse.get("transactionId"));
                commission.setPayoutReference((String) mpesaResponse.get("reference"));
                commissionRepository.save(commission);
                
                // Update instructor earnings
                instructor.setTotalEarnings(instructor.getTotalEarnings().add(commission.getCommissionAmount()));
                instructorRepository.save(instructor);
                
                response.put("message", "Commission payout processed successfully");
                response.put("status", "success");
                response.put("commissionId", commissionId);
                response.put("amount", commission.getCommissionAmount());
            } else {
                response.put("message", "M-Pesa payout failed: " + mpesaResponse.get("message"));
                response.put("status", "error");
            }
            
        } catch (Exception e) {
            response.put("message", "Payout processing failed: " + e.getMessage());
            response.put("status", "error");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> processBulkCommissionPayout(List<Long> commissionIds) {
        Map<String, Object> response = new HashMap<>();
        int successCount = 0;
        int failureCount = 0;
        
        for (Long commissionId : commissionIds) {
            Map<String, Object> payoutResponse = processCommissionPayout(commissionId);
            if ("success".equals(payoutResponse.get("status"))) {
                successCount++;
            } else {
                failureCount++;
            }
        }
        
        response.put("message", "Bulk payout completed. Success: " + successCount + ", Failures: " + failureCount);
        response.put("status", "success");
        response.put("successCount", successCount);
        response.put("failureCount", failureCount);
        
        return response;
    }
    
    @Override
    public BigDecimal getTotalPendingCommissions(Long instructorId) {
        return commissionRepository.sumPendingCommissionsByInstructorId(instructorId);
    }
    
    @Override
    public BigDecimal getTotalPaidCommissions(Long instructorId) {
        return commissionRepository.sumPaidCommissionsByInstructorId(instructorId);
    }
    
    @Override
    public Commission getCommissionById(Long id) {
        return commissionRepository.findById(id).orElse(null);
    }
    
    @Override
    public List<Commission> getAllCommissions() {
        return commissionRepository.findAll();
    }
}