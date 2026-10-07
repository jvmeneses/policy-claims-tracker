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
1. `PolicyService` checking of endDate implementation
2. Tests (`ClaimStatusTest`, `ClaimServiceTest`, `ClaimApprovalIT`)
3. Angular frontend (scaffold coming next)

## Design notes
_TODO(you): why the approval logic lives in T-SQL, trade-offs, what you'd do next._
