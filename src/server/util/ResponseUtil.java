package server.util;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class ResponseUtil {

    public static void sendResponse(HttpExchange exchange, String message, ResponseStatus status, int httpStatusCode) throws IOException {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("message", message);
        responseJson.addProperty("status", status.getValue());

        sendResponse(exchange, responseJson, httpStatusCode);
    }

    public static void sendResponse(HttpExchange exchange, JsonObject responseJson, int httpStatusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(httpStatusCode, responseJson.toString().getBytes().length);

        OutputStream os = exchange.getResponseBody();
        os.write(responseJson.toString().getBytes());
        os.close();
    }

    public enum ResponseStatus {
        SUCCESS("success"),
        ERROR("error");

        private final String value;

        ResponseStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}