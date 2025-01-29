import dao.UserDAO;
import dao.WalletDAO;
import model.User;
import model.Wallet;
import server.WalletAPIServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Testes de DAO
        System.out.println("Iniciando testes de DAO...");

        boolean userCreated = UserDAO.createUser("John", "Doe");
        if (userCreated) {
            System.out.println("User created successfully!");
        } else {
            System.out.println("User creation failed.");
        }

        User user = UserDAO.getUserByID(1);
        if (user != null) {
            System.out.println("User found: " + user.firstName() + " " + user.lastName());
        } else {
            System.out.println("User not found.");
        }

        if (user != null) {
            boolean walletCreated = WalletDAO.createWallet(user.id(), 100.0);
            if (walletCreated) {
                System.out.println("Wallet created successfully for user ID: " + user.id());
            } else {
                System.out.println("Wallet creation failed.");
            }
        }

        if (user != null) {
            Wallet wallet = WalletDAO.getWalletByUserID(user.id());
            if (wallet != null) {
                System.out.println("Wallet found for user ID " + user.id() + " with balance: " + wallet.getBalance());
            } else {
                System.out.println("Wallet not found for user ID " + user.id());
            }
        }

        // Iniciando o servidor HTTP
        System.out.println("Iniciando o servidor HTTP...");
        try {
            WalletAPIServer.main(args);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}
