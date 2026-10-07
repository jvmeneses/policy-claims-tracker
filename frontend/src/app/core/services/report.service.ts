import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { LossRatioRow } from '../models/report.model';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly http = inject(HttpClient);

  lossRatio(): Observable<LossRatioRow[]> {
    return this.http.get<LossRatioRow[]>(`${API_BASE_URL}/reports/loss-ratio`);
  }
}
