package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.WalletDAO;

import java.io.IOException;
import java.io.OutputStream;

public class AddBalanceHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("POST".equalsIgnoreCase(method)) {
            try {
                String body = new String(exchange.getRequestBody().readAllBytes());
                String[] params = body.split("&");

                int userId = Integer.parseInt(params[0].split("=")[1]);
                double amount = Double.parseDouble(params[1].split("=")[1]);

                boolean success = WalletDAO.addBalanceToWallet(userId, amount);

                String response = success ? "Balance added successfully!" : "Failed to add balance.";
                exchange.sendResponseHeaders(success ? 200 : 500, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                String response = "Invalid request data.";
                exchange.sendResponseHeaders(400, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
