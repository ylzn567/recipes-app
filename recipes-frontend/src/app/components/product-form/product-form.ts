import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../../services/product';
import { AllergenService, Allergen } from '../../services/allergen';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './product-form.html',
  styleUrl: './product-form.css'
})
export class ProductFormComponent implements OnInit {
  productForm!: FormGroup;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  // רשימת אלרגנים נמשכת מהשרת
  availableAllergens: Allergen[] = [];

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private allergenService: AllergenService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      kashrut: ['PARVE', Validators.required],
      allergens: this.fb.array([])
    });

    this.allergenService.getAllAllergens().subscribe({
      next: (allergens) => {
        this.availableAllergens = allergens;
        const allergensFormArray = this.productForm.get('allergens') as FormArray;
        allergensFormArray.clear();
        allergens.forEach(() => allergensFormArray.push(this.fb.control(false)));
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching allergens', err);
      }
    });
  }

  get allergensFormArray(): FormArray {
    return this.productForm.get('allergens') as FormArray;
  }

  onSubmit(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.productForm.value;

    // פילטור רק של האלרגנים שהמנהל סימן כ-true והמרתם למבנה שהשרת מצפה לו
    const selectedAllergens = this.availableAllergens
      .filter((_, index) => formValue.allergens[index])
      .map(allergen => ({ name: allergen.name })); // מייצר אובייקט { name: "לקטוז" } עבור ה-Spring Boot

    const newProduct = {
      name: formValue.name,
      kashrut: formValue.kashrut,
      allergens: selectedAllergens
    };

    // שליחה לשרת דרך ה-ProductService
    this.productService.addProduct(newProduct).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.successMessage = '🎉 המוצר נוסף בהצלחה למערכת!';
        this.productForm.reset({ name: '', kashrut: 'PARVE', allergens: this.availableAllergens.map(() => false) });
      },
      error: (err) => {
        this.isSubmitting = false;
        // חילוץ הודעת השגיאה - ייתכן שמגיעה כמחרוזת פשוטה או כאובייקט
        const errorMsg = typeof err.error === 'string' ? err.error : (err.error?.message || err.message || 'שגיאה בהוספת המוצר');
        this.errorMessage = errorMsg;
        console.error(err);
      }
    });
  }
}