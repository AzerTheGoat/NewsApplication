import {Component, Input} from '@angular/core';
import {Article} from "../entities/Article";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {NewsService} from "../services/news.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-article-card',
  standalone: true,
  imports: [
    NgIf,
    NgOptimizedImage
  ],
  templateUrl: './article-card.component.html',
  styleUrl: './article-card.component.css'
})
export class ArticleCardComponent {
  @Input() article: Article = new Article();
  @Input() isLogged: boolean = false;
  isModalOpen: boolean = false;
  errorMessage: string = '';

  constructor(private router : Router, private newsService: NewsService) {
  }

  goToArticle(newsId?: number) {
    if (!newsId) {
      alert('No article id provided');
      return;
    }
    this.router.navigate(['/detail/' + newsId]);
  }

  goToEditArticle(newsId?: number) {
    if (!newsId) {
      alert('No article id provided');
      return;
    }
    this.router.navigate(['/write-article/' + newsId]);
  }

  openModal() {
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  deleteArticle(newsId?: number) {
    if (!newsId) {
      alert('No article id provided');
      return;
    }
    this.newsService.deleteArticle(newsId).subscribe(() => {
      this.router.navigate(['/']).then(() => {
        window.location.reload();
      });
      this.isModalOpen = false;
    }, error => {
      this.errorMessage = error;
    });

  }
}
