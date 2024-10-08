import { Component } from '@angular/core';
import { AuthService } from '../services/auth/auth.service';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  token: string | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.token = this.authService.getToken(); // Access the token
    console.log('Token:', this.token);
  }
}
