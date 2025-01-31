package server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class BankAPIServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.createContext("/api/user", new UserHandler());
        server.createContext("/api/wallet", new WalletHandler());
        server.createContext("/api/transactions", new TransactionHandler());

        server.setExecutor(null); // Usa um executor padrão
        server.start();
        System.out.println("Server started in port 8000...");
    }
}
