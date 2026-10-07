export type ClaimStatus = 'SUBMITTED' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED';

export interface ClaimResponse {
  id: number;
  claimNumber: string;
  policyId: number;
  policyNumber: string;
  description: string;
  claimAmount: number;
  incidentDate: string;
  filedAt: string;
  status: ClaimStatus;
  approvedAmount: number | null;
  reviewerNote: string | null;
}

export interface FileClaimRequest {
  policyId: number;
  description: string;
  claimAmount: number;
  incidentDate: string;
}

export interface ApproveClaimRequest {
  approvedAmount: number;
  note?: string | null;
}

export interface RejectClaimRequest {
  note: string;
}

export interface AuditResponse {
  oldStatus: string | null;
  newStatus: string;
  changedAt: string;
  changedBy: string | null;
}

export interface ClaimSearchParams {
  status?: ClaimStatus;
  policyId?: number;
  incidentFrom?: string;
  incidentTo?: string;
  page?: number;
  size?: number;
}
