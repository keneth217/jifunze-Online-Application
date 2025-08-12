package com.jifunze.online.services;

import com.jifunze.online.entity.Commission;
import com.jifunze.online.entity.Payment;
import com.jifunze.online.entity.Resource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CommissionService {
    
    Commission calculateCommission(Payment payment, Resource resource);
    
    Commission saveCommission(Commission commission);
    
    List<Commission> getCommissionsByInstructorId(Long instructorId);
    
    List<Commission> getPendingCommissions();
    
    Map<String, Object> processCommissionPayout(Long commissionId);
    
    Map<String, Object> processBulkCommissionPayout(List<Long> commissionIds);
    
    BigDecimal getTotalPendingCommissions(Long instructorId);
    
    BigDecimal getTotalPaidCommissions(Long instructorId);
    
    Commission getCommissionById(Long id);
    
    List<Commission> getAllCommissions();
}