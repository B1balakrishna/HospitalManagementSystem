package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Doctors {
    private Connection connection;

    // Constructor
    public Doctors(Connection connection) {
        this.connection = connection;
    }

    // Method to view all doctors
    public void viewDoctor() {
        String query = "SELECT * FROM doctors";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("Doctors: ");
            System.out.println("+------------+---------------------+---------------------+");
            System.out.println("| Doctor ID  | Name                | Specialization      |");
            System.out.println("+------------+---------------------+---------------------+");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");

                System.out.printf("| %-10d | %-19s | %-19s |\n", id, name, specialization);
            }

            System.out.println("+------------+---------------------+---------------------+");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to check if a doctor exists by ID
    public boolean getDoctorById(int id) {
        String query = "SELECT * FROM doctors WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
