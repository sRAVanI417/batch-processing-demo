package com.example.batch_processing_demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesRecord {

    private Long id;
    private String itemName;
    private Integer quantitySold;
    private BigDecimal price;
    private Integer remainingStock;

    // getters and setters
}