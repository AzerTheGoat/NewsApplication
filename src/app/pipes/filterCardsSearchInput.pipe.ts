import {Pipe, PipeTransform} from "@angular/core";
import {Article} from "../entities/Article";

@Pipe(
  {
    name: 'FilterCardsSearchInput',
    standalone: true
  }
)
export class FilterCardsSearchInputPipe implements PipeTransform {
  transform(cards: Article[], searchText: string): Article[] {
    if (!cards) return [];

    return cards.filter(card =>
      card.title.toLowerCase().includes(searchText.toLowerCase()) ||
      card.abstract.toLowerCase().includes(searchText.toLowerCase()) ||
      card.category.toLowerCase().includes(searchText.toLowerCase()) ||
      card.subtitle.toLowerCase().includes(searchText.toLowerCase())
    );
  }
}
