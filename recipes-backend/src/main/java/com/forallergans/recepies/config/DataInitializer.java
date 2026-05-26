package com.forallergans.recepies.config;

import com.forallergans.recepies.entities.*;
import com.forallergans.recepies.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AllergenRepository allergenRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository; // הזרקת רפוזיטורי המתכונים
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(AllergenRepository allergenRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            RecipeRepository recipeRepository,
            BCryptPasswordEncoder passwordEncoder) { // מעדכנים את הבנאי
        this.allergenRepository = allergenRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {
            System.out.println("--- מתחיל אתחול נתונים אוטומטי לטבלאות ---");

            // 1. יצירת משתמש מנהל עם סיסמה מוצפנת!
            User admin = new User();
            admin.setUsername("admin");
            // משתמשים ב-passwordEncoder.encode בשביל להצפין את הסיסמה:
            admin.setPassword(passwordEncoder.encode("admin123")); 
            admin.setEmail("admin@recipes.com");
            admin.setRole(Role.ADMIN); 
            userRepository.save(admin); 

            // 2. יצירת משתמש רגיל עם סיסמה מוצפנת!
            User user = new User();
            user.setUsername("user");
            // מצפינים גם פה:
            user.setPassword(passwordEncoder.encode("user123")); 
            user.setEmail("user@recipes.com");
            user.setRole(Role.USER);
            userRepository.save(user);

            // 2. יצירת אלרגנים
            Allergen gluten = new Allergen();
            gluten.setName("גלוטן");
            allergenRepository.save(gluten);

            Allergen milk = new Allergen();
            milk.setName("חלב");
            allergenRepository.save(milk);

            // 3. יצירת מוצרים
            Product flour = new Product();
            flour.setName("קמח חיטה");
            flour.setAllergens(Set.of(gluten));
            productRepository.save(flour);

            Product butter = new Product();
            butter.setName("חמאה");
            butter.setAllergens(Set.of(milk));
            productRepository.save(butter);

            // 4. שורות חדשות: יצירת מתכון ראשון מחובר לנתונים!
            Recipe cake = new Recipe();
            cake.setTitle("עוגת שוקולד חגיגית");
            cake.setInstructions("מערבבים את החומרים, מוסיפים את החמאה ואופים 40 דקות.");
            cake.setPreparationTimeMinutes(40);
            cake.setDifficulty(Difficulty.EASY);
            cake.setKashrut(Kashrut.DAIRY); // מותאם ל-Enum שלכן (DAIRY)
            cake.setCreatedBy(admin); // מקשרים את המתכון למשתמש ה-admin שיצרנו למעלה

            // יצירת הרכיבים הספציפיים למתכון הזה (החיבור בין המתכון למוצר)
            RecipeIngredient ing1 = new RecipeIngredient();
            ing1.setRecipe(cake);
            ing1.setProduct(flour);
            ing1.setQuantity(2.0);
            ing1.setMeasurementUnit(MeasurementUnit.CUP); // מותאם ל-Enum שלכן (CUP)

            RecipeIngredient ing2 = new RecipeIngredient();
            ing2.setRecipe(cake);
            ing2.setProduct(butter);
            ing2.setQuantity(150.0);
            ing2.setMeasurementUnit(MeasurementUnit.GRAM); // מותאם ל-Enum שלכן (GRAM)

            // מוסיפים את הרכיבים לתוך המתכון
            cake.setIngredients(List.of(ing1, ing2));

            // שומרים את המתכון בבסיס הנתונים
            recipeRepository.save(cake);

            System.out.println("--- אתחול הנתונים הסתיים בהצלחה! יש מתכון בטבלה ---");
        }
    }
}