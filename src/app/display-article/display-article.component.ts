import {Component, OnInit} from '@angular/core';
import {Article} from "../entities/Article";
import {NewsService} from "../services/news.service";
import {ActivatedRoute, Router} from "@angular/router";
import {async, Observable} from "rxjs";
import {CommonModule, NgIf} from "@angular/common";
import {routes} from "../app.routes";
import {HeaderComponent} from "../header/header.component";

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

  constructor(private route: ActivatedRoute, private newsService: NewsService, private router: Router) {
  }

  ngOnInit() {
    const articleId = +this.route.snapshot.paramMap.get('id')!;

    this.newsService.getArticle(articleId).subscribe(article => {
      this.article = article;
    });
  }

  goToMainPage() {
    this.router.navigate(['/']);
  }
}
