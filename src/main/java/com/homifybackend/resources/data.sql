-- Init data for H2 Database
-- File này sẽ tự động chạy khi Spring Boot start

-- Insert test address
INSERT INTO addresses (street, city, province, nation, latitude, longitude) 
VALUES ('123 Test Street', 'Ho Chi Minh', 'Ho Chi Minh', 'Vietnam', 10.8231, 106.6297);

-- Insert test user (owner)
INSERT INTO users (full_name, phone_number, gender, role, address_id) 
VALUES ('Test Owner', '0901234567', 'Male', 'CUSTOMER', 1);

-- Insert test customer
INSERT INTO customers (user_id) VALUES (1);

-- Insert test agent user
INSERT INTO users (full_name, phone_number, gender, role, address_id) 
VALUES ('Test Agent', '0907654321', 'Female', 'AGENT', 1);

-- Insert test agent
INSERT INTO agents (user_id, license_id, bio, rate) 
VALUES (2, 'AGT001', 'Experienced real estate agent', 4.5);
