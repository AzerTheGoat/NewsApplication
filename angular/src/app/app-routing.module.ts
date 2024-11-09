import {RouterModule} from "@angular/router";
import {routes} from "./app.routes";
import {NgModule} from "@angular/core";
import { HttpClientModule } from '@angular/common/http';


@NgModule({
  imports: [RouterModule.forRoot(routes),HttpClientModule],
  exports: [RouterModule]
})
export class AppRoutingModule { }
