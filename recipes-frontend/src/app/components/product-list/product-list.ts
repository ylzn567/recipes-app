import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService, Product } from '../../services/product';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css'
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  loading: boolean = true;
  errorMessage: string | null = null;
  editingId: number | null = null;
  editName: string = '';

  constructor(private productService: ProductService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getAllProducts().subscribe({
      next: (data) => {
        this.products = data || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = 'שגיאה בשליפת מוצרים מהשרת';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  startEdit(product: Product): void {
    if (product.id !== undefined) {
      this.editingId = product.id;
      this.editName = product.name;
    }
  }

  cancelEdit(): void {
    this.editingId = null;
    this.editName = '';
  }

  saveEdit(product: Product): void {
    if (!product.id || !this.editName.trim()) return;
    
    // ממירים את רשימת האלרגנים ממערך של מחרוזות למבנה שהשרת מצפה לו ({ name: "..." })
    const mappedAllergens = (product.allergens || []).map(allergen => ({
      name: allergen
    }));

    const updatedProductData = {
      id: product.id,
      name: this.editName.trim(),
      allergens: mappedAllergens
    };

    this.productService.updateProduct(product.id, updatedProductData).subscribe({
      next: (updated) => {
        const index = this.products.findIndex(p => p.id === updated.id);
        if (index !== -1) {
          this.products[index] = updated;
        }
        this.cancelEdit();
        this.cdr.detectChanges();
      },
      error: (err) => {
        const msg = typeof err.error === 'string' ? err.error : (err.error?.message || err.message);
        alert('שגיאה בעדכון המוצר: ' + msg);
      }
    });
  }

  deleteProduct(product: Product): void {
    if (!product.id) return;
    
    if (confirm(`האם אתה בטוח שברצונך למחוק את המוצר "${product.name}"?`)) {
      this.productService.deleteProduct(product.id).subscribe({
        next: () => {
          this.products = this.products.filter(p => p.id !== product.id);
          this.cdr.detectChanges();
        },
        error: (err) => {
          const msg = typeof err.error === 'string' ? err.error : (err.error?.message || err.message);
          alert('שגיאה במחיקת המוצר: ' + msg);
        }
      });
    }
  }
}
