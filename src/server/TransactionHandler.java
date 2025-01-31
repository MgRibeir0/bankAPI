package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.DatabaseConnection;
import server.util.ResponseUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionHandler implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger(TransactionHandler.class.getName());

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "POST":
                createTransaction(exchange);
                break;
            case "GET":
                getTransactions(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1);
                break;
        }
    }

    private void createTransaction(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes());
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            int fromUserId = json.get("fromUserId").getAsInt();
            int toUserId = json.get("toUserId").getAsInt();
            double amount = json.get("amount").getAsDouble();

            if (amount <= 0) {
                ResponseUtil.sendResponse(exchange, "Amount must be positive.", ResponseUtil.ResponseStatus.ERROR, 400);
                return;
            }

            Connection conn = null;

            try {
                conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    ResponseUtil.sendResponse(exchange, "Connection failed.", ResponseUtil.ResponseStatus.ERROR, 500);
                    return;
                }

                conn.setAutoCommit(false);

                String sql1 = "UPDATE Wallet SET balance = balance - ? WHERE user_id = ?";
                try (PreparedStatement stmt1 = conn.prepareStatement(sql1)) {
                    stmt1.setDouble(1, amount);
                    stmt1.setInt(2, fromUserId);
                    int rowsUpdated = stmt1.executeUpdate();

                    if (rowsUpdated == 0) {
                        ResponseUtil.sendResponse(exchange, "Source wallet not found.", ResponseUtil.ResponseStatus.ERROR, 404);
                        return;
                    }
                }

                String sql2 = "UPDATE Wallet SET balance = balance + ? WHERE user_id = ?";
                try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                    stmt2.setDouble(1, amount);
                    stmt2.setInt(2, toUserId);
                    int rowsUpdated = stmt2.executeUpdate();

                    if (rowsUpdated == 0) {
                        ResponseUtil.sendResponse(exchange, "Destination wallet not found.", ResponseUtil.ResponseStatus.ERROR, 404);
                        return;
                    }
                }

                String sql3 = "INSERT INTO Transactions (fromUserId, toUserId, amount) VALUES (?, ?, ?)";
                try (PreparedStatement stmt3 = conn.prepareStatement(sql3)) {
                    stmt3.setInt(1, fromUserId);
                    stmt3.setInt(2, toUserId);
                    stmt3.setDouble(3, amount);
                    stmt3.executeUpdate();
                }

                conn.commit();
                ResponseUtil.sendResponse(exchange, "Transaction created.", ResponseUtil.ResponseStatus.SUCCESS, 201);

            } catch (SQLException e) {
                try {
                    conn.rollback();
                    LOGGER.info("Transaction reverted.");
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error: Unable to rollback transaction.", ex);
                }

                ResponseUtil.sendResponse(exchange, "Internal server error: " + e.getMessage(), ResponseUtil.ResponseStatus.ERROR, 500);

            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Error: Unable to close connection.", e);
                    }
                }
            }

        } catch (Exception e) {
            ResponseUtil.sendResponse(exchange, "Invalid request data: " + e.getMessage(), ResponseUtil.ResponseStatus.ERROR, 400);
        }
    }

    private void getTransactions(HttpExchange exchange) throws IOException {
        try {

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("message", "List of transactions");
            responseJson.addProperty("status", ResponseUtil.ResponseStatus.SUCCESS.getValue());

            ResponseUtil.sendResponse(exchange, responseJson, 200);
        } catch (Exception e) {
            ResponseUtil.sendResponse(exchange, "Invalid request data.", ResponseUtil.ResponseStatus.ERROR, 400);
        }
    }
}