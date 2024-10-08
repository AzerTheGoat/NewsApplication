import {Component, OnInit} from '@angular/core';
import {Article} from "../entities/Article";
import {NewsService} from "../services/news.service";
import {ActivatedRoute, Router} from "@angular/router";
import {async, Observable} from "rxjs";
import {CommonModule, NgIf} from "@angular/common";
import {routes} from "../app.routes";
import {HeaderComponent} from "../header/header.component";
import {LoginService} from "../services/login.service";

@Component({
  selector: 'app-display-article',
  standalone: true,
  imports: [
    NgIf, CommonModule, HeaderComponent
  ],
  templateUrl: './display-article.component.html',
  styleUrl: './display-article.component.css'
})
export class DisplayArticleComponent implements OnInit {
  article: Article = new Article();
  isErrorOnFetchingArticleDetailsWhileEditing: boolean = false;
  isLogged: boolean = false;



  constructor(private route: ActivatedRoute,private loginService : LoginService, private newsService: NewsService, private router: Router) {
    this.loginService.isLogged$.subscribe(status => {
      this.isLogged = status;
    });
  }

  ngOnInit() {
    const articleId = +this.route.snapshot.paramMap.get('id')!;

    this.newsService.getArticle(articleId).subscribe(article => {
        this.article = article;
      },
      error => {
        alert('Error loading article details' + error);
        this.isErrorOnFetchingArticleDetailsWhileEditing = true;
      }
    );
  }

  goToMainPage() {
    this.router.navigate(['/']);
  }

  goToEditArticle() {
    this.router.navigate(['/write-article/' + +this.route.snapshot.paramMap.get('id')!]);
  }


}
