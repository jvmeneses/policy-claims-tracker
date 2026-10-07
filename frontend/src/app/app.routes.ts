import { Routes } from '@angular/router';
import { Shell } from './layout/shell/shell';

export const routes: Routes = [
  {
    path: '',
    component: Shell,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard').then((m) => m.Dashboard)
      },
      {
        path: 'policies',
        loadComponent: () => import('./features/policies/policies-list').then((m) => m.PoliciesList)
      },
      {
        path: 'claims',
        loadComponent: () => import('./features/claims/claims-list').then((m) => m.ClaimsList)
      },
      {
        path: 'reports/loss-ratio',
        loadComponent: () => import('./features/reports/loss-ratio').then((m) => m.LossRatio)
      }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
