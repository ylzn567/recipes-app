package com.forallergans.recepies.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.forallergans.recepies.entities.Difficulty;
import com.forallergans.recepies.entities.Kashrut;
import com.forallergans.recepies.entities.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    List<Recipe> findByKashrut(Kashrut kashrut);
    
    List<Recipe> findByDifficulty(Difficulty difficulty);
    

    @Query("SELECT r FROM Recipe r WHERE r.id NOT IN (" +
           "SELECT r2.id FROM Recipe r2 " +
           "JOIN r2.ingredients ri " +
           "JOIN ri.product p " +
           "JOIN p.allergens a " +
           "WHERE a.name = :allergenName)")
    List<Recipe> findRecipesWithoutAllergen(@Param("allergenName") String allergenName);
}