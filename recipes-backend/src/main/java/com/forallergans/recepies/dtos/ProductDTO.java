package com.forallergans.recepies.dtos;

import lombok.Data;
import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private List<String> allergens; // למשל: ["גלוטן", "בוטנים"]
}