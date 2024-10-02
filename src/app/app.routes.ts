import { Routes } from '@angular/router';
import {MainPageComponent} from "./main-page/main-page.component";
import {DisplayArticleComponent} from "./display-article/display-article.component";
import {WritingArticleComponent} from "./writing-article/writing-article.component";

export const routes: Routes = [
  { path: '', component: MainPageComponent},
  { path: 'detail/:id', component: DisplayArticleComponent },
  { path: 'write-article/:id', component: WritingArticleComponent },

];
