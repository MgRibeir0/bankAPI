package dao;

import database.DatabaseConnection;
import model.Wallet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WalletDAO {
    private static final Logger LOGGER = Logger.getLogger(WalletDAO.class.getName());

    public static boolean createWallet(int userID, double initialBalance) {
        String sql = "INSERT INTO Wallet (balance, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Error: Not able to establish connection to MariaDB.");
                return false;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, initialBalance);
                stmt.setInt(2, userID);
                stmt.executeUpdate();
                LOGGER.info("Wallet created successfully.");
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error: Unable to create wallet.", e);
            return false;
        }
    }

    public static Wallet getWalletByUserID(int userID) {
        String sql = "SELECT * FROM Wallet WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Error: Not able to establish connection to MariaDB.");
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userID);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    Wallet wallet = new Wallet(rs.getInt("id"), rs.getDouble("balance"), rs.getInt("user_id"));
                    LOGGER.info("Wallet found.");
                    return wallet;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error: Unable to retrieve wallet.", e);
        }
        return null;
    }
}
