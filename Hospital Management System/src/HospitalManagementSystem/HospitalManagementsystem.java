package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

class HospitalManagementsystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Bala@12345678";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Patient patient = new Patient(connection, scanner);
            Doctors doctors = new Doctors(connection);

            while (true) {
                System.out.println("\nHospital Management System");
                System.out.println("1. Add patient");
                System.out.println("2. View patient");
                System.out.println("3. View doctor");
                System.out.println("4. Book appointment");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1: // Add patient
                        patient.addPatient();
                        break;

                    case 2: // View patient
                        patient.viewPatient();
                        break;

                    case 3: // View doctor
                        doctors.viewDoctor();
                        break;

                    case 4: // Book appointment
                        bookAppointment(patient, doctors, connection, scanner);
                        break;

                    case 5: // Exit
                        System.out.println("Exiting the system. Goodbye!");
                        return;

                    default:
                        System.out.println("Enter a valid choice!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctors doctors, Connection connection, Scanner scanner) {
        System.out.print("Enter the patient ID: ");
        int patientId = scanner.nextInt();

        System.out.print("Enter the doctor ID: ");
        int doctorId = scanner.nextInt();

        System.out.print("Enter the appointment date (yyyy-mm-dd): ");
        String appointmentDate = scanner.next();

        if (patient.getPatientById(patientId) && doctors.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointments (patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery)) {
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);

                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Appointment booked successfully!");
                    } else {
                        System.out.println("Failed to book the appointment.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("The doctor is not available on this date.");
            }
        } else {
            System.out.println("Invalid patient or doctor ID.");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
