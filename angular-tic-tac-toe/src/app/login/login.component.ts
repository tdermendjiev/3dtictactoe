import { Component, OnInit } from '@angular/core';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {

  constructor(private authService: AuthService) {}

  ngOnInit() {
  }

  login() {
    this.authService.login();
  }

  async logout() {
    this.authService.logout();
    this.authService.refresh();
  }
}
