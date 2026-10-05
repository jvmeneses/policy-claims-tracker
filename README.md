# Policy & Claims Tracker

Spring Boot 3 + SQL Server (T-SQL) + Angular. Built collaboratively: the plumbing is done, the interesting parts are marked `TODO(you)`.

## Run
```bash
docker compose up -d            # SQL Server + creates `claimsdb`
cd backend && ./mvnw spring-boot:run   # or: mvn spring-boot:run
# Swagger: http://localhost:8080/swagger-ui.html
```

## Find your TODOs
```bash
grep -rn "TODO(you)" backend/src
```

## Suggested order
1. `V2__procedures.sql`  `usp_ApproveClaim`
2. `V3__views.sql`  `vw_PolicyLossRatio`
3. `V4__indexes.sql`, `V5__audit_trigger.sql`
4. `ClaimStatus.canTransitionTo`  then `ClaimService` methods
5. `ClaimService.search` (Specification), `ReportController`
6. Tests (`ClaimStatusTest`, `ClaimServiceTest`, `ClaimApprovalIT`)
7. Angular frontend (scaffold coming next)

## Design notes
_TODO(you): why the approval logic lives in T-SQL, trade-offs, what you'd do next._
