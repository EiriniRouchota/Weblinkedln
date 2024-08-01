import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  email: string = "";
  password: string = "";

  constructor(private router: Router, private http: HttpClient) { }


  Login() {

    console.log(this.email);
    console.log(this.password);

    let bodyData = {

      email: this.email,
      password: this.password

    };

    this.http.post("http://localhost:8080/api/v1/employee/login", bodyData).subscribe((resultData: any) => {
      console.log(resultData);

      if (resultData.message == "Email not exists") {
        alert("Email does not exist");

      }

      else if (resultData.message == "Login Success") {
        this.router.navigateByUrl('/home');
      }

      else {
        alert("Incorrect Email and Password not match");
      }

    })
  }

}
