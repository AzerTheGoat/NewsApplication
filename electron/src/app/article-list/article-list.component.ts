import {Component, Input} from '@angular/core';
import {Article} from "../entities/Article";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {NewsService} from "../services/news.service";
import {Router} from "@angular/router";
import {MainPageComponent} from "../main-page/main-page.component";

@Component({
  selector: 'app-article-list',
  standalone: true,
  imports: [
    NgIf,
    NgOptimizedImage
  ],
  templateUrl: './article-list.component.html',
  styleUrl: './article-list.component.css'
})
export class ArticleListComponent {
  @Input() article: Article = new Article();
  @Input() isLogged: boolean = false;
  isModalOpen: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private router: Router, private newsService: NewsService, private mainPageComponent : MainPageComponent) {}

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
        this.successMessage = 'The article was successfully deleted.';
        this.errorMessage = '';
        this.isModalOpen = false;
        setTimeout(() => {
        this.mainPageComponent.ngOnInit(); // IN ORTHER TO REFRESH MY ARTICLES LIST, I COULD HAVE DONE IT WITH OBSERVALE ALSO
      }, 2000);
      },
      (error) => {
        this.errorMessage = error;
        this.successMessage = '';
        this.isModalOpen = false;
      }
    );
  }
}
