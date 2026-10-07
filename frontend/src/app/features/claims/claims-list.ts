import { Component, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClaimResponse, ClaimStatus } from '../../core/models/claim.model';
import { PageResponse } from '../../core/models/page.model';
import { ClaimService } from '../../core/services/claim.service';

@Component({
  selector: 'app-claims-list',
  imports: [CurrencyPipe, DatePipe, FormsModule],
  templateUrl: './claims-list.html',
  styleUrl: './claims-list.css'
})
export class ClaimsList implements OnInit {
  private readonly service = inject(ClaimService);

  readonly page = signal<PageResponse<ClaimResponse> | null>(null);
  readonly loading = signal(false);
  readonly error = signal('');

  status: ClaimStatus | '' = '';
  policyId: number | null = null;
  incidentFrom = '';
  incidentTo = '';

  ngOnInit(): void { this.load(); }

  load(pageNumber = 0): void {
    this.loading.set(true);
    this.error.set('');

    this.service.search({
      status: this.status || undefined,
      policyId: this.policyId ?? undefined,
      incidentFrom: this.incidentFrom || undefined,
      incidentTo: this.incidentTo || undefined,
      page: pageNumber,
      size: 10
    }).subscribe({
      next: (result) => {
        this.page.set(result);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load claims.');
        this.loading.set(false);
      }
    });
  }

  clearFilters(): void {
    this.status = '';
    this.policyId = null;
    this.incidentFrom = '';
    this.incidentTo = '';
    this.load();
  }
}
