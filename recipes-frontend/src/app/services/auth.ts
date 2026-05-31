import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

// ממשק לתגובת ה-Login מהשרת
export interface LoginResponse {
  token: string;
  username: string;
  role: string;
}

// ממשק לתגובת ה-Register מהשרת
export interface RegisterResponse {
  username: string;
  email: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  // התחברות - שולח POST לשרת ושומר את הטוקן ב-localStorage
  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { username, password })
      .pipe(
        tap((response: LoginResponse) => {
          // שומרים את הנתונים ב-localStorage כדי שהמשתמש יישאר מחובר
          localStorage.setItem('token', response.token);
          localStorage.setItem('username', response.username);
          localStorage.setItem('role', response.role);
        })
      );
  }

  // הרשמה - שולח POST לשרת עם פרטי המשתמש החדש
  register(username: string, password: string, email: string): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.apiUrl}/register`, { username, password, email });
  }

  // התנתקות - מוחק את כל הנתונים מ-localStorage
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
  }

  // בודק אם המשתמש מחובר (יש טוקן שמור)
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  // מחזיר את שם המשתמש המחובר
  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  // מחזיר את התפקיד של המשתמש (USER / ADMIN)
  getRole(): string | null {
    return localStorage.getItem('role');
  }

  // בודק אם המשתמש הוא מנהל מערכת
  isAdmin(): boolean {
    return localStorage.getItem('role') === 'ADMIN';
  }
}
