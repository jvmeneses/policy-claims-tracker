import { PolicyType } from './policy.model';

export interface LossRatioRow {
  policyId: number;
  policyNumber: string;
  type: PolicyType;
  premiumAmount: number;
  totalApproved: number;
  lossRatio: number;
  rankInType: number;
}
