-- Person A: Needs partyId (3 rows)
INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group) VALUES (1, '199001011234', 'Arbetsresor', '12 mån', '2026-01-01', '2027-01-01', 'FARDTJANST');
INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group) VALUES (2, '199001011234', 'Buss', '12 mån', '2026-01-01', '2027-01-01', 'FARDTJANST');
INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group) VALUES (3, '199001011234', 'Generellt tillstånd', NULL, '2026-03-01', NULL, 'RIKSFARDTJANST');

-- Person B: Needs partyId (2 rows) - will get 404 in some tests
INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group) VALUES (4, '198505055678', 'Privatresor', '6 mån', '2026-02-01', '2026-08-01', 'FARDTJANST');
INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group) VALUES (5, '198505055678', 'Flyg', NULL, '2026-02-01', NULL, 'RIKSFARDTJANST');

-- Person C: Already has partyId (should be skipped)
INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, status) VALUES (6, '197212129999', 'Arbetsresor', '12 mån', '2025-06-01', '2026-06-01', 'FARDTJANST', 'already-has-party-id', 'PARTY_ID_FETCHED');
