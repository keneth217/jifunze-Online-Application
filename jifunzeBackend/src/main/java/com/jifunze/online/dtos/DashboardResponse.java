package com.jifunze.online.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private String message;
    private String status;
    private DashboardData data;
}