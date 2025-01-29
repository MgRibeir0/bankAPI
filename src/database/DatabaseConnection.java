package database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public static Connection getConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            LOGGER.info("Connected to MariaDB");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error: MariaDB Driver not found!", e);
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error: Cannot connect to MariaDB!", e);
            return null;
        }
    }
}
