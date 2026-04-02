-- Test data for default type verification
-- Only assistance_types that do NOT map to a type value (only transportMode, mobilityAids, etc.)
-- This ensures jsonParameters.type will be defaulted

-- FARDTJANST group: "Buss" -> transportMode, "Rollator" -> mobilityAids, no type mapping
INSERT INTO procapita_raw (id, personal_number, assistance_type, start_date, end_date, permit_group, party_id, status) VALUES (1, '199001011234', 'Buss',     '2026-01-01', '2027-01-01', 'FARDTJANST', 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', 'PARTY_ID_FETCHED');
INSERT INTO procapita_raw (id, personal_number, assistance_type, start_date, end_date, permit_group, party_id, status) VALUES (2, '199001011234', 'Rollator', '2026-01-01', '2027-01-01', 'FARDTJANST', 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', 'PARTY_ID_FETCHED');

-- RIKSFARDTJANST group: "Flyg" -> transportMode, "Tåg" -> transportMode, no type mapping
INSERT INTO procapita_raw (id, personal_number, assistance_type, start_date, end_date, permit_group, party_id, status) VALUES (3, '199001011234', 'Flyg', '2026-03-01', '2027-03-01', 'RIKSFARDTJANST', 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', 'PARTY_ID_FETCHED');
INSERT INTO procapita_raw (id, personal_number, assistance_type, start_date, end_date, permit_group, party_id, status) VALUES (4, '199001011234', 'Tåg',  '2026-03-01', '2027-03-01', 'RIKSFARDTJANST', 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', 'PARTY_ID_FETCHED');
