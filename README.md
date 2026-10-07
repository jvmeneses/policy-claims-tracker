# Policy & Claims Tracker

A full-stack insurance policy and claims management application built with Spring Boot, SQL Server, and Angular.

The project demonstrates backend business-rule enforcement, claim status workflows, SQL Server stored procedures and views, audit tracking, filtering and pagination, and a typed Angular frontend.

## Tech Stack

### Backend
- Java 21
- Spring Boot 3
- Spring Data JPA
- Spring JDBC / `SimpleJdbcCall`
- SQL Server / T-SQL
- Flyway
- Maven
- Testcontainers
- JUnit 5

### Frontend
- Angular
- TypeScript
- HTML5
- CSS
- Angular Router
- Angular HttpClient

## Frontend TODO

The remaining frontend work is focused on completing the user workflows already supported by the backend.

### 1. Claim Details

- [ ] Create a claim details page
- [ ] Route claims using `/claims/:id`
- [ ] Display claim information:
  - claim number
  - policy number
  - description
  - claim amount
  - incident date
  - filed date
  - current status
  - approved amount
  - reviewer note
- [ ] Make claim rows in the Claims page open the details page

### 2. Claim Workflow Actions

Connect the UI to the existing claim workflow endpoints.

- [ ] Add **Start Review** action for `SUBMITTED` claims
- [ ] Add **Approve Claim** form for `UNDER_REVIEW` claims
- [ ] Add approved amount input
- [ ] Add optional reviewer note input
- [ ] Add **Reject Claim** form
- [ ] Require rejection reason
- [ ] Refresh claim data after a successful status change
- [ ] Hide or disable actions that are invalid for the current claim status

Backend endpoints:

```text
POST /api/claims/{id}/review
POST /api/claims/{id}/approve
POST /api/claims/{id}/reject
```
## Features

### Policies
- List and filter policies by status
- View policy details
- Create policies
- Validate policy dates and business rules

### Claims
- File a claim against an active policy
- Search and filter claims by status, policy, and incident date range
- Paginated claim results
- Claim workflow:
    - `SUBMITTED -> UNDER_REVIEW`
    - `UNDER_REVIEW -> APPROVED`
    - `UNDER_REVIEW -> REJECTED`
- Approve claims through a SQL Server stored procedure
- Enforce approval amount and policy coverage limits
- View claim status history

### Reporting
- Policy loss-ratio report backed by a SQL Server view
- Ranking of policies within each policy type

### Database
- Flyway-managed schema and seed data
- Stored procedure for claim approval
- Audit trigger for claim status changes
- SQL view for loss-ratio reporting
- Supporting indexes

## Project Structure

```text
policy-claims-tracker/
├── backend/                 # Spring Boot API
├── frontend/                # Angular application
├── docker-compose.yml       # SQL Server
└── README.md