import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  confirmPassword = '';
  errorMessage = '';
  successMessage = '';
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onRegister(): void {
    // ולידציות
    if (!this.username || !this.email || !this.password || !this.confirmPassword) {
      this.errorMessage = 'יש למלא את כל השדות';
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.errorMessage = 'הסיסמאות לא תואמות';
      return;
    }

    if (this.password.length < 4) {
      this.errorMessage = 'הסיסמה חייבת להכיל לפחות 4 תווים';
      return;
    }

    // בדיקת פורמט אימייל בסיסית
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) {
      this.errorMessage = 'כתובת האימייל אינה תקינה';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.register(this.username, this.password, this.email).subscribe({
      next: () => {
        this.successMessage = 'ההרשמה הצליחה! מעבירים אותך לעמוד ההתחברות...';
        this.isLoading = false;
        // מעבר אוטומטי לעמוד ההתחברות אחרי 2 שניות
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err: any) => {
        this.isLoading = false;
        if (err.status === 400) {
          this.errorMessage = typeof err.error === 'string'
            ? err.error
            : 'שם המשתמש או האימייל כבר קיימים במערכת';
        } else {
          this.errorMessage = 'אירעה שגיאה בשרת, נסי שוב מאוחר יותר';
        }
      }
    });
  }
}
