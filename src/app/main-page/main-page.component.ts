import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from "../header/header.component";
import {ArticleListComponent} from "../article-list/article-list.component";
import {LoginService} from "../services/login.service";
import {NewsService} from "../services/news.service";
import {NgForOf, NgIf} from "@angular/common";
import {Article} from "../entities/Article";
import {FilterCardsSearchInputPipe} from "../pipes/filterCardsSearchInput.pipe";
import {FilterCardsSearchCategoryPipe} from "../pipes/filterCardsCategory.pipe";

@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [
    HeaderComponent,
    ArticleListComponent,
    NgForOf,
    FilterCardsSearchInputPipe,
    FilterCardsSearchCategoryPipe,
    NgIf
  ],
  templateUrl: './main-page.component.html',
  styleUrl: './main-page.component.css'
})
export class MainPageComponent implements OnInit{
  articles: Article[] = [];
  isLogged: boolean = false;
  searchText: string = '';
  categorySelected: string = '';
  isErrorOnFetchingArticles: boolean = false;
  isDataLoaded: boolean = false;
  errorMessage: string = '';

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
      this.isDataLoaded = true;
    }, error => {
        alert('Error loading articles' + error);
        this.isErrorOnFetchingArticles = true;
        this.errorMessage = error;
      }
    );

  }




}
