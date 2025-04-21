import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.*;

/**
 * Hospital Management System
 * A comprehensive application for managing hospital data including patients,
 * appointments, and user authentication.
 */
import java.util.List;
import java.util.ArrayList;

import javax.swing.plaf.ColorUIResource;

public class HospitalManagementSystem {
    // Main method - application entry point
    public static void main(String[] args) {
        // Set system properties for better rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Run application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Show splash screen
                showSplashScreen();
                
                // Create and show main frame
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error starting application: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
    
    /**
     * Show application splash screen
     */
    private static void showSplashScreen() {
        // Create splash screen frame
        JFrame splashFrame = new JFrame("Loading");
        splashFrame.setUndecorated(true);
        
        // Create splash panel
        JPanel splashPanel = new JPanel(new BorderLayout());
        splashPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        splashPanel.setBackground(new Color(18, 18, 18));
        
        // Add title
        JLabel titleLabel = new JLabel("Hospital Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        splashPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Add loading indicator
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(false);
        progressBar.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
        splashPanel.add(progressBar, BorderLayout.CENTER);
        
        // Add loading text
        JLabel loadingLabel = new JLabel("Loading...");
        loadingLabel.setForeground(Color.LIGHT_GRAY);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        splashPanel.add(loadingLabel, BorderLayout.SOUTH);
        
        // Configure splash frame
        splashFrame.add(splashPanel);
        splashFrame.pack();
        splashFrame.setLocationRelativeTo(null);
        splashFrame.setVisible(true);
        
        // Simulate loading time
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> splashFrame.dispose());
            }
        }).start();
    }
    
    //==========================================================================
    // DATABASE CONFIGURATION
    //==========================================================================
    
    /**
     * Database configuration and connection management
     */
    static class DatabaseConfig {
        private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
        private static DatabaseConfig instance;
        private Connection connection;
        
        // Database connection parameters - modify these to match your MySQL setup
        private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_management";
        private static final String DB_USER = "root";
        private static final String DB_PASSWORD = "PASSWORD";
        
        private DatabaseConfig() {
            // Private constructor for singleton pattern
        }
        
        public static synchronized DatabaseConfig getInstance() {
            if (instance == null) {
                instance = new DatabaseConfig();
            }
            return instance;
        }
        
        public Connection getConnection() throws SQLException {
            if (connection == null || connection.isClosed()) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                } catch (ClassNotFoundException e) {
                    LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
                    throw new SQLException("MySQL JDBC Driver not found", e);
                }
            }
            return connection;
        }
        
        public void closeConnection() {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing database connection", e);
                }
            }
        }
    }
    
    //==========================================================================
    // MODELS
    //==========================================================================
    
    /**
     * User model class
     */
    static class User {
        private int userId;
        private String username;
        private String passwordHash;
        private String role;
        private Date createdAt;
        private Date lastLogin;
        
        // Getters and setters
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
        
        public Date getLastLogin() { return lastLogin; }
        public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }
    }
    
    /**
     * Patient model class
     */
    static class Patient {
        private int patientId;
        private String firstName;
        private String lastName;
        private Date dateOfBirth;
        private String gender;
        private String contactNumber;
        private String email;
        private String address;
        private Date registrationDate;
        
        // Default constructor
        public Patient() {
            this.registrationDate = new Date();
        }
        
        // Parameterized constructor
        public Patient(String firstName, String lastName, Date dateOfBirth, 
                      String gender, String contactNumber, String email, String address) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.dateOfBirth = dateOfBirth;
            this.gender = gender;
            this.contactNumber = contactNumber;
            this.email = email;
            this.address = address;
            this.registrationDate = new Date();
        }
        
        // Getters and setters
        public int getPatientId() { return patientId; }
        public void setPatientId(int patientId) { this.patientId = patientId; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public Date getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        
        public String getContactNumber() { return contactNumber; }
        public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public Date getRegistrationDate() { return registrationDate; }
        public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
        
        @Override
        public String toString() {
            return firstName + " " + lastName;
        }
        
        // Calculate age based on date of birth
        public int getAge() {
            if (dateOfBirth == null) {
                return 0;
            }
            
            Date now = new Date();
            long diffInMillis = now.getTime() - dateOfBirth.getTime();
            return (int) (diffInMillis / (1000L * 60 * 60 * 24 * 365));
        }
    }
    
    /**
     * Appointment model class
     */
    static class Appointment {
        private int appointmentId;
        private int patientId;
        private int doctorId;
        private Date appointmentTime;
        private String status;
        private String purpose;
        private String notes;
        
        // Getters and setters
        public int getAppointmentId() { return appointmentId; }
        public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
        
        public int getPatientId() { return patientId; }
        public void setPatientId(int patientId) { this.patientId = patientId; }
        
        public int getDoctorId() { return doctorId; }
        public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
        
        public Date getAppointmentTime() { return appointmentTime; }
        public void setAppointmentTime(Date appointmentTime) { this.appointmentTime = appointmentTime; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
    
    /**
     * Disease model class
     */
    static class Disease {
        private int diseaseId;
        private String name;
        private String description;
        private String symptoms;
        private String treatment;
        
        // Default constructor
        public Disease() {
        }
        
        // Parameterized constructor
        public Disease(String name, String description, String symptoms, String treatment) {
            this.name = name;
            this.description = description;
            this.symptoms = symptoms;
            this.treatment = treatment;
        }
        
        // Getters and setters
        public int getDiseaseId() { return diseaseId; }
        public void setDiseaseId(int diseaseId) { this.diseaseId = diseaseId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getSymptoms() { return symptoms; }
        public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
        
        public String getTreatment() { return treatment; }
        public void setTreatment(String treatment) { this.treatment = treatment; }
        
        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * PatientDisease model class - represents a disease diagnosed for a patient
     */
    static class PatientDisease {
        private int patientDiseaseId;
        private int patientId;
        private int diseaseId;
        private Date diagnosisDate;
        private String notes;
        private String status; // Active, Recovered, Chronic, etc.
        
        // Default constructor
        public PatientDisease() {
            this.diagnosisDate = new Date();
        }
        
        // Parameterized constructor
        public PatientDisease(int patientId, int diseaseId, String notes, String status) {
            this.patientId = patientId;
            this.diseaseId = diseaseId;
            this.diagnosisDate = new Date();
            this.notes = notes;
            this.status = status;
        }
        
        // Getters and setters
        public int getPatientDiseaseId() { return patientDiseaseId; }
        public void setPatientDiseaseId(int patientDiseaseId) { this.patientDiseaseId = patientDiseaseId; }
        
        public int getPatientId() { return patientId; }
        public void setPatientId(int patientId) { this.patientId = patientId; }
        
        public int getDiseaseId() { return diseaseId; }
        public void setDiseaseId(int diseaseId) { this.diseaseId = diseaseId; }
        
        public Date getDiagnosisDate() { return diagnosisDate; }
        public void setDiagnosisDate(Date diagnosisDate) { this.diagnosisDate = diagnosisDate; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    //==========================================================================
    // REPOSITORIES (DATA ACCESS)
    //==========================================================================
    
    /**
     * User repository for database operations
     */
    static class UserRepository {
        private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());
        private final DatabaseConfig dbConfig;
        
        public UserRepository() {
            this.dbConfig = DatabaseConfig.getInstance();
        }
        
        /**
         * Find a user by username
         */
        public User findByUsername(String username) throws SQLException {
            String sql = "SELECT * FROM users WHERE username = ?";
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, username);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setUsername(rs.getString("username"));
                        user.setPasswordHash(rs.getString("password_hash"));
                        user.setRole(rs.getString("role"));
                        user.setCreatedAt(rs.getTimestamp("created_at"));
                        user.setLastLogin(rs.getTimestamp("last_login"));
                        return user;
                    } else {
                        return null;
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error finding user by username", e);
                throw e;
            }
        }
        
        /**
         * Update the last login time for a user
         */
        public boolean updateLastLogin(int userId) throws SQLException {
            String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, userId);
                
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error updating last login", e);
                throw e;
            }
        }
    }
    
    /**
     * Patient repository for database operations
     */
    static class PatientRepository {
        private static final Logger LOGGER = Logger.getLogger(PatientRepository.class.getName());
        private final DatabaseConfig dbConfig;
        
        public PatientRepository() {
            this.dbConfig = DatabaseConfig.getInstance();
        }
        
        /**
         * Save a new patient to the database
         */
        public Patient save(Patient patient) throws SQLException {
            String sql = "INSERT INTO patients (first_name, last_name, date_of_birth, gender, " +
                        "contact_number, email, address, registration_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setString(1, patient.getFirstName());
                stmt.setString(2, patient.getLastName());
                stmt.setDate(3, new java.sql.Date(patient.getDateOfBirth().getTime()));
                stmt.setString(4, patient.getGender());
                stmt.setString(5, patient.getContactNumber());
                stmt.setString(6, patient.getEmail());
                stmt.setString(7, patient.getAddress());
                stmt.setTimestamp(8, new Timestamp(patient.getRegistrationDate().getTime()));
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating patient failed, no rows affected.");
                }
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patient.setPatientId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating patient failed, no ID obtained.");
                    }
                }
                
                return patient;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error saving patient", e);
                throw e;
            }
        }
        
        /**
         * Update an existing patient in the database
         */
        public boolean update(Patient patient) throws SQLException {
            String sql = "UPDATE patients SET first_name = ?, last_name = ?, date_of_birth = ?, " +
                        "gender = ?, contact_number = ?, email = ?, address = ? " +
                        "WHERE patient_id = ?";
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, patient.getFirstName());
                stmt.setString(2, patient.getLastName());
                stmt.setDate(3, new java.sql.Date(patient.getDateOfBirth().getTime()));
                stmt.setString(4, patient.getGender());
                stmt.setString(5, patient.getContactNumber());
                stmt.setString(6, patient.getEmail());
                stmt.setString(7, patient.getAddress());
                stmt.setInt(8, patient.getPatientId());
                
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error updating patient", e);
                throw e;
            }
        }
        
        /**
         * Delete a patient from the database
         */
        public boolean delete(int patientId) throws SQLException {
            String sql = "DELETE FROM patients WHERE patient_id = ?";
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, patientId);
                
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error deleting patient", e);
                throw e;
            }
        }
        
        /**
         * Find a patient by ID
         */
        public Patient findById(int patientId) throws SQLException {
            String sql = "SELECT * FROM patients WHERE patient_id = ?";
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, patientId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToPatient(rs);
                    } else {
                        return null;
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error finding patient by ID", e);
                throw e;
            }
        }
        
        /**
         * Find all patients
         */
        public List<Patient> findAll() throws SQLException {
            String sql = "SELECT * FROM patients ORDER BY last_name, first_name";
            List<Patient> patients = new ArrayList<>();
            
            try (Connection conn = dbConfig.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
                
                return patients;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error finding all patients", e);
                throw e;
            }
        }
        
        /**
         * Search patients by name
         */
        public List<Patient> searchByName(String searchTerm) throws SQLException {
            String sql = "SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ? " +
                        "ORDER BY last_name, first_name";
            List<Patient> patients = new ArrayList<>();
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                String searchPattern = "%" + searchTerm + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        patients.add(mapResultSetToPatient(rs));
                    }
                }
                
                return patients;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error searching patients by name", e);
                throw e;
            }
        }
        
        /**
         * Map a database result set to a Patient object
         */
        private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
            Patient patient = new Patient();
            patient.setPatientId(rs.getInt("patient_id"));
            patient.setFirstName(rs.getString("first_name"));
            patient.setLastName(rs.getString("last_name"));
            patient.setDateOfBirth(rs.getDate("date_of_birth"));
            patient.setGender(rs.getString("gender"));
            patient.setContactNumber(rs.getString("contact_number"));
            patient.setEmail(rs.getString("email"));
            patient.setAddress(rs.getString("address"));
            patient.setRegistrationDate(rs.getTimestamp("registration_date"));
            return patient;
        }
    }
    
    /**
     * Disease repository for database operations
     */
    static class DiseaseRepository {
        private static final Logger LOGGER = Logger.getLogger(DiseaseRepository.class.getName());
        private final DatabaseConfig dbConfig;
        
        public DiseaseRepository() {
            this.dbConfig = DatabaseConfig.getInstance();
        }
        
        /**
         * Save a new disease to the database
         */
        public Disease save(Disease disease) throws SQLException {
            String sql = "INSERT INTO diseases (name, description, symptoms, treatment) " +
                    "VALUES (?, ?, ?, ?)";
        
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
                stmt.setString(1, disease.getName());
                stmt.setString(2, disease.getDescription());
                stmt.setString(3, disease.getSymptoms());
                stmt.setString(4, disease.getTreatment());
            
                int affectedRows = stmt.executeUpdate();
            
                if (affectedRows == 0) {
                    throw new SQLException("Creating disease failed, no rows affected.");
                }
            
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        disease.setDiseaseId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating disease failed, no ID obtained.");
                    }
                }
            
                return disease;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error saving disease", e);
                throw e;
            }
        }
    
        /**
         * Find all diseases
         */
        public List<Disease> findAll() throws SQLException {
            String sql = "SELECT * FROM diseases ORDER BY name";
            List<Disease> diseases = new ArrayList<>();
        
            try (Connection conn = dbConfig.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
            
                while (rs.next()) {
                    Disease disease = new Disease();
                    disease.setDiseaseId(rs.getInt("disease_id"));
                    disease.setName(rs.getString("name"));
                    disease.setDescription(rs.getString("description"));
                    disease.setSymptoms(rs.getString("symptoms"));
                    disease.setTreatment(rs.getString("treatment"));
                    diseases.add(disease);
                }
            
                return diseases;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error finding all diseases", e);
                throw e;
            }
        }
    
        /**
         * Find a disease by ID
         */
        public Disease findById(int diseaseId) throws SQLException {
            String sql = "SELECT * FROM diseases WHERE disease_id = ?";
        
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
            
                stmt.setInt(1, diseaseId);
            
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Disease disease = new Disease();
                        disease.setDiseaseId(rs.getInt("disease_id"));
                        disease.setName(rs.getString("name"));
                        disease.setDescription(rs.getString("description"));
                        disease.setSymptoms(rs.getString("symptoms"));
                        disease.setTreatment(rs.getString("treatment"));
                        return disease;
                    } else {
                        return null;
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error finding disease by ID", e);
                throw e;
            }
        }
    }

    /**
     * PatientDisease repository for database operations
     */
    static class PatientDiseaseRepository {
        private static final Logger LOGGER = Logger.getLogger(PatientDiseaseRepository.class.getName());
        private final DatabaseConfig dbConfig;
    
        public PatientDiseaseRepository() {
            this.dbConfig = DatabaseConfig.getInstance();
        }
    
        /**
         * Save a new patient disease to the database
         */
        public PatientDisease save(PatientDisease patientDisease) throws SQLException {
            String sql = "INSERT INTO patient_diseases (patient_id, disease_id, diagnosis_date, notes, status) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
                stmt.setInt(1, patientDisease.getPatientId());
                stmt.setInt(2, patientDisease.getDiseaseId());
                stmt.setTimestamp(3, new Timestamp(patientDisease.getDiagnosisDate().getTime()));
                stmt.setString(4, patientDisease.getNotes());
                stmt.setString(5, patientDisease.getStatus());
            
                int affectedRows = stmt.executeUpdate();
            
                if (affectedRows == 0) {
                    throw new SQLException("Creating patient disease failed, no rows affected.");
                }
            
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patientDisease.setPatientDiseaseId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating patient disease failed, no ID obtained.");
                    }
                }
            
                return patientDisease;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error saving patient disease", e);
                throw e;
            }
        }
    
        /**
         * Find all diseases for a patient
         */
        public List<Map<String, Object>> findByPatientId(int patientId) throws SQLException {
            String sql = "SELECT pd.*, d.name as disease_name, d.description, d.symptoms, d.treatment " +
                    "FROM patient_diseases pd " +
                    "JOIN diseases d ON pd.disease_id = d.disease_id " +
                    "WHERE pd.patient_id = ? " +
                    "ORDER BY pd.diagnosis_date DESC";
        
            List<Map<String, Object>> patientDiseases = new ArrayList<>();
        
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
            
                stmt.setInt(1, patientId);
            
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> record = new HashMap<>();
                        record.put("patientDiseaseId", rs.getInt("patient_disease_id"));
                        record.put("patientId", rs.getInt("patient_id"));
                        record.put("diseaseId", rs.getInt("disease_id"));
                        record.put("diagnosisDate", rs.getTimestamp("diagnosis_date"));
                        record.put("notes", rs.getString("notes"));
                        record.put("status", rs.getString("status"));
                        record.put("diseaseName", rs.getString("disease_name"));
                        record.put("description", rs.getString("description"));
                        record.put("symptoms", rs.getString("symptoms"));
                        record.put("treatment", rs.getString("treatment"));
                    
                        patientDiseases.add(record);
                    }
                }
            
                return patientDiseases;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error finding diseases for patient", e);
                throw e;
            }
        }
    
        /**
         * Delete a patient disease
         */
        public boolean delete(int patientDiseaseId) throws SQLException {
            String sql = "DELETE FROM patient_diseases WHERE patient_disease_id = ?";
        
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
            
                stmt.setInt(1, patientDiseaseId);
            
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error deleting patient disease", e);
                throw e;
            }
        }
    }
    
    //==========================================================================
    // SERVICES (BUSINESS LOGIC)
    //==========================================================================
    
    /**
     * Authentication service for user login and access control
     */
    static class AuthenticationService {
        private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());
        private final UserRepository userRepository;
        private User currentUser;
        
        private static AuthenticationService instance;
        
        private AuthenticationService() {
            this.userRepository = new UserRepository();
        }
        
        public static synchronized AuthenticationService getInstance() {
            if (instance == null) {
                instance = new AuthenticationService();
            }
            return instance;
        }
        
        /**
         * Authenticate a user with username and password
         */
        public boolean login(String username, String password) {
            try {
                User user = userRepository.findByUsername(username);
                
                if (user != null && verifyPassword(password, user.getPasswordHash())) {
                    currentUser = user;
                    userRepository.updateLastLogin(user.getUserId());
                    return true;
                }
                
                return false;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error during login", e);
                return false;
            }
        }
        
        /**
         * Verify a password against a hash
         * Note: This is a simplified implementation. In a real application,
         * you would use a proper password hashing library like BCrypt.
         */
        private boolean verifyPassword(String password, String passwordHash) {
            // For simplicity, we're using a basic comparison
            // In a real application, use a proper password hashing library
            return passwordHash.equals(password);
        }
        
        /**
         * Log out the current user
         */
        public void logout() {
            currentUser = null;
        }
        
        /**
         * Get the currently authenticated user
         */
        public User getCurrentUser() {
            return currentUser;
        }
        
        /**
         * Check if a user is currently authenticated
         */
        public boolean isAuthenticated() {
            return currentUser != null;
        }
        
        /**
         * Check if the current user has admin role
         */
        public boolean isAdmin() {
            return isAuthenticated() && "ADMIN".equals(currentUser.getRole());
        }
        
        /**
         * Check if the current user has doctor role
         */
        public boolean isDoctor() {
            return isAuthenticated() && "DOCTOR".equals(currentUser.getRole());
        }
        
        /**
         * Check if the current user has receptionist role
         */
        public boolean isReceptionist() {
            return isAuthenticated() && "RECEPTIONIST".equals(currentUser.getRole());
        }
        
        /**
         * Check if the current user has a specific role
         */
        public boolean hasRole(String role) {
            return isAuthenticated() && role.equals(currentUser.getRole());
        }
    }
    
    /**
     * Patient service for patient-related business logic
     */
    static class PatientService {
        private static final Logger LOGGER = Logger.getLogger(PatientService.class.getName());
        private final PatientRepository patientRepository;
        
        public PatientService() {
            this.patientRepository = new PatientRepository();
        }
        
        /**
         * Create a new patient
         */
        public Patient createPatient(Patient patient) throws Exception {
            // Validate patient data
            validatePatient(patient);
            
            try {
                return patientRepository.save(patient);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error creating patient", e);
                throw new Exception("Failed to create patient: " + e.getMessage(), e);
            }
        }
        
        /**
         * Update an existing patient
         */
        public boolean updatePatient(Patient patient) throws Exception {
            // Validate patient data
            validatePatient(patient);
            
            try {
                return patientRepository.update(patient);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error updating patient", e);
                throw new Exception("Failed to update patient: " + e.getMessage(), e);
            }
        }
        
        /**
         * Delete a patient
         */
        public boolean deletePatient(int patientId) throws Exception {
            try {
                return patientRepository.delete(patientId);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error deleting patient", e);
                throw new Exception("Failed to delete patient: " + e.getMessage(), e);
            }
        }
        
        /**
         * Get a patient by ID
         */
        public Patient getPatientById(int patientId) throws Exception {
            try {
                return patientRepository.findById(patientId);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error getting patient by ID", e);
                throw new Exception("Failed to get patient: " + e.getMessage(), e);
            }
        }
        
        /**
         * Get all patients
         */
        public List<Patient> getAllPatients() throws Exception {
            try {
                return patientRepository.findAll();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error getting all patients", e);
                throw new Exception("Failed to get patients: " + e.getMessage(), e);
            }
        }
        
        /**
         * Search patients by name
         */
        public List<Patient> searchPatientsByName(String searchTerm) throws Exception {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return getAllPatients();
            }
            
            try {
                return patientRepository.searchByName(searchTerm);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error searching patients by name", e);
                throw new Exception("Failed to search patients: " + e.getMessage(), e);
            }
        }
        
        /**
         * Validate patient data
         */
        private void validatePatient(Patient patient) throws Exception {
            if (patient == null) {
                throw new IllegalArgumentException("Patient cannot be null");
            }
            
            if (patient.getFirstName() == null || patient.getFirstName().trim().isEmpty()) {
                throw new Exception("First name is required");
            }
            
            if (patient.getLastName() == null || patient.getLastName().trim().isEmpty()) {
                throw new Exception("Last name is required");
            }
            
            if (patient.getDateOfBirth() == null) {
                throw new Exception("Date of birth is required");
            }
            
            if (patient.getGender() == null || patient.getGender().trim().isEmpty()) {
                throw new Exception("Gender is required");
            }
            
            if (patient.getContactNumber() != null && !patient.getContactNumber().trim().isEmpty()) {
                if (!isValidPhoneNumber(patient.getContactNumber())) {
                    throw new Exception("Invalid contact number format");
                }
            }
            
            if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
                if (!isValidEmail(patient.getEmail())) {
                    throw new Exception("Invalid email format");
                }
            }
        }
        
        /**
         * Validate phone number format
         */
        private boolean isValidPhoneNumber(String phoneNumber) {
            // Simple validation - can be enhanced as needed
            return phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}") || 
                   phoneNumber.matches("\\$\\d{3}\\$ \\d{3}-\\d{4}") ||
                   phoneNumber.matches("\\d{10}");
        }
        
        /**
         * Validate email format
         */
        private boolean isValidEmail(String email) {
            // Simple validation - can be enhanced as needed
            return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        }
    }

    /**
     * Disease service for disease-related business logic
     */
    static class DiseaseService {
        private static final Logger LOGGER = Logger.getLogger(DiseaseService.class.getName());
        private final DiseaseRepository diseaseRepository;
        private final PatientDiseaseRepository patientDiseaseRepository;
    
        public DiseaseService() {
            this.diseaseRepository = new DiseaseRepository();
            this.patientDiseaseRepository = new PatientDiseaseRepository();
        }
    
        /**
         * Get all diseases
         */
        public List<Disease> getAllDiseases() throws Exception {
            try {
                return diseaseRepository.findAll();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error getting all diseases", e);
                throw new Exception("Failed to get diseases: " + e.getMessage(), e);
            }
        }
    
        /**
         * Get a disease by ID
         */
        public Disease getDiseaseById(int diseaseId) throws Exception {
            try {
                return diseaseRepository.findById(diseaseId);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error getting disease by ID", e);
                throw new Exception("Failed to get disease: " + e.getMessage(), e);
            }
        }
    
        /**
         * Add a disease to a patient
         */
        public PatientDisease addDiseaseToPatient(PatientDisease patientDisease) throws Exception {
            try {
                return patientDiseaseRepository.save(patientDisease);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error adding disease to patient", e);
                throw new Exception("Failed to add disease to patient: " + e.getMessage(), e);
            }
        }
    
        /**
         * Get all diseases for a patient
         */
        public List<Map<String, Object>> getDiseasesForPatient(int patientId) throws Exception {
            try {
                return patientDiseaseRepository.findByPatientId(patientId);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error getting diseases for patient", e);
                throw new Exception("Failed to get diseases for patient: " + e.getMessage(), e);
            }
        }
    
        /**
         * Remove a disease from a patient
         */
        public boolean removeDiseaseFromPatient(int patientDiseaseId) throws Exception {
            try {
                return patientDiseaseRepository.delete(patientDiseaseId);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error removing disease from patient", e);
                throw new Exception("Failed to remove disease from patient: " + e.getMessage(), e);
            }
        }
    }
    
    //==========================================================================
    // UI COMPONENTS
    //==========================================================================
    
    /**
     * Color scheme for the dark theme
     */
    static class ColorScheme {
        // Main colors
        public static final Color BACKGROUND = new Color(18, 18, 18);
        public static final Color TEXT = new Color(230, 230, 230);
        
        // Input fields
        public static final Color INPUT_BACKGROUND = new Color(30, 30, 30);
        public static final Color SELECTION_BACKGROUND = new Color(0, 120, 215);
        public static final Color SELECTION_FOREGROUND = Color.WHITE;
        
        // Buttons
        public static final Color BUTTON_BACKGROUND = new Color(60, 60, 60);
        public static final Color BUTTON_TEXT = new Color(230, 230, 230);
        public static final Color BUTTON_SELECTED = new Color(80, 80, 80);
        public static final Color BUTTON_FOCUS = new Color(0, 120, 215, 100);
        
        // Tables
        public static final Color TABLE_BACKGROUND = new Color(25, 25, 25);
        public static final Color TABLE_HEADER_BACKGROUND = new Color(40, 40, 40);
        public static final Color TABLE_HEADER_TEXT = new Color(200, 200, 200);
        public static final Color TABLE_GRID = new Color(50, 50, 50);
        
        // Accent colors
        public static final Color PRIMARY = new Color(0, 120, 215);
        public static final Color SUCCESS = new Color(0, 180, 100);
        public static final Color WARNING = new Color(255, 180, 0);
        public static final Color ERROR = new Color(215, 0, 0);
    }
    
    /**
     * Animated button with smooth transitions
     */
    static class AnimatedButton extends JButton {
        private float alpha = 0.7f;
        private Color hoverColor;
        private Color normalColor;
        private Color pressedColor;
        private Color currentColor;
        private boolean isHovered = false;
        private boolean isPressed = false;
        
        private javax.swing.Timer fadeTimer;
        private int targetAlpha;
        
        public AnimatedButton(String text) {
            super(text);
        
            // Set button properties
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setForeground(ColorScheme.BUTTON_TEXT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        
            // Set colors
            normalColor = ColorScheme.BUTTON_BACKGROUND;
            hoverColor = ColorScheme.BUTTON_BACKGROUND.brighter();
            pressedColor = ColorScheme.BUTTON_SELECTED;
            currentColor = normalColor;
        
            // Create fade timer
            fadeTimer = new javax.swing.Timer(20, e -> {
                if (targetAlpha > alpha) {
                    alpha += 0.05f;
                    if (alpha >= targetAlpha) {
                        alpha = targetAlpha;
                        fadeTimer.stop();
                    }
                } else {
                    alpha -= 0.05f;
                    if (alpha <= targetAlpha) {
                        alpha = targetAlpha;
                        fadeTimer.stop();
                    }
                }
                repaint();
            });
        
            // Add mouse listeners for hover and press effects
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    currentColor = hoverColor;
                    targetAlpha = 1;
                    fadeTimer.start();
                }
            
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    currentColor = normalColor;
                    targetAlpha = (int) 0.7f;
                    fadeTimer.start();
                }
            
                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    currentColor = pressedColor;
                    repaint();
                }
            
                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    currentColor = isHovered ? hoverColor : normalColor;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
            // Set button color based on state
            if (isPressed) {
                g2d.setColor(pressedColor);
            } else {
                g2d.setColor(currentColor);
            }
        
            // Apply alpha transparency
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        
            // Draw rounded rectangle background
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        
            // Draw focus border if button has focus
            if (hasFocus()) {
                g2d.setColor(ColorScheme.BUTTON_FOCUS);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
            }
        
            // Draw text
            FontMetrics fm = g2d.getFontMetrics();
            Rectangle textRect = new Rectangle(0, 0, getWidth(), getHeight());
            String text = getText();
        
            int x = (textRect.width - fm.stringWidth(text)) / 2;
            int y = (textRect.height - fm.getHeight()) / 2 + fm.getAscent();
        
            g2d.setColor(ColorScheme.BUTTON_TEXT);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2d.drawString(text, x, y);
        
            g2d.dispose();
        }
        
        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            return new Dimension(size.width + 20, size.height + 10);
        }
    }

    /**
     * Navigation panel with animated buttons
     */
    static class NavigationPanel extends JPanel {
        private final MainFrame mainFrame;
        private final Map<String, AnimatedButton> buttons = new HashMap<>();
    
        public NavigationPanel(MainFrame mainFrame) {
            this.mainFrame = mainFrame;
        
            // Set layout
            setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
            setBackground(ColorScheme.BACKGROUND);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
            // Create navigation buttons
            createButton("Dashboard", e -> mainFrame.showDashboard());
            createButton("Patients", e -> mainFrame.showPatientPanel());
            createButton("Admin", e -> mainFrame.showAdminPanel());
            createButton("Logout", e -> mainFrame.logout());
        }
    
        /**
         * Create a navigation button
         */
        private void createButton(String text, ActionListener action) {
            AnimatedButton button = new AnimatedButton(text);
            button.addActionListener(action);
            buttons.put(text, button);
            add(button);
        }
    
        /**
         * Highlight the active button
         */
        public void setActiveButton(String buttonText) {
            for (Map.Entry<String, AnimatedButton> entry : buttons.entrySet()) {
                AnimatedButton button = entry.getValue();
                if (entry.getKey().equals(buttonText)) {
                    button.setForeground(ColorScheme.PRIMARY);
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, ColorScheme.PRIMARY),
                        BorderFactory.createEmptyBorder(0, 0, 2, 0)
                    ));
                } else {
                    button.setForeground(ColorScheme.BUTTON_TEXT);
                    button.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
                }
            }
        }
    }
    
    /**
     * Custom table with dark theme styling
     */
    static class CustomTable extends JTable {
        public CustomTable(DefaultTableModel model) {
            super(model);
            
            // Set table properties
            setShowGrid(true);
            setGridColor(ColorScheme.TABLE_GRID);
            setRowHeight(30);
            setIntercellSpacing(new Dimension(5, 5));
            setFillsViewportHeight(true);
            setSelectionBackground(ColorScheme.SELECTION_BACKGROUND);
            setSelectionForeground(ColorScheme.SELECTION_FOREGROUND);
            setBackground(ColorScheme.TABLE_BACKGROUND);
            setForeground(ColorScheme.TEXT);
            
            // Set header properties
            JTableHeader header = getTableHeader();
            header.setBackground(ColorScheme.TABLE_HEADER_BACKGROUND);
            header.setForeground(ColorScheme.TABLE_HEADER_TEXT);
            header.setFont(header.getFont().deriveFont(Font.BOLD));
            header.setReorderingAllowed(false);
            header.setResizingAllowed(true);
            
            // Set default cell renderer
            setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, 
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column);
                    
                    if (!isSelected) {
                        c.setBackground(row % 2 == 0 ? 
                                ColorScheme.TABLE_BACKGROUND : 
                                new Color(ColorScheme.TABLE_BACKGROUND.getRed() + 10, 
                                        ColorScheme.TABLE_BACKGROUND.getGreen() + 10, 
                                        ColorScheme.TABLE_BACKGROUND.getBlue() + 10));
                        c.setForeground(ColorScheme.TEXT);
                    }
                    
                    ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                    
                    return c;
                }
            });
        }
    }
    
    /**
     * Panel with fade-in animation
     */
    static class AnimatedPanel extends JPanel {
        private float alpha = 0.0f;
        private javax.swing.Timer fadeInTimer;
        private boolean isAnimating = false;
        
        public AnimatedPanel() {
            setOpaque(true);
            
            // Create fade-in animation timer
            fadeInTimer = new javax.swing.Timer(20, e -> {
                alpha += 0.05f;
                if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    fadeInTimer.stop();
                    isAnimating = false;
                }
                repaint();
            });
            
            // Start animation when panel becomes visible
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    startFadeInAnimation();
                }
            });
        }
        
        /**
         * Start the fade-in animation
         */
        public void startFadeInAnimation() {
            alpha = 0.0f;
            isAnimating = true;
            fadeInTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            
            // If animating, set alpha composite
            if (isAnimating) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            }
            
            super.paintComponent(g2d);
            g2d.dispose();
        }
    }
    
    //==========================================================================
    // UI SCREENS
    //==========================================================================
    
    /**
     * Main application frame
     */
    static class MainFrame extends JFrame {
        private final CardLayout cardLayout;
        private final JPanel contentPanel;
        private final LoginPanel loginPanel;
        private final DashboardPanel dashboardPanel;
        private final PatientPanel patientPanel;
        private final AdminPanel adminPanel;
        private final PatientDiseasePanel patientDiseasePanel;
        private final NavigationPanel navigationPanel;
        
        private final AuthenticationService authService;
        
        public MainFrame() {
            // Set application title
            super("Hospital Management System");
            
            // Apply dark theme
            applyDarkTheme();
            
            // Get authentication service
            authService = AuthenticationService.getInstance();

            // Initialize navigation panel
            navigationPanel = new NavigationPanel(this);
            
            // Set up the main frame
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1200, 800);
            setMinimumSize(new Dimension(800, 600));
            setLocationRelativeTo(null);
            
            // Create card layout for switching between panels
            cardLayout = new CardLayout();
            contentPanel = new JPanel(cardLayout);
            
            // Create panels
            loginPanel = new LoginPanel(this);
            dashboardPanel = new DashboardPanel(this);
            patientPanel = new PatientPanel(this);
            adminPanel = new AdminPanel(this);
            patientDiseasePanel = new PatientDiseasePanel(this);
            
            // Add panels to content panel
            contentPanel.add(loginPanel, "LOGIN");
            contentPanel.add(dashboardPanel, "DASHBOARD");
            contentPanel.add(patientPanel, "PATIENTS");
            contentPanel.add(adminPanel, "ADMIN");
            contentPanel.add(patientDiseasePanel, "PATIENT_DISEASES");
            
            // Add content panel to frame
            add(contentPanel);
            
            // Show login panel initially
            cardLayout.show(contentPanel, "LOGIN");
            
            // Add window listener to handle application close
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Perform cleanup operations
                    cleanup();
                }
            });
        }
        
        /**
         * Apply dark theme to the application
         */
        private void applyDarkTheme() {
            try {
                // Set look and feel to system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Override colors with dark theme
                UIManager.put("Panel.background", new ColorUIResource(ColorScheme.BACKGROUND));
                UIManager.put("OptionPane.background", new ColorUIResource(ColorScheme.BACKGROUND));
                UIManager.put("OptionPane.messageForeground", new ColorUIResource(ColorScheme.TEXT));
                
                UIManager.put("TextField.background", new ColorUIResource(ColorScheme.INPUT_BACKGROUND));
                UIManager.put("TextField.foreground", new ColorUIResource(ColorScheme.TEXT));
                UIManager.put("TextField.caretForeground", new ColorUIResource(ColorScheme.TEXT));
                UIManager.put("TextField.selectionBackground", new ColorUIResource(ColorScheme.SELECTION_BACKGROUND));
                UIManager.put("TextField.selectionForeground", new ColorUIResource(ColorScheme.SELECTION_FOREGROUND));
                
                UIManager.put("PasswordField.background", new ColorUIResource(ColorScheme.INPUT_BACKGROUND));
                UIManager.put("PasswordField.foreground", new ColorUIResource(ColorScheme.TEXT));
                UIManager.put("PasswordField.caretForeground", new ColorUIResource(ColorScheme.TEXT));
                UIManager.put("PasswordField.selectionBackground", new ColorUIResource(ColorScheme.SELECTION_BACKGROUND));
                UIManager.put("PasswordField.selectionForeground", new ColorUIResource(ColorScheme.SELECTION_FOREGROUND));
                
                UIManager.put("TextArea.background", new ColorUIResource(ColorScheme.INPUT_BACKGROUND));
                UIManager.put("TextArea.foreground", new ColorUIResource(ColorScheme.TEXT));
                UIManager.put("TextArea.caretForeground", new ColorUIResource(ColorScheme.TEXT));
                UIManager.put("TextArea.selectionBackground", new ColorUIResource(ColorScheme.SELECTION_BACKGROUND));
                UIManager.put("TextArea.selectionForeground", new ColorUIResource(ColorScheme.SELECTION_FOREGROUND));
                
                UIManager.put("ComboBox.background", new ColorUIResource(ColorScheme.INPUT_BACKGROUND));
                UIManager.put("ComboBox.foreground", new ColorUIResource(ColorScheme.TEXT));
                UIManager.put("ComboBox.selectionBackground", new ColorUIResource(ColorScheme.SELECTION_BACKGROUND));
                UIManager.put("ComboBox.selectionForeground", new ColorUIResource(ColorScheme.SELECTION_FOREGROUND));
                
                UIManager.put("Button.background", new ColorUIResource(ColorScheme.BUTTON_BACKGROUND));
                UIManager.put("Button.foreground", new ColorUIResource(ColorScheme.BUTTON_TEXT));
                UIManager.put("Button.select", new ColorUIResource(ColorScheme.BUTTON_SELECTED));
                UIManager.put("Button.focus", new ColorUIResource(ColorScheme.BUTTON_FOCUS));
                
                UIManager.put("Table.background", new ColorUIResource(ColorScheme.TABLE_BACKGROUND));
                UIManager.put("Table.foreground", new ColorUIResource(ColorScheme.TEXT));
                UIManager.put("Table.selectionBackground", new ColorUIResource(ColorScheme.SELECTION_BACKGROUND));
                UIManager.put("Table.selectionForeground", new ColorUIResource(ColorScheme.SELECTION_FOREGROUND));
                UIManager.put("Table.gridColor", new ColorUIResource(ColorScheme.TABLE_GRID));
                
                UIManager.put("TableHeader.background", new ColorUIResource(ColorScheme.TABLE_HEADER_BACKGROUND));
                UIManager.put("TableHeader.foreground", new ColorUIResource(ColorScheme.TABLE_HEADER_TEXT));
                
                UIManager.put("ScrollPane.background", new ColorUIResource(ColorScheme.BACKGROUND));
                UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
                
                UIManager.put("Label.foreground", new ColorUIResource(ColorScheme.TEXT));
                
                // Apply system-specific tweaks
                String osName = System.getProperty("os.name").toLowerCase();
                
                if (osName.contains("mac")) {
                    // Mac-specific tweaks
                    System.setProperty("apple.laf.useScreenMenuBar", "true");
                    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Hospital Management System");
                } else if (osName.contains("windows")) {
                    // Windows-specific tweaks
                    JFrame.setDefaultLookAndFeelDecorated(true);
                    JDialog.setDefaultLookAndFeelDecorated(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        /**
         * Show the dashboard panel after successful login
         */
        public void showDashboard() {
            navigationPanel.setActiveButton("Dashboard");
            cardLayout.show(contentPanel, "DASHBOARD");
        }
        
        /**
         * Show the patient management panel
         */
        public void showPatientPanel() {
            navigationPanel.setActiveButton("Patients");
            patientPanel.refreshData();
            cardLayout.show(contentPanel, "PATIENTS");
        }
        
        /**
         * Show the admin panel if the user has admin privileges
         */
        public void showAdminPanel() {
            if (authService.isAdmin()) {
                navigationPanel.setActiveButton("Admin");
                adminPanel.refreshData();
                cardLayout.show(contentPanel, "ADMIN");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "You do not have permission to access the admin panel.",
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
            }
        }

        /**
         * Show patient disease panel
         */
        public void showPatientDiseasePanel(Patient patient) {
            navigationPanel.setActiveButton("Patients");
            patientDiseasePanel.setPatient(patient);
            patientDiseasePanel.startFadeInAnimation();
            cardLayout.show(contentPanel, "PATIENT_DISEASES");
        }
        
        /**
         * Log out the current user and return to the login screen
         */
        public void logout() {
            authService.logout();
            cardLayout.show(contentPanel, "LOGIN");
            loginPanel.reset();
        }
        
        /**
         * Perform cleanup operations before application close
         */
        private void cleanup() {
            // Close database connections and perform other cleanup
            try {
                // Log out user if logged in
                if (authService.isAuthenticated()) {
                    authService.logout();
                }
                
                // Close database connection
                DatabaseConfig.getInstance().closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Login panel for user authentication
     */
    static class LoginPanel extends JPanel {
        private final MainFrame mainFrame;
        private final JTextField usernameField;
        private final JPasswordField passwordField;
        final JButton loginButton; // made final so it can be accessed if needed externally
        private final JLabel statusLabel;

        public LoginPanel(MainFrame mainFrame) {
            this.mainFrame = mainFrame;

            // Set layout
            setLayout(new GridBagLayout());
            setBackground(ColorScheme.BACKGROUND);

            // Create components
            JLabel titleLabel = new JLabel("Hospital Management System");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setForeground(ColorScheme.TEXT);

            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setForeground(ColorScheme.TEXT);

            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setForeground(ColorScheme.TEXT);

            usernameField = new JTextField(20);
            passwordField = new JPasswordField(20);

            loginButton = new AnimatedButton("Login");
            loginButton.addActionListener(e -> attemptLogin());

            statusLabel = new JLabel(" ");
            statusLabel.setForeground(Color.RED);

            // Add components to panel
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            add(titleLabel, gbc);

            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(30, 10, 10, 10);
            add(new JSeparator(), gbc);

            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.EAST;
            add(usernameLabel, gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            add(usernameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.anchor = GridBagConstraints.EAST;
            add(passwordLabel, gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            add(passwordField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            add(loginButton, gbc);

            gbc.gridy = 5;
            add(statusLabel, gbc);

            // Set default button when component is shown
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    if (getRootPane() != null) {
                        getRootPane().setDefaultButton(loginButton);
                    }
                }
            });
        }

        /**
         * Attempt to log in with the provided credentials
         */
        private void attemptLogin() {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Username and password are required");
                return;
            }

            // Show loading cursor
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // Disable login button during authentication
            loginButton.setEnabled(false);

            // Use SwingWorker to perform login in background
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    AuthenticationService authService = AuthenticationService.getInstance();
                    return authService.login(username, password);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            statusLabel.setText("");
                            mainFrame.showDashboard();
                        } else {
                            statusLabel.setText("Invalid username or password");
                        }
                    } catch (Exception e) {
                        statusLabel.setText("Login error: " + e.getMessage());
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                        loginButton.setEnabled(true);
                    }
                }
            };

            worker.execute();
        }

        /**
         * Reset the login form
         */
        public void reset() {
            usernameField.setText("");
            passwordField.setText("");
            statusLabel.setText("");
        }
    }
    
    /**
     * Dashboard panel shown after login
     */
    static class DashboardPanel extends AnimatedPanel {
        private final MainFrame mainFrame;
        
        public DashboardPanel(MainFrame mainFrame) {
            this.mainFrame = mainFrame;
            
            // Set layout
            setLayout(new BorderLayout());
            setBackground(ColorScheme.BACKGROUND);
            
            // Create top panel with navigation
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBackground(ColorScheme.BACKGROUND);
            topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Navigation buttons
            JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            navPanel.setBackground(ColorScheme.BACKGROUND);
            
            AnimatedButton patientsButton = new AnimatedButton("Patients");
            patientsButton.addActionListener(e -> mainFrame.showPatientPanel());
            
            AnimatedButton adminButton = new AnimatedButton("Admin");
            adminButton.addActionListener(e -> mainFrame.showAdminPanel());
            
            AnimatedButton logoutButton = new AnimatedButton("Logout");
            logoutButton.addActionListener(e -> mainFrame.logout());
            
            navPanel.add(patientsButton);
            navPanel.add(adminButton);
            navPanel.add(logoutButton);
            
            topPanel.add(navPanel, BorderLayout.WEST);
            
            // Create content panel
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBackground(ColorScheme.BACKGROUND);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Welcome message
            JLabel welcomeLabel = new JLabel("Welcome to Hospital Management System");
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
            welcomeLabel.setForeground(ColorScheme.TEXT);
            welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Dashboard content
            JPanel dashboardPanel = new JPanel(new GridLayout(2, 2, 20, 20));
            dashboardPanel.setBackground(ColorScheme.BACKGROUND);
            
            // Patient management card
            JPanel patientCard = createDashboardCard("Patient Management", 
                "Manage patient records, add new patients, update information", 
                e -> mainFrame.showPatientPanel());
            
            // Admin panel card
            JPanel adminCard = createDashboardCard("Administration", 
                "System settings, user management, and configuration", 
                e -> mainFrame.showAdminPanel());
            
            // Add cards to dashboard
            dashboardPanel.add(patientCard);
            dashboardPanel.add(adminCard);
            
            // Add components to content panel
            contentPanel.add(welcomeLabel, BorderLayout.NORTH);
            contentPanel.add(dashboardPanel, BorderLayout.CENTER);
            
            // Add panels to main panel
            add(topPanel, BorderLayout.NORTH);
            add(contentPanel, BorderLayout.CENTER);
        }
        
        /**
         * Create a dashboard card with title, description, and action
         */
        private JPanel createDashboardCard(String title, String description, ActionListener action) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(new Color(30, 30, 30));
            card.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 1));
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(ColorScheme.TEXT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
            
            JLabel descLabel = new JLabel("<html><p>" + description + "</p></html>");
            descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            descLabel.setForeground(new Color(200, 200, 200));
            descLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
            
            AnimatedButton openButton = new AnimatedButton("Open");
            openButton.addActionListener(action);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(new Color(30, 30, 30));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
            buttonPanel.add(openButton);
            
            card.add(titleLabel, BorderLayout.NORTH);
            card.add(descLabel, BorderLayout.CENTER);
            card.add(buttonPanel, BorderLayout.SOUTH);
            
            return card;
        }
    }
    
    /**
     * Patient management panel
     */
    static class PatientPanel extends AnimatedPanel {
        private final MainFrame mainFrame;
        private final PatientService patientService;
        
        private JTextField searchField;
        private CustomTable patientTable;
        private DefaultTableModel tableModel;
        
        public PatientPanel(MainFrame mainFrame) {
            this.mainFrame = mainFrame;
            this.patientService = new PatientService();
            
            initializeUI();
        }
        
        /**
         * Initialize the UI components
         */
        private void initializeUI() {
            setLayout(new BorderLayout());
            setBackground(ColorScheme.BACKGROUND);
            
            // Create top panel with navigation and search
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBackground(ColorScheme.BACKGROUND);
            topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Navigation buttons
            JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            navPanel.setBackground(ColorScheme.BACKGROUND);
            
            AnimatedButton dashboardButton = new AnimatedButton("Dashboard");
            dashboardButton.addActionListener(e -> mainFrame.showDashboard());
            
            AnimatedButton adminButton = new AnimatedButton("Admin");
            adminButton.addActionListener(e -> mainFrame.showAdminPanel());
            
            AnimatedButton logoutButton = new AnimatedButton("Logout");
            logoutButton.addActionListener(e -> mainFrame.logout());
            
            navPanel.add(dashboardButton);
            navPanel.add(adminButton);
            navPanel.add(logoutButton);
            
            // Search panel
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            searchPanel.setBackground(ColorScheme.BACKGROUND);
            
            JLabel searchLabel = new JLabel("Search:");
            searchLabel.setForeground(ColorScheme.TEXT);
            
            searchField = new JTextField(20);
            
            AnimatedButton searchButton = new AnimatedButton("Search");
            searchButton.addActionListener(e -> searchPatients());
            
            searchPanel.add(searchLabel);
            searchPanel.add(searchField);
            searchPanel.add(searchButton);
            
            topPanel.add(navPanel, BorderLayout.WEST);
            topPanel.add(searchPanel, BorderLayout.EAST);
            
            // Create title panel
            JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            titlePanel.setBackground(ColorScheme.BACKGROUND);
            titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            
            JLabel titleLabel = new JLabel("Patient Management");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            titleLabel.setForeground(ColorScheme.TEXT);
            
            titlePanel.add(titleLabel);
            
            // Create button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setBackground(ColorScheme.BACKGROUND);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            
            AnimatedButton addButton = new AnimatedButton("Add Patient");
            addButton.addActionListener(e -> showAddPatientDialog());
            
            AnimatedButton editButton = new AnimatedButton("Edit Patient");
            editButton.addActionListener(e -> {
                int selectedRow = patientTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int patientId = (int) tableModel.getValueAt(selectedRow, 0);
                    showEditPatientDialog(patientId);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Please select a patient to edit", 
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });
            
            AnimatedButton deleteButton = new AnimatedButton("Delete Patient");
            deleteButton.addActionListener(e -> {
                int selectedRow = patientTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int patientId = (int) tableModel.getValueAt(selectedRow, 0);
                    deletePatient(patientId);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Please select a patient to delete", 
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });

            AnimatedButton viewDiseasesButton = new AnimatedButton("View Diseases");
            viewDiseasesButton.addActionListener(e -> {
                int selectedRow = patientTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int patientId = (int) tableModel.getValueAt(selectedRow, 0);
                    try {
                        Patient patient = patientService.getPatientById(patientId);
                        mainFrame.showPatientDiseasePanel(patient);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Error loading patient: " + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Please select a patient to view diseases", 
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });

            buttonPanel.add(viewDiseasesButton);
            
            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            
            // Create table panel
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBackground(ColorScheme.BACKGROUND);
            tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            
            // Create table model with columns
            String[] columns = {"ID", "First Name", "Last Name", "Date of Birth", "Age", "Gender", "Contact", "Email"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
                
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0 || columnIndex == 4) {
                        return Integer.class; // ID and Age columns are integers
                    }
                    return String.class;
                }
            };
            
            // Create table
            patientTable = new CustomTable(tableModel);
            patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            // Add table to scroll pane
            JScrollPane scrollPane = new JScrollPane(patientTable);
            scrollPane.getViewport().setBackground(ColorScheme.BACKGROUND);
            
            tablePanel.add(scrollPane, BorderLayout.CENTER);
            
            // Add components to main panel
            add(topPanel, BorderLayout.NORTH);
            add(titlePanel, BorderLayout.NORTH);
            add(buttonPanel, BorderLayout.NORTH);
            add(tablePanel, BorderLayout.CENTER);
            
            // Load initial data
            refreshData();
        }
        
        /**
         * Refresh the patient data in the table
         */
        public void refreshData() {
            try {
                // Clear existing data
                tableModel.setRowCount(0);
                
                // Get all patients
                List<Patient> patients = patientService.getAllPatients();
                
                // Add patients to table
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (Patient patient : patients) {
                    Object[] row = {
                        patient.getPatientId(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        dateFormat.format(patient.getDateOfBirth()),
                        patient.getAge(),
                        patient.getGender(),
                        patient.getContactNumber(),
                        patient.getEmail()
                    };
                    tableModel.addRow(row);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading patients: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        /**
         * Search for patients by name
         */
        private void searchPatients() {
            try {
                String searchTerm = searchField.getText().trim();
                
                // Clear existing data
                tableModel.setRowCount(0);
                
                // Search patients
                List<Patient> patients = patientService.searchPatientsByName(searchTerm);
                
                // Add patients to table
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (Patient patient : patients) {
                    Object[] row = {
                        patient.getPatientId(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        dateFormat.format(patient.getDateOfBirth()),
                        patient.getAge(),
                        patient.getGender(),
                        patient.getContactNumber(),
                        patient.getEmail()
                    };
                    tableModel.addRow(row);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error searching patients: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        /**
         * Show dialog to add a new patient
         */
        private void showAddPatientDialog() {
            // Create patient form dialog
            JDialog dialog = new JDialog(mainFrame, "Add New Patient", true);
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(mainFrame);
            dialog.setLayout(new BorderLayout());
            
            // Create form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(ColorScheme.BACKGROUND);
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // First Name
            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel firstNameLabel = new JLabel("First Name:");
            firstNameLabel.setForeground(ColorScheme.TEXT);
            formPanel.add(firstNameLabel, gbc);
            
            gbc.gridx = 1;
            JTextField firstNameField = new JTextField(20);
            formPanel.add(firstNameField, gbc);
            
            // Last Name
            gbc.gridx = 0;
            gbc.gridy = 1;
            JLabel lastNameLabel = new JLabel("Last Name:");
            lastNameLabel.setForeground(ColorScheme.TEXT);
            formPanel.add(lastNameLabel, gbc);
            
            gbc.gridx = 1;
            JTextField lastNameField = new JTextField(20);
            formPanel.add(lastNameField, gbc);
            
            // Date of Birth
            gbc.gridx = 0;
            gbc.gridy = 2;
            JLabel dobLabel = new JLabel("Date of Birth (yyyy-MM-dd):");
            dobLabel.setForeground(ColorScheme.TEXT);
            formPanel.add(dobLabel, gbc);
            
            gbc.gridx = 1;
            JTextField dobField = new JTextField(20);
            formPanel.add(dobField, gbc);
            
            // Gender
            gbc.gridx = 0;
            gbc.gridy = 3;
            JLabel genderLabel = new JLabel("Gender:");
            genderLabel.setForeground(ColorScheme.TEXT);
            formPanel.add(genderLabel, gbc);
            
            gbc.gridx = 1;
            String[] genders = {"Male", "Female", "Other"};
            JComboBox<String> genderComboBox = new JComboBox<>(genders);
            formPanel.add(genderComboBox, gbc);
            
            // Contact Number
            gbc.gridx = 0;
            gbc.gridy = 4;
            JLabel contactLabel = new JLabel("Contact Number:");
            contactLabel.setForeground(ColorScheme.TEXT);
            formPanel.add(contactLabel, gbc);
            
            gbc.gridx = 1;
            JTextField contactField = new JTextField(20);
            formPanel.add(contactField, gbc);
            
            // Email
            gbc.gridx = 0;
            gbc.gridy = 5;
            JLabel emailLabel = new JLabel("Email:");
            emailLabel.setForeground(ColorScheme.TEXT);
            formPanel.add(emailLabel, gbc);
            
            gbc.gridx = 1;
            JTextField emailField = new JTextField(20);
            formPanel.add(emailField, gbc);
            
            // Address
            gbc.gridx = 0;
            gbc.gridy = 6;
            JLabel addressLabel = new JLabel("Address:");
            addressLabel.setForeground(ColorScheme.TEXT);
            formPanel.add(addressLabel, gbc);
            
            gbc.gridx = 1;
            JTextArea addressArea = new JTextArea(3, 20);
            JScrollPane addressScrollPane = new JScrollPane(addressArea);
            formPanel.add(addressScrollPane, gbc);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(ColorScheme.BACKGROUND);
            
            AnimatedButton saveButton = new AnimatedButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Validate and parse input
                        String firstName = firstNameField.getText().trim();
                        String lastName = lastNameField.getText().trim();
                        String dobString = dobField.getText().trim();
                        String gender = (String) genderComboBox.getSelectedItem();
                        String contact = contactField.getText().trim();
                        String email = emailField.getText().trim();
                        String address = addressArea.getText().trim();
                        
                        // Validate required fields
                        if (firstName.isEmpty() || lastName.isEmpty() || dobString.isEmpty()) {
                            JOptionPane.showMessageDialog(dialog, 
                                "First name, last name, and date of birth are required", 
                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Parse date of birth
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date dob;
                        try {
                            dob = dateFormat.parse(dobString);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(dialog, 
                                "Invalid date format. Please use yyyy-MM-dd", 
                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Create patient object
                        Patient patient = new Patient(firstName, lastName, dob, gender, contact, email, address);
                        
                        // Save patient
                        patientService.createPatient(patient);
                        
                        // Close dialog
                        dialog.dispose();
                        
                        // Refresh data
                        refreshData();
                        
                        // Show success message
                        JOptionPane.showMessageDialog(mainFrame, 
                            "Patient added successfully", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Error adding patient: " + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            AnimatedButton cancelButton = new AnimatedButton("Cancel");
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            // Add panels to dialog
            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            // Show dialog
            dialog.setVisible(true);
        }
        
        /**
         * Show dialog to edit an existing patient
         */
        private void showEditPatientDialog(int patientId) {
            try {
                // Get patient by ID
                Patient patient = patientService.getPatientById(patientId);
                
                if (patient == null) {
                    JOptionPane.showMessageDialog(this, 
                        "Patient not found", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create patient form dialog
                JDialog dialog = new JDialog(mainFrame, "Edit Patient", true);
                dialog.setSize(500, 400);
                dialog.setLocationRelativeTo(mainFrame);
                dialog.setLayout(new BorderLayout());
                
                // Create form panel
                JPanel formPanel = new JPanel(new GridBagLayout());
                formPanel.setBackground(ColorScheme.BACKGROUND);
                formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.anchor = GridBagConstraints.WEST;
                
                // First Name
                gbc.gridx = 0;
                gbc.gridy = 0;
                JLabel firstNameLabel = new JLabel("First Name:");
                firstNameLabel.setForeground(ColorScheme.TEXT);
                formPanel.add(firstNameLabel, gbc);
                
                gbc.gridx = 1;
                JTextField firstNameField = new JTextField(20);
                firstNameField.setText(patient.getFirstName());
                formPanel.add(firstNameField, gbc);
                
                // Last Name
                gbc.gridx = 0;
                gbc.gridy = 1;
                JLabel lastNameLabel = new JLabel("Last Name:");
                lastNameLabel.setForeground(ColorScheme.TEXT);
                formPanel.add(lastNameLabel, gbc);
                
                gbc.gridx = 1;
                JTextField lastNameField = new JTextField(20);
                lastNameField.setText(patient.getLastName());
                formPanel.add(lastNameField, gbc);
                
                // Date of Birth
                gbc.gridx = 0;
                gbc.gridy = 2;
                JLabel dobLabel = new JLabel("Date of Birth (yyyy-MM-dd):");
                dobLabel.setForeground(ColorScheme.TEXT);
                formPanel.add(dobLabel, gbc);
                
                gbc.gridx = 1;
                JTextField dobField = new JTextField(20);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dobField.setText(dateFormat.format(patient.getDateOfBirth()));
                formPanel.add(dobField, gbc);
                
                // Gender
                gbc.gridx = 0;
                gbc.gridy = 3;
                JLabel genderLabel = new JLabel("Gender:");
                genderLabel.setForeground(ColorScheme.TEXT);
                formPanel.add(genderLabel, gbc);
                
                gbc.gridx = 1;
                String[] genders = {"Male", "Female", "Other"};
                JComboBox<String> genderComboBox = new JComboBox<>(genders);
                genderComboBox.setSelectedItem(patient.getGender());
                formPanel.add(genderComboBox, gbc);
                
                // Contact Number
                gbc.gridx = 0;
                gbc.gridy = 4;
                JLabel contactLabel = new JLabel("Contact Number:");
                contactLabel.setForeground(ColorScheme.TEXT);
                formPanel.add(contactLabel, gbc);
                
                gbc.gridx = 1;
                JTextField contactField = new JTextField(20);
                contactField.setText(patient.getContactNumber());
                formPanel.add(contactField, gbc);
                
                // Email
                gbc.gridx = 0;
                gbc.gridy = 5;
                JLabel emailLabel = new JLabel("Email:");
                emailLabel.setForeground(ColorScheme.TEXT);
                formPanel.add(emailLabel, gbc);
                
                gbc.gridx = 1;
                JTextField emailField = new JTextField(20);
                emailField.setText(patient.getEmail());
                formPanel.add(emailField, gbc);
                
                // Address
                gbc.gridx = 0;
                gbc.gridy = 6;
                JLabel addressLabel = new JLabel("Address:");
                addressLabel.setForeground(ColorScheme.TEXT);
                formPanel.add(addressLabel, gbc);
                
                gbc.gridx = 1;
                JTextArea addressArea = new JTextArea(3, 20);
                addressArea.setText(patient.getAddress());
                JScrollPane addressScrollPane = new JScrollPane(addressArea);
                formPanel.add(addressScrollPane, gbc);
                
                // Button panel
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setBackground(ColorScheme.BACKGROUND);
                
                AnimatedButton saveButton = new AnimatedButton("Save");
                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            // Validate and parse input
                            String firstName = firstNameField.getText().trim();
                            String lastName = lastNameField.getText().trim();
                            String dobString = dobField.getText().trim();
                            String gender = (String) genderComboBox.getSelectedItem();
                            String contact = contactField.getText().trim();
                            String email = emailField.getText().trim();
                            String address = addressArea.getText().trim();
                            
                            // Validate required fields
                            if (firstName.isEmpty() || lastName.isEmpty() || dobString.isEmpty()) {
                                JOptionPane.showMessageDialog(dialog, 
                                    "First name, last name, and date of birth are required", 
                                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            
                            // Parse date of birth
                            Date dob;
                            try {
                                dob = dateFormat.parse(dobString);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(dialog, 
                                    "Invalid date format. Please use yyyy-MM-dd", 
                                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            
                            // Update patient object
                            patient.setFirstName(firstName);
                            patient.setLastName(lastName);
                            patient.setDateOfBirth(dob);
                            patient.setGender(gender);
                            patient.setContactNumber(contact);
                            patient.setEmail(email);
                            patient.setAddress(address);
                            
                            // Save patient
                            patientService.updatePatient(patient);
                            
                            // Close dialog
                            dialog.dispose();
                            
                            // Refresh data
                            refreshData();
                            
                            // Show success message
                            JOptionPane.showMessageDialog(mainFrame, 
                                "Patient updated successfully", 
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(dialog, 
                                "Error updating patient: " + ex.getMessage(), 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                
                AnimatedButton cancelButton = new AnimatedButton("Cancel");
                cancelButton.addActionListener(e -> dialog.dispose());
                
                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);
                
                // Add panels to dialog
                dialog.add(formPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                
                // Show dialog
                dialog.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading patient: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        /**
         * Delete a patient
         */
        private void deletePatient(int patientId) {
            try {
                // Confirm deletion
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete this patient?", 
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Delete patient
                    boolean success = patientService.deletePatient(patientId);
                    
                    if (success) {
                        // Refresh data
                        refreshData();
                        
                        // Show success message
                        JOptionPane.showMessageDialog(this, 
                            "Patient deleted successfully", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to delete patient", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting patient: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Panel for managing patient diseases
     */
    static class PatientDiseasePanel extends AnimatedPanel {
        private final MainFrame mainFrame;
        private final DiseaseService diseaseService;
        private final PatientService patientService;
    
        private Patient currentPatient;
        private JTable diseaseTable;
        private DefaultTableModel tableModel;
    
        public PatientDiseasePanel(MainFrame mainFrame) {
            this.mainFrame = mainFrame;
            this.diseaseService = new DiseaseService();
            this.patientService = new PatientService();
        
            initializeUI();
        }
    
        /**
         * Initialize the UI components
         */
        private void initializeUI() {
            setLayout(new BorderLayout());
            setBackground(ColorScheme.BACKGROUND);
        
            // Create title panel
            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setBackground(ColorScheme.BACKGROUND);
            titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
            JLabel titleLabel = new JLabel("Patient Diseases");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            titleLabel.setForeground(ColorScheme.TEXT);
        
            JLabel patientLabel = new JLabel();
            patientLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            patientLabel.setForeground(ColorScheme.TEXT);
        
            titlePanel.add(titleLabel, BorderLayout.WEST);
            titlePanel.add(patientLabel, BorderLayout.EAST);
        
            // Create button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setBackground(ColorScheme.BACKGROUND);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
            AnimatedButton addButton = new AnimatedButton("Add Disease");
            addButton.addActionListener(e -> showAddDiseaseDialog());
        
            AnimatedButton removeButton = new AnimatedButton("Remove Disease");
            removeButton.addActionListener(e -> {
                int selectedRow = diseaseTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int patientDiseaseId = (int) tableModel.getValueAt(selectedRow, 0);
                    removeDiseaseFromPatient(patientDiseaseId);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Please select a disease to remove", 
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });
        
            AnimatedButton backButton = new AnimatedButton("Back to Patients");
            backButton.addActionListener(e -> mainFrame.showPatientPanel());
        
            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);
            buttonPanel.add(backButton);
        
            // Create table panel
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBackground(ColorScheme.BACKGROUND);
            tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
            // Create table model with columns
            String[] columns = {"ID", "Disease", "Diagnosis Date", "Status", "Notes"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) {
                        return Integer.class; // ID column is integer
                    }
                    return String.class;
                }
            };
        
            // Create table
            diseaseTable = new CustomTable(tableModel);
            diseaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
            // Add table to scroll pane
            JScrollPane scrollPane = new JScrollPane(diseaseTable);
            scrollPane.getViewport().setBackground(ColorScheme.BACKGROUND);
        
            tablePanel.add(scrollPane, BorderLayout.CENTER);
        
            // Add components to main panel
            add(titlePanel, BorderLayout.NORTH);
            add(buttonPanel, BorderLayout.NORTH);
            add(tablePanel, BorderLayout.CENTER);
        }
    
        /**
         * Set the current patient and load their diseases
         */
        public void setPatient(Patient patient) {
            this.currentPatient = patient;
        
            // Update patient label
            JLabel patientLabel = (JLabel) ((JPanel) getComponent(0)).getComponent(1);
            patientLabel.setText("Patient: " + patient.getFirstName() + " " + patient.getLastName());
        
            // Load patient diseases
            loadPatientDiseases();
        }
    
        /**
         * Load diseases for the current patient
         */
        private void loadPatientDiseases() {
            try {
                // Clear existing data
                tableModel.setRowCount(0);
            
                // Get diseases for patient
                List<Map<String, Object>> patientDiseases = diseaseService.getDiseasesForPatient(currentPatient.getPatientId());
            
                // Add diseases to table
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (Map<String, Object> record : patientDiseases) {
                    Object[] row = {
                        record.get("patientDiseaseId"),
                        record.get("diseaseName"),
                        dateFormat.format((Date) record.get("diagnosisDate")),
                        record.get("status"),
                        record.get("notes")
                    };
                    tableModel.addRow(row);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading patient diseases: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        /**
         * Show dialog to add a disease to the patient
         */
        private void showAddDiseaseDialog() {
            try {
                // Get all diseases
                List<Disease> diseases = diseaseService.getAllDiseases();
            
                if (diseases.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "No diseases available. Please add diseases first.", 
                        "No Diseases", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            
                // Create dialog
                JDialog dialog = new JDialog(mainFrame, "Add Disease to Patient", true);
                dialog.setSize(500, 400);
                dialog.setLocationRelativeTo(mainFrame);
                dialog.setLayout(new BorderLayout());
            
                // Create form panel
                JPanel formPanel = new JPanel(new GridBagLayout());
                formPanel.setBackground(ColorScheme.BACKGROUND);
                formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.anchor = GridBagConstraints.WEST;
            
                // Disease
                gbc.gridx = 0;
                gbc.gridy = 0;
                JLabel diseaseLabel = new JLabel("Disease:");
                diseaseLabel.setForeground(ColorScheme.TEXT);
                formPanel.add(diseaseLabel, gbc);
            
                gbc.gridx = 1;
                JComboBox<Disease> diseaseComboBox = new JComboBox<>(diseases.toArray(new Disease[0]));
                formPanel.add(diseaseComboBox, gbc);
            
                // Status
                gbc.gridx = 0;
                gbc.gridy = 1;
                JLabel statusLabel = new JLabel("Status:");
                statusLabel.setForeground(ColorScheme.TEXT);
                formPanel.add(statusLabel, gbc);
            
                gbc.gridx = 1;
                String[] statuses = {"Active", "Recovered", "Chronic", "In Treatment"};
                JComboBox<String> statusComboBox = new JComboBox<>(statuses);
                formPanel.add(statusComboBox, gbc);
            
                // Notes
                gbc.gridx = 0;
                gbc.gridy = 2;
                JLabel notesLabel = new JLabel("Notes:");
                notesLabel.setForeground(ColorScheme.TEXT);
                formPanel.add(notesLabel, gbc);
            
                gbc.gridx = 1;
                JTextArea notesArea = new JTextArea(5, 20);
                JScrollPane notesScrollPane = new JScrollPane(notesArea);
                formPanel.add(notesScrollPane, gbc);
            
                // Button panel
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setBackground(ColorScheme.BACKGROUND);
            
                AnimatedButton saveButton = new AnimatedButton("Save");
                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            // Get selected disease
                            Disease selectedDisease = (Disease) diseaseComboBox.getSelectedItem();
                            String status = (String) statusComboBox.getSelectedItem();
                            String notes = notesArea.getText().trim();
                        
                            // Create patient disease
                            PatientDisease patientDisease = new PatientDisease(
                                currentPatient.getPatientId(),
                                selectedDisease.getDiseaseId(),
                                notes,
                                status
                            );
                        
                            // Save patient disease
                            diseaseService.addDiseaseToPatient(patientDisease);
                        
                            // Close dialog
                            dialog.dispose();
                        
                            // Refresh data
                            loadPatientDiseases();
                        
                            // Show success message
                            JOptionPane.showMessageDialog(mainFrame, 
                                "Disease added to patient successfully", 
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(dialog, 
                                "Error adding disease to patient: " + ex.getMessage(), 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            
                AnimatedButton cancelButton = new AnimatedButton("Cancel");
                cancelButton.addActionListener(e -> dialog.dispose());
            
                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);
            
                // Add panels to dialog
                dialog.add(formPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
            
                // Show dialog
                dialog.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading diseases: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        /**
         * Remove a disease from the patient
         */
        private void removeDiseaseFromPatient(int patientDiseaseId) {
            try {
                // Confirm deletion
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to remove this disease from the patient?", 
                    "Confirm Removal", JOptionPane.YES_NO_OPTION);
            
                if (confirm == JOptionPane.YES_OPTION) {
                    // Remove disease
                    boolean success = diseaseService.removeDiseaseFromPatient(patientDiseaseId);
                
                    if (success) {
                        // Refresh data
                        loadPatientDiseases();
                    
                        // Show success message
                        JOptionPane.showMessageDialog(this, 
                            "Disease removed from patient successfully", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to remove disease from patient", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error removing disease from patient: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Admin panel for system administration
     */
    static class AdminPanel extends AnimatedPanel {
        private final MainFrame mainFrame;
        
        public AdminPanel(MainFrame mainFrame) {
            this.mainFrame = mainFrame;
            
            // Set layout
            setLayout(new BorderLayout());
            setBackground(ColorScheme.BACKGROUND);
            
            // Create top panel with navigation
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBackground(ColorScheme.BACKGROUND);
            topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Navigation buttons
            JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            navPanel.setBackground(ColorScheme.BACKGROUND);
            
            AnimatedButton dashboardButton = new AnimatedButton("Dashboard");
            dashboardButton.addActionListener(e -> mainFrame.showDashboard());
            
            AnimatedButton patientsButton = new AnimatedButton("Patients");
            patientsButton.addActionListener(e -> mainFrame.showPatientPanel());
            
            AnimatedButton logoutButton = new AnimatedButton("Logout");
            logoutButton.addActionListener(e -> mainFrame.logout());
            
            navPanel.add(dashboardButton);
            navPanel.add(patientsButton);
            navPanel.add(logoutButton);
            
            topPanel.add(navPanel, BorderLayout.WEST);
            
            // Create title panel
            JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            titlePanel.setBackground(ColorScheme.BACKGROUND);
            titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            
            JLabel titleLabel = new JLabel("Administration");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            titleLabel.setForeground(ColorScheme.TEXT);
            
            titlePanel.add(titleLabel);
            
            // Create content panel
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBackground(ColorScheme.BACKGROUND);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Admin message
            JLabel adminLabel = new JLabel("System Administration Panel");
            adminLabel.setFont(new Font("Arial", Font.BOLD, 18));
            adminLabel.setForeground(ColorScheme.TEXT);
            adminLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Admin content - placeholder for now
            JPanel adminContent = new JPanel(new GridLayout(2, 2, 20, 20));
            adminContent.setBackground(ColorScheme.BACKGROUND);
            
            // Add components to content panel
            contentPanel.add(adminLabel, BorderLayout.NORTH);
            contentPanel.add(adminContent, BorderLayout.CENTER);
            
            // Add panels to main panel
            add(topPanel, BorderLayout.NORTH);
            add(titlePanel, BorderLayout.NORTH);
            add(contentPanel, BorderLayout.CENTER);
        }
        
        /**
         * Refresh admin data
         */
        public void refreshData() {
            // Placeholder for admin data refresh
        }
    }
}
