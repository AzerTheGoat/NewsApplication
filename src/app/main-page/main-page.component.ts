import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from "../header/header.component";
import {ArticleCardComponent} from "../article-card/article-card.component";
import {LoginService} from "../services/login.service";
import {NewsService} from "../services/news.service";
import {NgForOf} from "@angular/common";
import {Article} from "../entities/Article";
import {FilterCardsSearchInputPipe} from "../pipes/filterCardsSearchInput.pipe";
import {FilterCardsSearchCategoryPipe} from "../pipes/filterCardsCategory.pipe";

@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [
    HeaderComponent,
    ArticleCardComponent,
    NgForOf,
    FilterCardsSearchInputPipe,
    FilterCardsSearchCategoryPipe
  ],
  templateUrl: './main-page.component.html',
  styleUrl: './main-page.component.css'
})
export class MainPageComponent implements OnInit{
  articles: Article[] = [];
  isLogged: boolean = false;
  searchText: string = '';
  categorySelected: string = '';

  constructor(private newService: NewsService, private loginService: LoginService, private newsService: NewsService) {
    this.loginService.isLogged$.subscribe(status => {
      this.isLogged = status;
    });
    this.newsService.searchInput$.subscribe(text => {
      this.searchText = text;
    });
    this.newsService.categoryFilter$.subscribe(category => {
      this.categorySelected = category;
    });
  }

  ngOnInit() {
    this.newService.getArticles().subscribe(articles => {
      this.articles = articles;
    }, error =>
    console.log(error));
  }




}
