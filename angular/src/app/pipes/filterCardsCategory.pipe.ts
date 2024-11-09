import {Pipe, PipeTransform} from "@angular/core";
import {Article} from "../entities/Article";
import {Category} from "../entities/Category";

@Pipe(
  {
    name: 'FilterCardsCategory',
    standalone: true
  }
)
export class FilterCardsSearchCategoryPipe implements PipeTransform {
  transform(cards: Article[], category: Category): Article[] {
    if (!cards) return [];

    if (category === Category.NONE) return cards;

    return cards.filter(card =>
      card.category.toLowerCase().includes(category.toLowerCase())
    );
  }
}
