import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onLogin(): void {
    // ולידציה בסיסית
    if (!this.username || !this.password) {
      this.errorMessage = 'יש למלא את כל השדות';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.username, this.password).subscribe({
      next: () => {
        // התחברות הצליחה - מעבירים לעמוד הראשי
        this.router.navigate(['/']);
      },
      error: (err: any) => {
        this.isLoading = false;
        if (err.status === 401) {
          this.errorMessage = 'שם משתמש או סיסמה שגויים';
        } else {
          this.errorMessage = 'אירעה שגיאה בשרת, נסי שוב מאוחר יותר';
        }
      }
    });
  }
}
