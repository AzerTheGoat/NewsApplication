import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditionArticleComponent } from './article-edition.component';

describe('EditionArticleComponent', () => {
  let component: EditionArticleComponent;
  let fixture: ComponentFixture<EditionArticleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditionArticleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditionArticleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
