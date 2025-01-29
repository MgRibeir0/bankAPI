package model;

public class Wallet {
    private final int id;
    private final int userId;
    private double balance;

    public Wallet(int id, double balance, int userId) {
        this.id = id;
        this.balance = balance;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getUserId() {
        return userId;
    }
}
