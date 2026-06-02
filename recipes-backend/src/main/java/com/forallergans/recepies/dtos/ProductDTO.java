package com.forallergans.recepies.dtos;

import lombok.Data;
import java.util.List;
import com.forallergans.recepies.entities.Kashrut;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private Kashrut kashrut; // למשל: DAIRY, MEAT, PARVE
    private List<String> allergens; // למשל: ["גלוטן", "בוטנים"]
}