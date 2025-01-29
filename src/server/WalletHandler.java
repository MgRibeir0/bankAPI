package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.WalletDAO;
import model.Wallet;

import java.io.IOException;
import java.io.OutputStream;

public class WalletHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            String query = exchange.getRequestURI().getQuery();
            int userId = Integer.parseInt(query.split("=")[1]);

            Wallet wallet = WalletDAO.getWalletByUserID(userId);

            String response = (wallet != null)
                    ? "Balance: " + wallet.getBalance()
                    : "Wallet not found.";

            exchange.sendResponseHeaders(wallet != null ? 200 : 404, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

        } else if ("POST".equalsIgnoreCase(method)) {
            String response = "To be implemented.";
            exchange.sendResponseHeaders(501, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
