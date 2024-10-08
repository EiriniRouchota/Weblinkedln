import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AlertService } from '../services/alert.service'; // Import the AlertService
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'], // corrected styleUrls to plural
})
export class RegisterComponent {
  employeename: string = '';
  lastname: string = '';
  email: string = '';
  phone: string = '';
  password: string = '';
  confirmPassword: string = '';
  alertMessage: string = '';
  alertType: string = '';

  constructor(
    private http: HttpClient,
    private alertService: AlertService,
    private router: Router
  ) {} // Inject both services

  save(registerForm: NgForm) {
    // Check if the form is valid
    if (!registerForm.valid) {
      this.alertService.showAlert(
        'danger',
        'Please fill out all required fields correctly.'
      );
      return;
    }

    // Check if passwords match
    if (this.confirmPassword !== this.password) {
      this.alertService.showAlert(
        'danger',
        'Password and confirm password fields must match!'
      );
      return;
    }

    // If form is valid and passwords match, proceed with the HTTP request
    let bodyData = {
      employeename: this.employeename,
      email: this.email,
      password: this.password,
      employeelastname: this.lastname,
      phone: this.phone,
    };

    this.http
      .post('http://localhost:8080/api/v1/employee/save', bodyData, {
        responseType: 'text',
      })
      .subscribe({
        next: (resultData: any) => {
          console.log(resultData);
          if (
            resultData === 'An employee with this email already exists.' ||
            resultData == ''
          ) {
            this.alertService.showAlert(
              'warning',
              'An employee with this email already exists.'
            );
          } else {
            this.alertService.showAlert(
              'success',
              'Employee Registered Successfully'
            );
            // Delay the navigation by 3 seconds (3000 milliseconds)
            setTimeout(() => {
              this.router.navigateByUrl('/login');
            }, 2000); // Adjust the time as needed
          }
        },
        error: (error) => {
          console.error(
            'There was an error during the registration process',
            error
          );
          if (error.status === 400) {
            alert('Bad Request. Please check the data you have entered.');
          } else if (error.status === 409) {
            // Assuming 409 for conflict (email already exists)
            alert('An employee with this email already exists.');
          } else {
            alert('An unexpected error occurred. Please try again later.');
          }
        },
      });
  }
}
