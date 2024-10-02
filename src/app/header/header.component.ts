import { Component } from '@angular/core';
import {NgClass, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {LoginService} from "../services/login.service";
import {NewsService} from "../services/news.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    NgIf,
    ReactiveFormsModule,
    NgClass,
    FormsModule
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  isModalOpen: boolean = false;
  loginForm: FormGroup;
  isCorrectLogs: boolean = true;
  isLogged: boolean = false;
  usermane: string = '';
  searchText: string = '';
  categorySelected: string = '';

  constructor(private fb: FormBuilder, private loginService : LoginService, public newsService: NewsService, private router: Router) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
    this.loginService.isLogged$.subscribe(status => {
      this.isLogged = status;
    });
    this.loginService.username$.subscribe(username => {
      this.usermane = username;
    });
    this.newsService.searchInput$.subscribe(text => {
      this.searchText = text;
    });
    this.newsService.categoryFilter$.subscribe(category => {
      this.categorySelected = category;
    });
  }

  get usernameControl() {
    return this.loginForm.get('username');
  }

  get passwordControl(){
    return this.loginForm.get('password');
  }

  // Open the modal
  openModal() {
    this.isModalOpen = true;
  }

  // Close the modal
  closeModal() {
    this.isModalOpen = false;
  }

  // Handle form submission
  onSubmit() {
    if (this.loginForm.valid) {
      const { username, password } = this.loginForm.value;

      this.loginService.login(username, password).subscribe(
        user => {
          this.closeModal();

          this.loginService.setLoggedStatus(true);
          this.loginService.setUsername(username);
        },
        error => {
          console.error('Login failed', error);
          this.isCorrectLogs = false;
        }
      );
    } else {
      // Handle validation errors if any
      console.log('Form is invalid');
    }
  }

  logout() {
    this.loginService.logout();
    this.loginService.setLoggedStatus(false);
    this.loginService.setUsername('');
  }

  goToWriteArticle() {
    this.router.navigate(['/write-article/create']);
  }
}
