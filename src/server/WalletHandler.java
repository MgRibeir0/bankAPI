package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.WalletDAO;
import server.util.ResponseUtil;

import java.io.IOException;

public class WalletHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();


        switch (method) {
            case "GET":
                getWalletBalance(exchange);
                break;
            case "PUT":
                addBalanceToWallet(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1);
                break;
        }
    }

    private void getWalletBalance(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            int userId = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));

            double balance = WalletDAO.getWalletBalance(userId);

            if (balance >= 0) {
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("balance", balance);
                responseJson.addProperty("status", ResponseUtil.ResponseStatus.SUCCESS.getValue());

                ResponseUtil.sendResponse(exchange, responseJson, 200);
            } else {
                ResponseUtil.sendResponse(exchange, "Wallet not found.", ResponseUtil.ResponseStatus.ERROR, 404);
            }
        } catch (Exception e) {
            ResponseUtil.sendResponse(exchange, "Invalid request data.", ResponseUtil.ResponseStatus.ERROR, 400);
        }
    }

    private void addBalanceToWallet(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            int userId = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));

            String body = new String(exchange.getRequestBody().readAllBytes());
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            double amount = json.get("amount").getAsDouble();

            if (amount <= 0) {
                ResponseUtil.sendResponse(exchange, "Amount must be positive.", ResponseUtil.ResponseStatus.ERROR, 400);
                return;
            }

            boolean success = WalletDAO.addBalanceToWallet(userId, amount);

            if (success) {
                ResponseUtil.sendResponse(exchange, "Balance added successfully!", ResponseUtil.ResponseStatus.SUCCESS, 200);
            } else {
                ResponseUtil.sendResponse(exchange, "Failed to add balance. User or wallet not found.", ResponseUtil.ResponseStatus.ERROR, 404);
            }
        } catch (Exception e) {
            ResponseUtil.sendResponse(exchange, "Invalid request data.", ResponseUtil.ResponseStatus.ERROR, 400);
        }
    }
}

