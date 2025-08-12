package com.jifunze.online.controllers;

import com.jifunze.online.entity.Commission;
import com.jifunze.online.services.CommissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commissions")
public class CommissionController {

    private final CommissionService commissionService;

    public CommissionController(CommissionService commissionService) {
        this.commissionService = commissionService;
    }

    @GetMapping
    public ResponseEntity<List<Commission>> getAllCommissions() {
        try {
            List<Commission> commissions = commissionService.getAllCommissions();
            return ResponseEntity.ok(commissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Commission> getCommissionById(@PathVariable Long id) {
        try {
            Commission commission = commissionService.getCommissionById(id);
            if (commission != null) {
                return ResponseEntity.ok(commission);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<Commission>> getCommissionsByInstructor(@PathVariable Long instructorId) {
        try {
            List<Commission> commissions = commissionService.getCommissionsByInstructorId(instructorId);
            return ResponseEntity.ok(commissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Commission>> getPendingCommissions() {
        try {
            List<Commission> commissions = commissionService.getPendingCommissions();
            return ResponseEntity.ok(commissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/payout")
    public ResponseEntity<Map<String, Object>> processCommissionPayout(@PathVariable Long id) {
        try {
            Map<String, Object> response = commissionService.processCommissionPayout(id);
            String status = (String) response.get("status");
            if ("success".equals(status)) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                    "message", "Payout processing failed: " + e.getMessage(),
                    "status", "error"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/bulk-payout")
    public ResponseEntity<Map<String, Object>> processBulkCommissionPayout(@RequestBody List<Long> commissionIds) {
        try {
            Map<String, Object> response = commissionService.processBulkCommissionPayout(commissionIds);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                    "message", "Bulk payout processing failed: " + e.getMessage(),
                    "status", "error"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/instructor/{instructorId}/pending-total")
    public ResponseEntity<Map<String, Object>> getPendingCommissionsTotal(@PathVariable Long instructorId) {
        try {
            BigDecimal pendingTotal = commissionService.getTotalPendingCommissions(instructorId);
            Map<String, Object> response = Map.of(
                    "instructorId", instructorId,
                    "pendingTotal", pendingTotal
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/instructor/{instructorId}/paid-total")
    public ResponseEntity<Map<String, Object>> getPaidCommissionsTotal(@PathVariable Long instructorId) {
        try {
            BigDecimal paidTotal = commissionService.getTotalPaidCommissions(instructorId);
            Map<String, Object> response = Map.of(
                    "instructorId", instructorId,
                    "paidTotal", paidTotal
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}