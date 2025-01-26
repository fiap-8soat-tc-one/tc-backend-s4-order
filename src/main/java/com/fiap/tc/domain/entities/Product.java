package com.fiap.tc.domain.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Product {
    private UUID id;
    private UUID idCategory;
    private String name;
    private String description;
    private BigDecimal price;
}
