import { Component, Input, OnInit } from '@angular/core';
import { NewsService } from "../services/news.service";
import { Article } from "../entities/Article";
import {AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import {NgForOf, NgIf} from "@angular/common";
import {HeaderComponent} from "../header/header.component";
import _ from "lodash";
import {LoginService} from "../services/login.service";
import {Category} from "../entities/Category";

@Component({
  selector: 'app-writing-article',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    HeaderComponent,
    ReactiveFormsModule,
    NgForOf
  ],
  templateUrl: './writing-article.component.html',
  styleUrls: ['./writing-article.component.css']
})
export class WritingArticleComponent implements OnInit {
  articleForm: FormGroup;
  imageError: string | null = null;
  isImageSaved: boolean = false;
  cardImageBase64: string | null = null;

  errorOnSubmit: boolean = false;
  errorMessage: string = '';

  isLogged: boolean = false;

  isErrorOnFetchingArticleDetailsWhileEditing: boolean = false;

  categories = Object.values(Category); // Pour générer les options du select
  Category = Category; // Pour accéder à l'énumération dans le template


  constructor(private fb: FormBuilder, private newsService: NewsService, private route: ActivatedRoute, private router: Router, private loginService: LoginService) {
    this.articleForm = this.fb.group({
      id: [undefined],
      title: ['', [Validators.required, Validators.minLength(5)]],
      subtitle: [''],
      category: [Category.NONE, [Validators.required, this.categoryValidator]], // Ajout de la validation personnalisée
      abstract: ['', Validators.required],
      body: [''],
      image_media_type: [''],
      image_data: ['']
    });
    this.loginService.isLogged$.subscribe(status => {
      this.isLogged = status;
    });
  }

  ngOnInit() {
    const articleId = this.route.snapshot.paramMap.get('id');
    if (articleId && !isNaN(Number(articleId))) {
      this.newsService.getArticle(+articleId).subscribe(article => {
          this.articleForm.patchValue(article);
        },
        error => {
          alert('Error loading article details' + error);
          this.isErrorOnFetchingArticleDetailsWhileEditing = true;
        }
      );
    }
  }

  categoryValidator(control: AbstractControl) {
    return control.value === Category.NONE ? { invalidCategory: true } : null;
  }

  get titleControl() {
    return this.articleForm.get('title');
  }

  get subtitleControl() {
    return this.articleForm.get('subtitle');
  }

  get categoryControl() {
    return this.articleForm.get('category');
  }

  get abstractControl() {
    return this.articleForm.get('abstract');
  }


  fileChangeEvent(fileInput: any) {
    this.imageError = null;

    if (fileInput.target.files && fileInput.target.files[0]) {
      const MAX_SIZE = 20971520; // 20MB
      const ALLOWED_TYPES = ['image/png', 'image/jpeg'];

      if (fileInput.target.files[0].size > MAX_SIZE) {
        this.imageError = 'Maximum size allowed is ' + MAX_SIZE / 1000 + 'Mb';
        return false;
      }

      if (!_.includes(ALLOWED_TYPES, fileInput.target.files[0].type)) {
        this.imageError = 'Only Images are allowed ( JPG | PNG )';
        return false;
      }

      const reader = new FileReader();

      const article = this.articleForm.value;

      reader.onload = (e: any) => {
        const imgBase64Path = e.target.result;
        this.cardImageBase64 = imgBase64Path;
        this.isImageSaved = true;

        article.image_media_type = fileInput.target.files[0].type;
        const head = article.image_media_type.length + 13;
        article.image_data = e.target.result.substring(head, e.target.result.length);
        this.articleForm.get('image_data')?.setValue(article.image_data);
        this.articleForm.get('image_media_type')?.setValue(article.image_media_type);
      };
      reader.readAsDataURL(fileInput.target.files[0]);
    }
    return true;
  }

  markAllAsTouched() {
    this.articleForm.markAllAsTouched();
  }

  saveArticle() {
    this.markAllAsTouched();
    if (this.articleForm.invalid) {
      this.errorOnSubmit = true;
      this.errorMessage = 'Please fill in the required fields.';
      return;
    }

    const articleData = this.articleForm.value;
    const articleId = this.route.snapshot.paramMap.get('id');
    if (articleId && !isNaN(Number(articleId))) {
      // Update article
      this.newsService.updateArticle(articleData).subscribe(
        response => {
          this.router.navigate(['/']);
        },
        error => {
          this.errorOnSubmit = true;
          this.errorMessage = 'Failed to update the article: ' + error.message;
        }
      );
    } else {
      // Create article
      this.newsService.createArticle(articleData).subscribe(
        response => {
          this.router.navigate(['/']);
        },
        error => {
          this.errorOnSubmit = true;
          this.errorMessage = 'Failed to create the article: ' + error.message;
        }
      );
    }
  }

  goToMainPage() {
    this.router.navigate(['/']);
  }

}
