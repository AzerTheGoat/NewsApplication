import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';

import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Article} from "../entities/Article";
import {Category} from "../entities/Category";

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  private newsUrl = 'http://sanger.dia.fi.upm.es/pui-rest-news/articles';
  private articleUrl = 'http://sanger.dia.fi.upm.es/pui-rest-news/article';
  private searchCardInput = new BehaviorSubject<string>("");
  private categoryFilter = new BehaviorSubject<Category>(Category.NONE);


  constructor(private http: HttpClient) {
    this.APIKEY = "";
  }

  searchInput$ = this.searchCardInput.asObservable();
  categoryFilter$ = this.categoryFilter.asObservable();
  setSearchInput(searchText: string) {
    this.searchCardInput.next(searchText);
  }
  setCategoryFilter(category: Category) {
    this.categoryFilter.next(category);
  }

  // Set the corresponding APIKEY accordig to the received by email
  private APIKEY: string | null;
  private APIKEY_ANON = 'ANON01';

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'PUIRESTAUTH apikey=' + this.APIKEY_ANON
    })
  };

  // Modifies the APIKEY with the received value
  setUserApiKey(apikey: string | undefined) {
    if (apikey) {
      this.APIKEY = apikey;
    }
    this.httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: 'PUIRESTAUTH apikey=' + this.APIKEY
      })
    };
    console.log('Apikey successfully changed ' + this.APIKEY);
  }

  setAnonymousApiKey() {
    this.setUserApiKey(this.APIKEY_ANON);
  }

  // Returns the list of news contain elements with the following fields:
  // {"id":...,
  //  "id_user":...,
  //  "abstract":...,
  //  "subtitle":...,
  //  "update_date":...,
  //  "category":...,
  //  "title":...,
  //  "thumbnail_image":...,
  //  "thumbnail_media_type":...}

  getArticles(): Observable<Article[]> {
    return this.http.get<Article[]>(this.newsUrl, this.httpOptions);
  }

  deleteArticle(article: Article | number): Observable<Article> {
    const url = `${this.articleUrl}/${article}`;
    return this.http.delete<Article>(url, this.httpOptions);
  }


  // Returns an article which contains the following elements:
  // {"id":...,
  //  "id_user":...,
  //  "abstract":...,
  //  "subtitle":...,
  //  "update_date":...,
  //  "category":...,
  //  "title":...,
  //  "image_data":...,
  //  "image_media_type":...}


  getArticle(id: number): Observable<Article> {
    console.log('Requesting article id=' + id);
    const url = `${this.articleUrl}/${id}`;
    return this.http.get<Article>(url, this.httpOptions);

  }

  updateArticle(article: Article): Observable<Article> {
    console.log('Updating article id=' + article.id);
    return this.http.post<Article>(this.articleUrl, article, this.httpOptions);
  }

  createArticle(article: Article): Observable<Article> {
    console.log('Creating article');
    console.log(article);
    article.id=undefined;
    return this.http.post<Article>(this.articleUrl, article, this.httpOptions);
  }
}
