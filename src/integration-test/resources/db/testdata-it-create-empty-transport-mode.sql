-- Test data for empty transportMode verification
-- Only assistance_types that do NOT map to transportMode
-- This ensures jsonParameters.transportMode will be an empty array

-- FARDTJANST group: "Arbetsresor" -> type, "Rollator" -> mobilityAids, no transportMode mapping
INSERT INTO procapita_raw (id, personal_number, assistance_type, start_date, end_date, permit_group, party_id, status) VALUES (1, '199001011234', 'Arbetsresor', '2026-01-01', '2027-01-01', 'FARDTJANST', 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', 'PARTY_ID_FETCHED');
INSERT INTO procapita_raw (id, personal_number, assistance_type, start_date, end_date, permit_group, party_id, status) VALUES (2, '199001011234', 'Rollator',    '2026-01-01', '2027-01-01', 'FARDTJANST', 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', 'PARTY_ID_FETCHED');
