import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Allergen {
  id?: number;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class AllergenService {
  private apiUrl = 'http://localhost:8080/api/allergens';

  constructor(private http: HttpClient) {}

  getAllAllergens(): Observable<Allergen[]> {
    return this.http.get<Allergen[]>(this.apiUrl);
  }

  addAllergen(allergen: Allergen): Observable<Allergen> {
    return this.http.post<Allergen>(this.apiUrl, allergen);
  }

  updateAllergen(id: number, allergen: Allergen): Observable<Allergen> {
    return this.http.put<Allergen>(`${this.apiUrl}/${id}`, allergen);
  }

  deleteAllergen(id: number): Observable<string> {
    // מחיקה עשויה להחזיר פשוט טקסט במקרה של הצלחה
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }
}
