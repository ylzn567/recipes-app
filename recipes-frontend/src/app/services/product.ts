import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
export interface Product {
  id?: number;
  name: string;
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
  addProduct(product: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, product);
  }

  deleteProduct(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}