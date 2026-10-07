import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import {
  ApproveClaimRequest,
  AuditResponse,
  ClaimResponse,
  ClaimSearchParams,
  FileClaimRequest,
  RejectClaimRequest
} from '../models/claim.model';
import { PageResponse } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class ClaimService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/claims`;

  search(filters: ClaimSearchParams = {}): Observable<PageResponse<ClaimResponse>> {
    let params = new HttpParams()
      .set('page', filters.page ?? 0)
      .set('size', filters.size ?? 20);

    if (filters.status) params = params.set('status', filters.status);
    if (filters.policyId != null) params = params.set('policyId', filters.policyId);
    if (filters.incidentFrom) params = params.set('incidentFrom', filters.incidentFrom);
    if (filters.incidentTo) params = params.set('incidentTo', filters.incidentTo);

    return this.http.get<PageResponse<ClaimResponse>>(this.baseUrl, { params });
  }

  get(id: number): Observable<ClaimResponse> {
    return this.http.get<ClaimResponse>(`${this.baseUrl}/${id}`);
  }

  history(id: number): Observable<AuditResponse[]> {
    return this.http.get<AuditResponse[]>(`${this.baseUrl}/${id}/history`);
  }

  file(request: FileClaimRequest): Observable<ClaimResponse> {
    return this.http.post<ClaimResponse>(this.baseUrl, request);
  }

  startReview(id: number): Observable<ClaimResponse> {
    return this.http.post<ClaimResponse>(`${this.baseUrl}/${id}/review`, {});
  }

  approve(id: number, request: ApproveClaimRequest): Observable<ClaimResponse> {
    return this.http.post<ClaimResponse>(`${this.baseUrl}/${id}/approve`, request);
  }

  reject(id: number, request: RejectClaimRequest): Observable<ClaimResponse> {
    return this.http.post<ClaimResponse>(`${this.baseUrl}/${id}/reject`, request);
  }
}
