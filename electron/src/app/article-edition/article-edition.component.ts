import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { NewsService } from "../services/news.service";
import { Article } from "../entities/Article";
import {AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import {NgForOf, NgIf} from "@angular/common";
import {HeaderComponent} from "../header/header.component";
import _ from "lodash";
import {LoginService} from "../services/login.service";
import {Category} from "../entities/Category";
import { ElectronService } from '../electron.service';


@Component({
  selector: 'app-edition-article',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    HeaderComponent,
    ReactiveFormsModule,
    NgForOf
  ],
  templateUrl: './article-edition.component.html',
  styleUrls: ['./article-edition.component.css']
})
export class EditionArticleComponent implements OnInit {
  articleForm: FormGroup;
  categories = Object.values(Category);
  imageError: string | null = null;
  isImageSaved: boolean = false;
  cardImageBase64: string | null = null;

  errorOnSubmit: boolean = false;
  errorMessage: string = '';

  isLogged: boolean = false;

  isErrorOnFetchingArticleDetailsWhileEditing: boolean = false;

  successMessage: string = '';
  showNotificationSign: boolean = false;

  constructor(
    private fb: FormBuilder, 
    private newsService: NewsService, 
    private route: ActivatedRoute, 
    private router: Router, 
    private loginService: LoginService,
    private electronService: ElectronService,
    private cdr: ChangeDetectorRef
  ) {
    this.articleForm = this.fb.group({
      id: [undefined],
      title: ['', [Validators.required, Validators.minLength(5)]],
      subtitle: ['',[Validators.required]],
      category: [Category.NONE, [Validators.required, this.categoryValidator]],
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

  sendNotification(title: string, message: string, callback?: () => void) {
    this.electronService.sendNotification({
      title,
      message,
      callback: () => {
        if (callback) callback();
        this.showNotificationSign = true;
        this.cdr.detectChanges();
        setTimeout(() => {
          this.showNotificationSign = false;
          this.cdr.detectChanges();
        }, 5000);
      },
    });
  }

  validateFields(): boolean {
    let isValid = true;

    if (!this.articleForm.get('title')?.value || this.articleForm.get('title')?.value.trim().length < 5) {
      this.sendNotification(
        'Title Missing or Too Short',
        'The title must be at least 5 characters long.',
        () => this.scrollToField('title')
      );
      isValid = false;
    }

    if (!this.articleForm.get('subtitle')?.value || this.articleForm.get('subtitle')?.value.trim().length < 5) {
      this.sendNotification(
        'Subtitle Missing or Too Short',
        'The subtitle must be at least 5 characters long.',
        () => this.scrollToField('subtitle')
      );
      isValid = false;
    }

    if (!this.articleForm.get('abstract')?.value || this.articleForm.get('abstract')?.value.trim().length < 10) {
      this.sendNotification(
        'Abstract Missing or Too Short',
        'The abstract must be at least 10 characters long.',
        () => this.scrollToField('abstract')
      );
      isValid = false;
    }

    if (this.articleForm.get('category')?.value === Category.NONE) {
      this.sendNotification(
        'Category Missing',
        'Please select a valid category for the article.',
        () => this.scrollToField('category')
      );
      isValid = false;
    }

    return isValid;
  }

  scrollToField(fieldName: string) {
    const field = document.querySelector(`[formControlName="${fieldName}"]`);
    if (field) {
      (field as HTMLElement).scrollIntoView({ behavior: 'smooth', block: 'center' });
      (field as HTMLElement).focus();
    }
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

    if (!this.validateFields()) {
      return;
    }

/*     if (this.articleForm.invalid) {
        this.errorOnSubmit = true;
        this.errorMessage = 'Please fill in the required fields.';
        return;
    } */

    const articleData = this.articleForm.value;
    const articleId = this.route.snapshot.paramMap.get('id');

    this.loginService.username$.subscribe(username => {
        articleData.updated_by = username;

        if (articleId && !isNaN(Number(articleId))) {
            // Mise à jour de l'article existant
            this.newsService.updateArticle(articleData).subscribe(
                response => {
                  /* window.alert('Article updated successfully!');
                  this.successMessage = 'Article updated successfully!';
                    setTimeout(() => this.successMessage = '', 3000); */
                    this.sendNotification('Article Updated', 'The article has been updated successfully.');
                    this.router.navigate(['/']);
                },
                error => {
                  /*   this.errorOnSubmit = true;
                    this.errorMessage = 'Failed to update the article: ' + error.message; */
                    this.sendNotification('Update Failed', `Failed to update the article: ${error.message}`);
                }
            );
        } else {
            // Création d'un nouvel article
            this.newsService.createArticle(articleData).subscribe(
                response => {
                    /* window.alert('Article created successfully!'); */
                    this.sendNotification('Article Created', 'The article has been created successfully.');
                    this.router.navigate(['/']);
                },
                error => {
                    /* this.errorOnSubmit = true;
                    this.errorMessage = 'Failed to create the article: ' + error.message; */
                    this.sendNotification('Creation Failed', `Failed to create the article: ${error.message}`);
                }
            );
        }
    });
  }
  goToMainPage() {
    this.router.navigate(['/']);
  }

}
