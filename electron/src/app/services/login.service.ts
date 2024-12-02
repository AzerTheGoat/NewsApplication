import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import {User} from "../entities/User";
import { ElectronService } from '../electron.service';

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

  constructor(private http: HttpClient, private electronService: ElectronService) { 
    this.loadSession();
  }

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
        this.setLoggedStatus(true);
        this.setUsername(user.username);
        this.saveSession(user);
        console.log(user)
      })
    );
  }

  getUser() {
    return this.user;
  }

  logout() {
    this.user = null;
    this.setLoggedStatus(false);
    this.setUsername("");
    this.electronService.clearSession();
  }

  private saveSession(user: User) {
    // Sauvegarder la session sur le système de fichiers via le service Electron
    this.electronService.saveSession({ token: user.token, user: user });
  }

  private loadSession() {
    // Charger la session depuis le système de fichiers via le service Electron
    this.electronService.loadSession().then(session => {
      if (session) {
        this.user = session.user;
        this.setLoggedStatus(true);
        this.setUsername(session.user.username);
        console.log('Session loaded:', session);
      }
    });
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