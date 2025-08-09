package com.jifunze.online.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceUploadRequest {
    
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private Long instructorId;
    private String thumbnailUrl;
    private boolean isPremium = false;
} 