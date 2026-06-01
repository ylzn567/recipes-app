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
            admin.setPassword(passwordEncoder.encode("Admin123!")); 
            admin.setEmail("admin@recipes.com");
            admin.setRole(Role.ADMIN); 
            userRepository.save(admin); 

            // 2. יצירת משתמש רגיל עם סיסמה מוצפנת!
            User user = new User();
            user.setUsername("user");
            // מצפינים גם פה:
            user.setPassword(passwordEncoder.encode("User123!")); 
            user.setEmail("user@recipes.com");
            user.setRole(Role.USER);
            userRepository.save(user);

            // 3. יצירת אלרגנים נפוצים
            Allergen gluten = allergenRepository.save(createAllergen("גלוטן"));
            Allergen lactose = allergenRepository.save(createAllergen("לקטוז"));
            Allergen peanuts = allergenRepository.save(createAllergen("בוטנים"));
            Allergen nuts = allergenRepository.save(createAllergen("אגוזים"));
            Allergen sesame = allergenRepository.save(createAllergen("שומשום"));
            Allergen eggs = allergenRepository.save(createAllergen("ביצים"));
            Allergen soy = allergenRepository.save(createAllergen("סויה"));
            Allergen fish = allergenRepository.save(createAllergen("דגים"));

            // 4. יצירת מוצרים נפוצים
            Product flour = productRepository.save(createProduct("קמח חיטה", Set.of(gluten)));
            Product butter = productRepository.save(createProduct("חמאה", Set.of(lactose)));
            Product milk = productRepository.save(createProduct("חלב", Set.of(lactose)));
            Product peanutButter = productRepository.save(createProduct("חמאת בוטנים", Set.of(peanuts)));
            Product egg = productRepository.save(createProduct("ביצה", Set.of(eggs)));
            Product soySauce = productRepository.save(createProduct("רוטב סויה", Set.of(soy, gluten)));
            Product salmon = productRepository.save(createProduct("סלמון", Set.of(fish)));
            Product tahini = productRepository.save(createProduct("טחינה גולמית", Set.of(sesame)));
            Product sugar = productRepository.save(createProduct("סוכר", Set.of()));
            Product chocolate = productRepository.save(createProduct("שוקולד מריר", Set.of(soy))); // עלול להכיל סויה

            // 5. יצירת מתכונים דוגמה
            
            // מתכון 1: עוגת שוקולד חגיגית
            Recipe cake = new Recipe();
            cake.setTitle("עוגת שוקולד חגיגית");
            cake.setInstructions("1. מחממים תנור ל-180 מעלות.\n2. מערבבים קמח, סוכר, ביצים וחמאה.\n3. אופים 40 דקות.");
            cake.setPreparationTimeMinutes(40);
            cake.setDifficulty(Difficulty.EASY);
            cake.setKashrut(Kashrut.DAIRY);
            cake.setCreatedBy(admin);

            RecipeIngredient ing1 = new RecipeIngredient();
            ing1.setRecipe(cake);
            ing1.setProduct(flour);
            ing1.setQuantity(2.0);
            ing1.setMeasurementUnit(MeasurementUnit.CUP);

            RecipeIngredient ing2 = new RecipeIngredient();
            ing2.setRecipe(cake);
            ing2.setProduct(butter);
            ing2.setQuantity(150.0);
            ing2.setMeasurementUnit(MeasurementUnit.GRAM);

            RecipeIngredient ing3 = new RecipeIngredient();
            ing3.setRecipe(cake);
            ing3.setProduct(egg);
            ing3.setQuantity(3.0);
            ing3.setMeasurementUnit(MeasurementUnit.UNIT);

            RecipeIngredient ing4 = new RecipeIngredient();
            ing4.setRecipe(cake);
            ing4.setProduct(sugar);
            ing4.setQuantity(1.0);
            ing4.setMeasurementUnit(MeasurementUnit.CUP);

            cake.setIngredients(List.of(ing1, ing2, ing3, ing4));
            recipeRepository.save(cake);

            // מתכון 2: דג סלמון בתנור
            Recipe salmonRecipe = new Recipe();
            salmonRecipe.setTitle("סלמון בתנור ברוטב סויה");
            salmonRecipe.setInstructions("1. משרים את הסלמון ברוטב סויה.\n2. אופים בתנור ב-200 מעלות למשך 15 דקות.");
            salmonRecipe.setPreparationTimeMinutes(20);
            salmonRecipe.setDifficulty(Difficulty.EASY);
            salmonRecipe.setKashrut(Kashrut.PARVE);
            salmonRecipe.setCreatedBy(user);

            RecipeIngredient sIng1 = new RecipeIngredient();
            sIng1.setRecipe(salmonRecipe);
            sIng1.setProduct(salmon);
            sIng1.setQuantity(500.0);
            sIng1.setMeasurementUnit(MeasurementUnit.GRAM);

            RecipeIngredient sIng2 = new RecipeIngredient();
            sIng2.setRecipe(salmonRecipe);
            sIng2.setProduct(soySauce);
            sIng2.setQuantity(4.0);
            sIng2.setMeasurementUnit(MeasurementUnit.TABLESPOON);

            salmonRecipe.setIngredients(List.of(sIng1, sIng2));
            recipeRepository.save(salmonRecipe);

            System.out.println("--- אתחול הנתונים הסתיים בהצלחה! יש מתכון בטבלה ---");
        }
    }

    private Allergen createAllergen(String name) {
        Allergen a = new Allergen();
        a.setName(name);
        return a;
    }

    private Product createProduct(String name, Set<Allergen> allergens) {
        Product p = new Product();
        p.setName(name);
        p.setAllergens(allergens);
        return p;
    }
}