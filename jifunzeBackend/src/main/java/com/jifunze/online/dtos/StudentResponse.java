package com.jifunze.online.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
    private String fullName;
    private String phoneNumber;
    private String role = "STUDENT";
    private String status = "ACTIVE";
}
