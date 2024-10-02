import { Component, Input, OnInit } from '@angular/core';
import { NewsService } from "../services/news.service";
import { Article } from "../entities/Article";
import * as _ from 'lodash';
import {FormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {HeaderComponent} from "../header/header.component";

@Component({
  selector: 'app-writing-article',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    HeaderComponent
  ],
  templateUrl: './writing-article.component.html',
  styleUrls: ['./writing-article.component.css']
})
export class WritingArticleComponent implements OnInit {
  Article: Article = new Article();

  imageError: string | null = null;
  isImageSaved: boolean = false;
  cardImageBase64: string | null = null;

  errorOnSubmit : boolean = false;
  errorMessage : string = '';

  constructor(private newsService: NewsService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    if(!isNaN(Number(this.route.snapshot.paramMap.get('id')))){
      this.newsService.getArticle(+this.route.snapshot.paramMap.get('id')!).subscribe(article => {
        this.Article = article;
      });
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
      reader.onload = (e: any) => {
        const imgBase64Path = e.target.result;
        this.cardImageBase64 = imgBase64Path;
        this.isImageSaved = true;
        this.Article.thumbnail_media_type = fileInput.target.files[0].type;
        const head = this.Article.thumbnail_media_type.length + 13;
        this.Article.image_data = e.target.result.substring(head);
        this.Article.image_media_type = fileInput.target.files[0].type;
      };
      reader.readAsDataURL(fileInput.target.files[0]);
    }
    return true;
  }

  saveArticle() {
    if (isNaN(Number(this.route.snapshot.paramMap.get('id')))) {
      // Creating an article
      this.newsService.createArticle(this.Article).subscribe(
        response => {
          console.log('Article created:', response);
          this.errorOnSubmit = false; // No error, reset the error flag
          this.router.navigate(['/']);
        },
        error => {
          // Handle the error scenario
          this.errorOnSubmit = true;
          this.errorMessage = 'Failed to create the article: ' + error.message;
          console.error('Error creating article:', error);
        }
      );
    } else {
      // Updating an article
      this.newsService.updateArticle(this.Article).subscribe(
        response => {
          console.log('Article updated:', response);
          this.errorOnSubmit = false; // No error, reset the error flag
          this.router.navigate(['/']);

        },
        error => {
          // Handle the error scenario
          this.errorOnSubmit = true;
          this.errorMessage = 'Failed to update the article: ' + error.message;
          console.error('Error updating article:', error);
        }
      );
    }
  }

}
