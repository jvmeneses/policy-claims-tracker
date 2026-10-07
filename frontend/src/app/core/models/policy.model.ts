export type PolicyType = 'AUTO' | 'HOME' | 'HEALTH';
export type PolicyStatus = 'ACTIVE' | 'EXPIRED' | 'CANCELLED';

export interface PolicyResponse {
  id: number;
  policyNumber: string;
  holderName: string;
  type: PolicyType;
  premiumAmount: number;
  coverageLimit: number;
  startDate: string;
  endDate: string;
  status: PolicyStatus;
}

export interface CreatePolicyRequest {
  policyholderId: number;
  type: PolicyType;
  premiumAmount: number;
  coverageLimit: number;
  startDate: string;
  endDate: string;
}
