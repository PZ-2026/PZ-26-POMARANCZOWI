CREATE TYPE user_role AS ENUM ('ADMIN', 'EMPLOYEE', 'CLIENT');
CREATE TYPE appointment_status AS ENUM ('BOOKED', 'CANCELLED', 'COMPLETED');

INSERT INTO "user" (name, email, phone, password_hash, role)
VALUES
('Admin User', 'admin@test.com', '111111111', 'hash1', 'ADMIN'),
('Barber One', 'barber1@test.com', '222222222', 'hash2', 'EMPLOYEE'),
('Client One', 'client1@test.com', '333333333', 'hash3', 'CLIENT');

INSERT INTO barber (user_id, specialization, bio)
VALUES
(2, 'Fade cuts', 'Experienced barber specializing in fades and modern cuts');

INSERT INTO service (name, description, duration_minutes, price, is_active)
VALUES
('Haircut', 'Basic haircut', 30, 50.00, TRUE),
('Beard Trim', 'Beard shaping', 20, 30.00, TRUE);

INSERT INTO barber_service (barber_id, service_id)
VALUES
(1, 1),
(1, 2);

INSERT INTO appointment (client_id, barber_id, start_time, end_time, status)
VALUES
(3, 1, '2025-01-10 10:00:00', '2025-01-10 10:30:00', 'BOOKED');

INSERT INTO appointment_service (appointment_id, service_id)
VALUES
(1, 1);

INSERT INTO availability (barber_id, day_of_week, start_time, end_time)
VALUES
(1, 1, '09:00', '17:00');