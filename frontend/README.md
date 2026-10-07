# Policy & Claims Tracker — Angular Frontend

Scaffold for the existing Spring Boot + SQL Server backend.

## Stack
- Angular 22 standalone components
- TypeScript
- HTML5
- CSS
- Angular HttpClient + Router
- No UI library yet

## Prerequisites
Angular 22 requires Node.js 22.22.3+ (or another version supported by Angular 22).

## Run
1. Start the backend first from the repository's `backend` folder.
2. From this frontend folder:

```bash
npm install
npm start
```

Open `http://localhost:4200`.

`npm start` uses `proxy.conf.json`, so browser calls to `/api/...` are proxied to `http://localhost:8080`.

## Included routes
- `/dashboard`
- `/policies`
- `/claims`
- `/reports/loss-ratio`

## API services already scaffolded
`ClaimService` includes search, get, history, file, startReview, approve, and reject.
`PolicyService` includes list, get, and create.
`ReportService` includes loss-ratio reporting.

## Suggested next frontend work
1. Claim details page + audit history
2. Claim workflow buttons: Start Review, Approve, Reject
3. File Claim form
4. Create Policy form
5. Reusable API error handling / toast messages
6. Form validation and unit tests

TypeScript is the source language used by Angular; Angular compiles it to browser JavaScript during the build.
