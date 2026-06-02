import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
export interface Product {
  id?: number;
  name: string;
  kashrut?: string;
  allergens?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  // נתיב ה-API ב-Spring Boot המשוייך למוצרים
  private apiUrl = 'http://localhost:8080/api/products'; 

  constructor(private http: HttpClient) {}

  // משיכת כל המוצרים (עבור ה-Dropdown של המתכונים כרגע)
  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  // 🌟 תשתית עתידית עבור מנהל המערכת (Admin)
  addProduct(product: any): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  updateProduct(id: number, product: any): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
  }

  deleteProduct(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }
}