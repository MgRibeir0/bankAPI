package dao;

import database.DatabaseConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PreparedStatement is used to replace the ? with actual values in an SQL query.
 */

public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    public static boolean createUser(String firstName, String lastName) {
        String sql = "INSERT INTO User (first_name, last_name) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Error: Not able to establish connection to MariaDB.");
                return false;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.executeUpdate();
                LOGGER.info("User created successfully: " + firstName + " " + lastName);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error: Unable to create user.", e);
            return false;
        }
    }

    public static User getUserByID(int id) {
        String sql = "SELECT * FROM User WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Error: Not able to establish connection to MariaDB.");
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    User user = new User(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"));
                    LOGGER.info("User found: [" + user.id() + "]: " + user.firstName() + " " + user.lastName());
                    return user;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error: SQL error while retrieving user.", e);
        }
        return null;
    }
}
