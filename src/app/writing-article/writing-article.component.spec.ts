import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WritingArticleComponent } from './writing-article.component';

describe('WritingArticleComponent', () => {
  let component: WritingArticleComponent;
  let fixture: ComponentFixture<WritingArticleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WritingArticleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WritingArticleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
