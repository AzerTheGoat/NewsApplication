import { Routes } from '@angular/router';
import {MainPageComponent} from "./main-page/main-page.component";
import {DetailsArticleComponent} from "./article-details/article-details.component";
import {EditionArticleComponent} from "./article-edition/article-edition.component";


export const routes: Routes = [
  { path: '', component: MainPageComponent},
  { path: 'detail/:id', component: DetailsArticleComponent },
  { path: 'write-article/:id', component: EditionArticleComponent },

];
