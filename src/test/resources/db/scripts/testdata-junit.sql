-- Person 1: Has both FARDTJANST and RIKSFARDTJANST permits, no partyId yet
INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, party_asset_id, status)
VALUES (1, '199001011234', 'Arbetsresor', '12 mån', '2026-01-01', '2027-01-01', 'FARDTJANST', NULL, NULL, NULL);

INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, party_asset_id, status)
VALUES (2, '199001011234', 'Buss', '12 mån', '2026-01-01', '2027-01-01', 'FARDTJANST', NULL, NULL, NULL);

INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, party_asset_id, status)
VALUES (3, '199001011234', 'Rollator', '12 mån', '2026-01-01', '2027-01-01', 'FARDTJANST', NULL, NULL, NULL);

INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, party_asset_id, status)
VALUES (4, '199001011234', 'Färdtjänst med ledsagare', '12 mån', '2026-01-01', '2027-01-01', 'FARDTJANST', NULL, NULL, NULL);

INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, party_asset_id, status)
VALUES (5, '199001011234', 'Generellt tillstånd', NULL, '2026-03-01', NULL, 'RIKSFARDTJANST', NULL, NULL, NULL);

INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, party_asset_id, status)
VALUES (6, '199001011234', 'Flyg', NULL, '2026-03-01', NULL, 'RIKSFARDTJANST', NULL, NULL, NULL);

INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, party_asset_id, status)
VALUES (7, '199001011234', 'Riksfärdtjänst med ledsagare', NULL, '2026-03-01', NULL, 'RIKSFARDTJANST', NULL, NULL, NULL);

-- Person 2: Already has partyId, ready for PartyAssets creation
INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, party_asset_id, status)
VALUES (8, '198505055678', 'Privatresor', '6 mån', '2026-02-01', '2026-08-01', 'FARDTJANST', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', NULL, 'PARTY_ID_FETCHED');

INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, party_asset_id, status)
VALUES (9, '198505055678', 'Specialfordon', '6 mån', '2026-02-01', '2026-08-01', 'FARDTJANST', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', NULL, 'PARTY_ID_FETCHED');

INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, party_asset_id, status)
VALUES (10, '198505055678', 'Baksäte', '6 mån', '2026-02-01', '2026-08-01', 'FARDTJANST', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', NULL, 'PARTY_ID_FETCHED');

INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, party_asset_id, status)
VALUES (11, '198505055678', 'Vinterfärdtjänst', '6 mån', '2026-02-01', '2026-08-01', 'FARDTJANST', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', NULL, 'PARTY_ID_FETCHED');

-- Row with NULL personal_number (should be excluded from processing)
INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, party_asset_id, status)
VALUES (13, NULL, 'Arbetsresor', NULL, '2026-01-01', NULL, 'FARDTJANST', NULL, NULL, NULL);

-- Person 3: Already fully processed (has both partyId and partyAssetId)
INSERT INTO procapita_raw (id, personal_number, assistance_type, duration, start_date, end_date, permit_group, party_id, party_asset_id, status)
VALUES (12, '197212129999', 'Arbetsresor', '12 mån', '2025-06-01', '2026-06-01', 'FARDTJANST', 'f1f2f3f4-a5b6-7890-cdef-123456789abc', 'deed1234-5678-90ab-cdef-feeddeadbeef', 'ASSET_CREATED');
