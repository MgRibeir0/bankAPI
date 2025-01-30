package test;

import dao.UserDAO;
import dao.WalletDAO;
import model.User;
import model.Wallet;

public class DAOTests {

    public static void main(String[] args) {
        testUserCreation();
        testWalletCreation();
    }

    private static void testUserCreation() {
        System.out.println("Iniciando teste de criação de usuário...");
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
    }

    private static void testWalletCreation() {
        System.out.println("Iniciando teste de criação de carteira...");
        User user = UserDAO.getUserByID(1);
        if (user != null) {
            boolean walletCreated = WalletDAO.createWallet(user.id(), 100.0);
            if (walletCreated) {
                System.out.println("Wallet created successfully for user ID: " + user.id());
            } else {
                System.out.println("Wallet creation failed.");
            }

            Wallet wallet = WalletDAO.getWalletByUserID(user.id());
            if (wallet != null) {
                System.out.println("Wallet found for user ID " + user.id() + " with balance: " + wallet.getBalance());
            } else {
                System.out.println("Wallet not found for user ID " + user.id());
            }
        }
    }
}
