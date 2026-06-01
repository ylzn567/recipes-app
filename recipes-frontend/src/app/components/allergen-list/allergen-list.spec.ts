import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllergenList } from './allergen-list';

describe('AllergenList', () => {
  let component: AllergenList;
  let fixture: ComponentFixture<AllergenList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllergenList],
    }).compileComponents();

    fixture = TestBed.createComponent(AllergenList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
