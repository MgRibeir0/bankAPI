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

            // Usar RETURN_GENERATED_KEYS para recuperar o ID gerado
            try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.executeUpdate();

                // Obter o ID gerado
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    LOGGER.info("User created successfully with ID: " + userId);

                    // Criar automaticamente a carteira
                    boolean walletCreated = WalletDAO.createWallet(userId, 0.0);
                    if (walletCreated) {
                        LOGGER.info("Wallet created for user ID: " + userId);
                    } else {
                        LOGGER.warning("Failed to create wallet for user ID: " + userId);
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error: Unable to create user.", e);
            return false;
        }
        return false;
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
