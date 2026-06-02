package com.forallergans.recepies.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Kashrut kashrut = Kashrut.PARVE;

    @ManyToMany
    @JoinTable(
        name = "product_allergens",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "allergen_id")
    )
    private Set<Allergen> allergens;

}