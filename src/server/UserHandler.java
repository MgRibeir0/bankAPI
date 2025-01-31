package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDAO;
import model.User;
import server.util.ResponseUtil;

import java.io.IOException;

public class UserHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "POST":
                createUser(exchange);
                break;
            case "GET":
                getUser(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1);
                break;
        }
    }

    private void createUser(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes());
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            String firstName = json.get("firstName").getAsString();
            String lastName = json.get("lastName").getAsString();

            boolean success = UserDAO.createUser(firstName, lastName);

            if (success) {
                ResponseUtil.sendResponse(exchange, "User created successfully!", ResponseUtil.ResponseStatus.SUCCESS, 201);
            } else {
                ResponseUtil.sendResponse(exchange, "Failed to create user.", ResponseUtil.ResponseStatus.ERROR, 500);
            }
        } catch (Exception e) {
            ResponseUtil.sendResponse(exchange, "Invalid request data.", ResponseUtil.ResponseStatus.ERROR, 400);
        }
    }

    private void getUser(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            int userId = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));

            User user = UserDAO.getUserByID(userId);
            if (user != null) {
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("id", user.id());
                responseJson.addProperty("firstName", user.firstName());
                responseJson.addProperty("lastName", user.lastName());
                responseJson.addProperty("status", ResponseUtil.ResponseStatus.SUCCESS.getValue());

                ResponseUtil.sendResponse(exchange, responseJson, 200);
            } else {
                ResponseUtil.sendResponse(exchange, "User not found.", ResponseUtil.ResponseStatus.ERROR, 404);
            }
        } catch (Exception e) {
            ResponseUtil.sendResponse(exchange, "Invalid request data.", ResponseUtil.ResponseStatus.ERROR, 400);
        }
    }
}