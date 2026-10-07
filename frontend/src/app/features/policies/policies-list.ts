import { Component, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PageResponse } from '../../core/models/page.model';
import { PolicyResponse, PolicyStatus } from '../../core/models/policy.model';
import { PolicyService } from '../../core/services/policy.service';

@Component({
  selector: 'app-policies-list',
  imports: [CurrencyPipe, DatePipe, FormsModule],
  templateUrl: './policies-list.html',
  styleUrl: './policies-list.css'
})
export class PoliciesList implements OnInit {
  private readonly service = inject(PolicyService);

  readonly page = signal<PageResponse<PolicyResponse> | null>(null);
  readonly loading = signal(false);
  readonly error = signal('');
  status: PolicyStatus | '' = '';

  ngOnInit(): void { this.load(); }

  load(pageNumber = 0): void {
    this.loading.set(true);
    this.error.set('');
    this.service.list(this.status || undefined, pageNumber, 10).subscribe({
      next: (result) => {
        this.page.set(result);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load policies.');
        this.loading.set(false);
      }
    });
  }
}
