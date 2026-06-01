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
  private apiUrl = 'http://127.0.0.1:8080/api/allergens';

  constructor(private http: HttpClient) {}

  getAllAllergens(): Observable<Allergen[]> {
    return this.http.get<Allergen[]>(this.apiUrl);
  }

  addAllergen(allergen: Allergen): Observable<Allergen> {
    return this.http.post<Allergen>(this.apiUrl, allergen);
  }

  deleteAllergen(id: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/${id}`);
  }
}
