CREATE TABLE policyholder (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    full_name  NVARCHAR(150) NOT NULL,
    email      NVARCHAR(255) NOT NULL UNIQUE,
    phone      NVARCHAR(30)  NULL,
    created_at DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE policy (
    id               BIGINT IDENTITY(1,1) PRIMARY KEY,
    policy_number    NVARCHAR(30) NOT NULL UNIQUE,
    policyholder_id  BIGINT NOT NULL REFERENCES policyholder(id),
    type             NVARCHAR(10) NOT NULL CHECK (type IN ('AUTO','HOME','HEALTH')),
    premium_amount   DECIMAL(12,2) NOT NULL CHECK (premium_amount > 0),
    coverage_limit   DECIMAL(12,2) NOT NULL CHECK (coverage_limit > 0),
    start_date       DATE NOT NULL,
    end_date         DATE NOT NULL,
    status           NVARCHAR(10) NOT NULL CHECK (status IN ('ACTIVE','EXPIRED','CANCELLED')),
    CONSTRAINT ck_policy_dates CHECK (end_date > start_date)
);

CREATE TABLE claim (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    claim_number    NVARCHAR(30) NOT NULL UNIQUE,
    policy_id       BIGINT NOT NULL REFERENCES policy(id),
    description     NVARCHAR(1000) NOT NULL,
    claim_amount    DECIMAL(12,2) NOT NULL CHECK (claim_amount > 0),
    incident_date   DATE NOT NULL,
    filed_at        DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    status          NVARCHAR(15) NOT NULL CHECK (status IN ('SUBMITTED','UNDER_REVIEW','APPROVED','REJECTED')),
    approved_amount DECIMAL(12,2) NULL,
    reviewer_note   NVARCHAR(1000) NULL
);

CREATE TABLE claim_audit (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    claim_id   BIGINT NOT NULL REFERENCES claim(id),
    old_status NVARCHAR(15) NULL,
    new_status NVARCHAR(15) NOT NULL,
    changed_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    changed_by NVARCHAR(128) NOT NULL DEFAULT SUSER_SNAME()
);
