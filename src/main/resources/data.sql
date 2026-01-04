-- Insert Customer (user_id=1)
INSERT INTO users (user_id, full_name, phone_number, registration_date, date_of_birth, gender, role) 
VALUES (1, 'Nguyen Van A', '0901234567', CURRENT_DATE, '1990-01-15', 'MALE', 'CUSTOMER');

INSERT INTO customers (user_id, pipeline_status, interest_score, is_favorite, demand) 
VALUES (1, 'ACTIVE', 80, true, 'Looking for apartment in HCM');

-- Insert Agent (user_id=2)
INSERT INTO users (user_id, full_name, phone_number, registration_date, date_of_birth, gender, role) 
VALUES (2, 'Tran Thi B', '0912345678', CURRENT_DATE, '1985-05-20', 'FEMALE', 'AGENT');

INSERT INTO agents (user_id, license_id, bio, rate) 
VALUES (2, 'AGT-2024-001', 'Experienced real estate agent with 10 years in market', 4.8);

-- Insert Agent (user_id=3) for frontend
INSERT INTO users (user_id, full_name, phone_number, registration_date, date_of_birth, gender, role) 
VALUES (3, 'Le Van C', '0923456789', CURRENT_DATE, '1988-03-10', 'MALE', 'AGENT');

INSERT INTO agents (user_id, license_id, bio, rate) 
VALUES (3, 'AGT-2024-002', 'Top performing agent specialized in luxury properties', 4.9);

-- Insert sample addresses
INSERT INTO addresses (address_id, street, city, province, nation, latitude, longitude) 
VALUES 
(100, '123 Nguyen Hue', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7769, 106.7009),
(101, '456 Le Loi', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7730, 106.6950),
(102, '789 Tran Hung Dao', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7680, 106.6920),
(103, '321 Vo Van Tan', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7810, 106.6890),
(104, '654 Pasteur', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7780, 106.6980),
(105, '987 Hai Ba Trung', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7750, 106.7020),
(106, '147 Cach Mang Thang 8', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7820, 106.6850),
(107, '258 Nguyen Thi Minh Khai', 'Ho Chi Minh City', 'Ho Chi Minh', 'Vietnam', 10.7870, 106.6990);

-- Insert sample properties
INSERT INTO properties (property_id, owner_id, address_id, year_built, floors, beds, baths, area, description, property_type)
VALUES
(100, 1, 100, 2020, 5, 3, 2, 120.5, 'Beautiful single house in District 1', 'SINGLE_HOUSE'),
(101, 1, 101, 2019, 3, 2, 2, 85.0, 'Modern apartment near city center', 'APARTMENT'),
(102, 1, 102, 2021, 4, 4, 3, 150.0, 'Spacious villa with garden', 'VILLA'),
(103, 1, 103, 2018, 3, 2, 2, 95.0, 'Cozy townhouse in quiet area', 'TOWN_HOUSE'),
(104, 1, 104, 2022, 2, 1, 1, 60.0, 'Studio apartment with city view', 'APARTMENT'),
(105, 1, 105, 2020, 4, 3, 2, 110.0, 'Family house with parking', 'SINGLE_HOUSE'),
(106, 1, 106, 2023, 5, 5, 4, 200.0, 'Luxury villa with pool', 'VILLA'),
(107, 1, 107, 2019, 3, 3, 2, 100.0, 'Modern townhouse near school', 'TOWN_HOUSE');

-- Insert subtype data for SINGLE_HOUSE properties (100, 105)
INSERT INTO single_houses (property_id, land_area, backyard_area, front_yard_area, has_garage, has_basement)
VALUES
(100, 150.0, 30.0, 20.0, true, false),
(105, 130.0, 25.0, 15.0, true, true);

-- Insert subtype data for APARTMENT properties (101, 104)
INSERT INTO apartments (property_id, usable_area, level, has_elevator_access, pet_allowed, balcony, total_building_floors)
VALUES
(101, 75.0, 5, true, false, true, 10),
(104, 55.0, 3, true, true, false, 8);

-- Insert subtype data for VILLA properties (102, 106)
INSERT INTO villas (property_id, lot_area, backyard_area, front_yard_area, garden_area, parking_spaces, has_garage, has_basement, view_type, smart_home_level)
VALUES
(102, 200.0, 50.0, 30.0, 40.0, 2, true, true, 'Garden', 1),
(106, 300.0, 80.0, 50.0, 60.0, 4, true, true, 'Ocean', 3);

-- Insert subtype data for TOWN_HOUSE properties (103, 107)
INSERT INTO town_houses (property_id, land_area, number_of_floors, corner_lot, front_width, depth)
VALUES
(103, 120.0, 3, false, 6.0, 20.0),
(107, 110.0, 3, true, 5.5, 18.0);

-- Insert sale listings with different statuses
INSERT INTO sale_listings (id, property_id, agent_id, current_price, estimate_value, sale_status, date_listed)
VALUES
(100, 100, 3, 5000000, 5200000, 'ACTIVE', CURRENT_DATE),
(101, 101, 3, 3500000, 3600000, 'ACTIVE', CURRENT_DATE),
(102, 102, 3, 8000000, 8200000, 'ACTIVE', CURRENT_DATE),
(103, 103, 3, 4200000, 4300000, 'ACTIVE', CURRENT_DATE),
(104, 104, 3, 2000000, 2100000, 'ACTIVE', CURRENT_DATE),
(105, 105, 3, 4500000, 4600000, 'PENDING', CURRENT_DATE),
(106, 106, 3, 12000000, 12500000, 'PENDING', CURRENT_DATE),
(107, 107, 3, 4000000, 4100000, 'DRAFT', CURRENT_DATE);
