import {Category} from "./Category";

export class Article{
  abstract: string='';
  category: Category=Category.NONE;
  id?: number=undefined;
  subtitle: string='';
  thumbnail_image: string='';
  thumbnail_media_type: string='';
  title: string='';
  username: string='ANON_TEAM_01';
  body: string='';
  image_data: string='';
  image_media_type: string='';
  update_date: string = ''; 
  updated_by: string = '';   
}
