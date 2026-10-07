import { Component, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ClaimService } from '../../core/services/claim.service';
import { PolicyService } from '../../core/services/policy.service';
import { ReportService } from '../../core/services/report.service';
import { ClaimResponse } from '../../core/models/claim.model';

@Component({
  selector: 'app-dashboard',
  imports: [CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  private readonly claims = inject(ClaimService);
  private readonly policies = inject(PolicyService);
  private readonly reports = inject(ReportService);

  readonly loading = signal(true);
  readonly error = signal('');
  readonly totalPolicies = signal(0);
  readonly totalClaims = signal(0);
  readonly reportRows = signal(0);
  readonly latestClaims = signal<ClaimResponse[]>([]);

  ngOnInit(): void {
    forkJoin({
      policies: this.policies.list(undefined, 0, 1),
      claims: this.claims.search({ page: 0, size: 5 }),
      report: this.reports.lossRatio()
    }).subscribe({
      next: ({ policies, claims, report }) => {
        this.totalPolicies.set(policies.totalElements);
        this.totalClaims.set(claims.totalElements);
        this.reportRows.set(report.length);
        this.latestClaims.set(claims.content);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not reach the backend. Make sure Spring Boot is running on port 8080.');
        this.loading.set(false);
      }
    });
  }
}
