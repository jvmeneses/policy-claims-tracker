import { Component, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe, PercentPipe } from '@angular/common';
import { LossRatioRow } from '../../core/models/report.model';
import { ReportService } from '../../core/services/report.service';

@Component({
  selector: 'app-loss-ratio',
  imports: [CurrencyPipe, PercentPipe],
  templateUrl: './loss-ratio.html',
  styleUrl: './loss-ratio.css'
})
export class LossRatio implements OnInit {
  private readonly service = inject(ReportService);

  readonly rows = signal<LossRatioRow[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  ngOnInit(): void {
    this.service.lossRatio().subscribe({
      next: (rows) => {
        this.rows.set(rows);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load loss-ratio report.');
        this.loading.set(false);
      }
    });
  }
}
