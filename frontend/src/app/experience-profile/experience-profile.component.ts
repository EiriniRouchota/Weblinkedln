import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth/auth.service';
import { AlertService } from '../services/alert.service'; // Import the AlertService

@Component({
  selector: 'app-experience-profile',
  templateUrl: './experience-profile.component.html',
  styleUrls: ['./experience-profile.component.scss'],
})
export class ExperienceProfileComponent implements OnInit {
  // Declare an array of experience forms with default values
  experienceForms = [
    {
      jobtitle: '',
      companyname: '',
      description: '', // Added description field
      startDate: '',
      endDate: '',
      isPublic: false,
    },
  ];

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.experienceForms = [];
    this.fetchUserExperiences();
  }

  fetchUserExperiences(): void {
    const token = this.authService.getToken(); // Retrieve the stored token

    if (!token) {
      console.error('No token found, cannot make authenticated request');
      return;
    }

    let headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    // Fetch existing experiences for the current user
    this.http
      .get<any[]>('http://localhost:8080/api/v1/employee/auth/me/experience', {
        headers,
      })
      .subscribe(
        (experiences: any[]) => {
          // Loop through the fetched experiences and populate the form array
          experiences.forEach((experience) => {
            this.experienceForms.push({
              jobtitle: experience.jobtitle,
              companyname: experience.companyname,
              description: experience.description, // Added description field
              startDate: new Date(experience.startDate)
                .toISOString()
                .split('T')[0], // Convert to yyyy-mm-dd format
              endDate: new Date(experience.endDate).toISOString().split('T')[0], // Convert to yyyy-mm-dd format
              isPublic: experience.isPublic,
            });
          });
          console.log('Fetched experiences:', this.experienceForms);
        },
        (error) => {
          console.error('Error fetching experiences', error);
        }
      );
  }

  // Method to add a new experience form
  addExperienceForm(): void {
    this.experienceForms.push({
      jobtitle: '',
      companyname: '',
      description: '', // Added description field
      startDate: '',
      endDate: '',
      isPublic: false,
    });
  }

  // Method to remove an experience form by index
  removeExperienceForm(index: number): void {
    this.experienceForms.splice(index, 1);
  }

  isEndDateValid(startDate: string, endDate: string): boolean {
    if (!startDate || !endDate) return true; // No validation if one of the dates is missing
    const start = new Date(startDate);
    const end = new Date(endDate);
    return end > start;
  }

  // Check all experience forms for valid dates
  areDatesValid(): boolean {
    return this.experienceForms.every((experience) =>
      this.isEndDateValid(experience.startDate, experience.endDate)
    );
  }
  // Submit all forms in one batch
  onSubmit(form: any): void {
    const token = this.authService.getToken(); // Retrieve the stored token
    if (form.invalid || !this.areDatesValid()) {
      // Prevent form submission if invalid
      console.log('Form is invalid or dates are incorrect.');
      return;
    }
    if (!token) {
      console.error('No token found, cannot make authenticated request');
      return;
    }

    // Validate that all fields in each form are filled
    const invalidForms = this.experienceForms.some(
      (form) =>
        !form.jobtitle ||
        !form.companyname ||
        !form.description || // Added validation for description
        !form.startDate ||
        !form.endDate
    );

    if (invalidForms) {
      console.error('Please fill out all fields.');
      return;
    }

    // Set the Authorization header with the token
    let headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    // Log the data being sent to the API for debugging
    console.log('Data being sent to the API:', this.experienceForms);

    // Batch submit all experience forms in one request
    this.http
      .post(
        'http://localhost:8080/api/v1/employee/auth/add/experience',
        this.experienceForms,
        { headers }
      )
      .subscribe(
        (response) => {
          console.log('All experiences saved successfully', response);
          this.alertService.showAlert(
            'success',
            'Experiences saved successfully!'
          ); // Show success feedback
        },
        (error) => {
          console.error('Error saving experiences', error);
          this.alertService.showAlert(
            'danger',
            'Error saving experiences. Please try again.'
          ); // Show error feedback
        }
      );
  }
}
