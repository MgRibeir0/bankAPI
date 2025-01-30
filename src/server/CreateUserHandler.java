package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDAO;

import java.io.IOException;
import java.io.OutputStream;

public class CreateUserHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("POST".equalsIgnoreCase(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes());
            String[] userData = body.split("&");
            String firstName = userData[0].split("=")[1];
            String lastName = userData[1].split("=")[1];

            boolean success = UserDAO.createUser(firstName, lastName);

            String response = success ? "User created!" : "Error while creating user.";
            exchange.sendResponseHeaders(success ? 201 : 500, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
