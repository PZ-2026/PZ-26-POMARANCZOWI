CREATE DATABASE barber_db;

CREATE TYPE user_role AS ENUM ('ADMIN', 'EMPLOYEE', 'CLIENT');
CREATE TYPE appointment_status AS ENUM ('BOOKED', 'CANCELLED', 'COMPLETED');


CREATE TABLE "user" (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    role user_role NOT NULL
);


CREATE TABLE barber (
    barber_id SERIAL PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    specialization VARCHAR(255),
    bio TEXT,
    CONSTRAINT fk_barber_user
        FOREIGN KEY (user_id) REFERENCES "user"(user_id)
        ON DELETE CASCADE
);


CREATE TABLE service (
    service_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);


CREATE TABLE appointment (
    appointment_id SERIAL PRIMARY KEY,
    client_id INT NOT NULL,
    barber_id INT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    status appointment_status NOT NULL,

    CONSTRAINT fk_appointment_client
        FOREIGN KEY (client_id) REFERENCES "user"(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_appointment_barber
        FOREIGN KEY (barber_id) REFERENCES barber(barber_id)
        ON DELETE CASCADE
);


CREATE TABLE availability (
    availability_id SERIAL PRIMARY KEY,
    barber_id INT NOT NULL,
    day_of_week INT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,

    CONSTRAINT fk_availability_barber
        FOREIGN KEY (barber_id) REFERENCES barber(barber_id)
        ON DELETE CASCADE
);

CREATE TABLE barber_service (
    id SERIAL PRIMARY KEY,
    barber_id INT NOT NULL,
    service_id INT NOT NULL,

    CONSTRAINT fk_bs_barber
        FOREIGN KEY (barber_id) REFERENCES barber(barber_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_bs_service
        FOREIGN KEY (service_id) REFERENCES service(service_id)
        ON DELETE CASCADE,

    CONSTRAINT unique_barber_service UNIQUE (barber_id, service_id)
);

CREATE TABLE appointment_service (
    id SERIAL PRIMARY KEY,
    appointment_id INT NOT NULL,
    service_id INT NOT NULL,

    CONSTRAINT fk_as_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_as_service
        FOREIGN KEY (service_id) REFERENCES service(service_id)
        ON DELETE CASCADE,

    CONSTRAINT unique_appointment_service UNIQUE (appointment_id, service_id)
);