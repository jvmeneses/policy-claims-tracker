import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { PageResponse } from '../models/page.model';
import { CreatePolicyRequest, PolicyResponse, PolicyStatus } from '../models/policy.model';

@Injectable({ providedIn: 'root' })
export class PolicyService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/policies`;

  list(status?: PolicyStatus, page = 0, size = 20): Observable<PageResponse<PolicyResponse>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (status) params = params.set('status', status);

    return this.http.get<PageResponse<PolicyResponse>>(this.baseUrl, { params });
  }

  get(id: number): Observable<PolicyResponse> {
    return this.http.get<PolicyResponse>(`${this.baseUrl}/${id}`);
  }

  create(request: CreatePolicyRequest): Observable<PolicyResponse> {
    return this.http.post<PolicyResponse>(this.baseUrl, request);
  }
}
