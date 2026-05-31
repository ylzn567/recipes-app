import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RecipeService } from '../../services/recipe';

@Component({
  selector: 'app-recipe-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './recipe-form.html',
  styleUrl: './recipe-form.css'
})
export class RecipeFormComponent implements OnInit {
  recipeForm!: FormGroup;
  isSubmitting = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private recipeService: RecipeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.recipeForm = this.fb.group({
      title: ['', Validators.required],
      instructions: ['', Validators.required],
      preparationTimeMinutes: [0, [Validators.required, Validators.min(1)]],
      difficulty: ['EASY', Validators.required],
      kashrut: ['PARVE', Validators.required],
      ingredients: this.fb.array([])
    });

    // נוסיף מצרך אחד ריק כברירת מחדל
    this.addIngredient();
  }

  get ingredients(): FormArray {
    return this.recipeForm.get('ingredients') as FormArray;
  }

  addIngredient(): void {
    const ingredientForm = this.fb.group({
      productName: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(0.1)]],
      measurementUnit: ['UNIT', Validators.required]
    });
    this.ingredients.push(ingredientForm);
  }

  removeIngredient(index: number): void {
    if (this.ingredients.length > 1) {
      this.ingredients.removeAt(index);
    }
  }

  onSubmit(): void {
    if (this.recipeForm.invalid) {
      this.recipeForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const formValue = this.recipeForm.value;
    
    // המרה לפורמט שהשרת מצפה לו
    const newRecipe = {
      title: formValue.title,
      instructions: formValue.instructions,
      preparationTimeMinutes: formValue.preparationTimeMinutes,
      difficulty: formValue.difficulty,
      kashrut: formValue.kashrut,
      ingredients: formValue.ingredients.map((ing: any) => ({
        quantity: ing.quantity,
        measurementUnit: ing.measurementUnit,
        product: {
          name: ing.productName
        }
      }))
    };

    this.recipeService.createRecipe(newRecipe).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigate(['/']); // חזרה לדף הבית
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err.error || err.message || 'שגיאה בשמירת המתכון';
      }
    });
  }
}
