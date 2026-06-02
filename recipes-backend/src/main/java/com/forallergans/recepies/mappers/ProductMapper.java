package com.forallergans.recepies.mappers;

import java.util.stream.Collectors;

import com.forallergans.recepies.dtos.ProductDTO;
import com.forallergans.recepies.entities.Allergen;
import com.forallergans.recepies.entities.Product;

public class ProductMapper {

    public static ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setKashrut(product.getKashrut());
        
        if (product.getAllergens() != null) {
            dto.setAllergens(product.getAllergens().stream()
                    .map(Allergen::getName)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}