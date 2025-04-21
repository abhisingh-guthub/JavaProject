-- Create database
CREATE DATABASE IF NOT EXISTS hospital_management;
USE hospital_management;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- Create patients table
CREATE TABLE IF NOT EXISTS patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    contact_number VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create doctors table
CREATE TABLE IF NOT EXISTS doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20),
    email VARCHAR(100),
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Create medical_records table
CREATE TABLE IF NOT EXISTS medical_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    record_date DATE NOT NULL,
    diagnosis TEXT,
    treatment TEXT,
    notes TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
);

-- Create appointments table
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    purpose VARCHAR(255),
    notes TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
);

-- Insert default admin user (username: admin, password: admin)
INSERT INTO users (username, password_hash, role)
VALUES ('admin', 'admin', 'ADMIN');

-- Insert sample doctors
INSERT INTO users (username, password_hash, role)
VALUES 
('doctor1', 'doctor1', 'DOCTOR'),
('doctor2', 'doctor2', 'DOCTOR');

INSERT INTO doctors (first_name, last_name, specialization, contact_number, email, user_id)
VALUES 
('John', 'Smith', 'Cardiology', '555-123-4567', 'john.smith@hospital.com', 2),
('Sarah', 'Johnson', 'Neurology', '555-987-6543', 'sarah.johnson@hospital.com', 3);

-- Insert sample patients
INSERT INTO patients (first_name, last_name, date_of_birth, gender, contact_number, email, address)
VALUES 
('Michael', 'Brown', '1985-04-12', 'Male', '555-111-2222', 'michael.brown@email.com', '123 Main St, Anytown'),
('Emily', 'Davis', '1990-08-22', 'Female', '555-333-4444', 'emily.davis@email.com', '456 Oak Ave, Somewhere'),
('Robert', 'Wilson', '1978-11-30', 'Male', '555-555-6666', 'robert.wilson@email.com', '789 Pine Rd, Nowhere'),
('Jennifer', 'Taylor', '1982-03-15', 'Female', '555-777-8888', 'jennifer.taylor@email.com', '321 Elm St, Anywhere');

-- Insert sample appointments
INSERT INTO appointments (patient_id, doctor_id, appointment_time, status, purpose)
VALUES 
(1, 1, '2023-06-15 10:00:00', 'Scheduled', 'Annual checkup'),
(2, 1, '2023-06-15 11:00:00', 'Scheduled', 'Follow-up'),
(3, 2, '2023-06-16 14:00:00', 'Scheduled', 'Consultation'),
(4, 2, '2023-06-16 15:00:00', 'Scheduled', 'New patient visit');

-- Insert sample medical records
INSERT INTO medical_records (patient_id, doctor_id, record_date, diagnosis, treatment, notes)
VALUES 
(1, 1, '2023-05-10', 'Hypertension', 'Prescribed Lisinopril 10mg daily', 'Blood pressure: 140/90'),
(2, 1, '2023-05-12', 'Migraine', 'Prescribed Sumatriptan as needed', 'Patient reports frequent headaches'),
(3, 2, '2023-05-15', 'Lower back pain', 'Physical therapy recommended', 'MRI shows mild disc degeneration'),
(4, 2, '2023-05-18', 'Anxiety disorder', 'Prescribed Sertraline 50mg daily', 'Patient reports increased stress at work');