import {Component, Input} from '@angular/core';
import {Article} from "../entities/Article";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {NewsService} from "../services/news.service";
import {Router} from "@angular/router";

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

  constructor(private router: Router, private newsService: NewsService) {}

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
        this.successMessage = 'The article was successfully deleted.'; // Message de succès
        this.errorMessage = ''; // Réinitialiser le message d'erreur
        this.isModalOpen = false;
        // Ajouter un délai avant de rafraîchir la page (par exemple 2 secondes)
        setTimeout(() => {
        this.router.navigate(['/']).then(() => {
          window.location.reload();
        });
      }, 2000);
      },
      (error) => {
        this.errorMessage = error;
        this.successMessage = ''; // Réinitialiser le message de succès
        this.isModalOpen = false;
      }
    );
  }
}
