INSERT INTO policyholder (full_name, email, phone) VALUES
 ('Ana Reyes','ana@example.com','+639170000001'),
 ('Ben Cruz','ben@example.com','+639170000002'),
 ('Carla Santos','carla@example.com',NULL);

INSERT INTO policy (policy_number, policyholder_id, type, premium_amount, coverage_limit, start_date, end_date, status) VALUES
 ('POL-0001',1,'AUTO',1200.00,50000.00,'2026-01-01','2026-12-31','ACTIVE'),
 ('POL-0002',1,'HOME',2400.00,200000.00,'2026-02-01','2027-01-31','ACTIVE'),
 ('POL-0003',2,'HEALTH',1800.00,100000.00,'2026-01-01','2026-12-31','ACTIVE'),
 ('POL-0004',3,'AUTO',1000.00,30000.00,'2025-01-01','2025-12-31','EXPIRED');

INSERT INTO claim (claim_number, policy_id, description, claim_amount, incident_date, status, approved_amount) VALUES
 ('CLM-0001',1,'Rear-end collision',8000.00,'2026-03-10','APPROVED',7500.00),
 ('CLM-0002',1,'Windshield replacement',900.00,'2026-05-02','UNDER_REVIEW',NULL),
 ('CLM-0003',2,'Water damage, kitchen',15000.00,'2026-04-18','SUBMITTED',NULL),
 ('CLM-0004',3,'Hospital admission',42000.00,'2026-06-21','UNDER_REVIEW',NULL),
 ('CLM-0005',3,'Outpatient procedure',3000.00,'2026-02-14','REJECTED',NULL);
