package com.jifunze.online.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CommissionPayoutRequest {
    private List<Long> commissionIds;
    private String payoutMethod; // MPESA, BANK_TRANSFER, etc.
    private String notes;
}