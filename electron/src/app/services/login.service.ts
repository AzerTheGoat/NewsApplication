import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import {User} from "../entities/User";

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private user: User | null = null;

  private loginUrl = 'http://sanger.dia.fi.upm.es/pui-rest-news/login';

  private message: string | null = null;

  private isLoggedSubject = new BehaviorSubject<boolean>(false);
  private usernameSubject = new BehaviorSubject<string>("");

  private httpOptions = {
    headers: new HttpHeaders()
      .set('Content-Type', 'x-www-form-urlencoded')
  };

  constructor(private http: HttpClient) { }

  isLogged$ = this.isLoggedSubject.asObservable();
  username$ = this.usernameSubject.asObservable();

  setLoggedStatus(status: boolean) {
    this.isLoggedSubject.next(status);
  }
  setUsername(username: string) {
    this.usernameSubject.next(username);
  }
  isLogged() {
    return this.user != null;
  }

  login(name: string, pwd: string): Observable<User> {
    const usereq = new HttpParams()
      .set('username', name)
      .set('passwd', pwd);

    return this.http.post<User>(this.loginUrl, usereq).pipe(
      tap(user => {
        this.user = user;
        console.log(user)
      })
    );
  }

  getUser() {
    return this.user;
  }

  logout() {
    this.user = null;
  }


  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      this.user = null;
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      console.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

}