import {Pipe, PipeTransform} from "@angular/core";
import {Article} from "../entities/Article";

@Pipe(
  {
    name: 'FilterCardsCategory',
    standalone: true
  }
)
export class FilterCardsSearchCategoryPipe implements PipeTransform {
  transform(cards: Article[], category: string): Article[] {
    if (!cards) return [];

    return cards.filter(card =>
      card.category.toLowerCase().includes(category.toLowerCase())
    );
  }
}
