//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package uk.co.asepstrath.bank;

import java.util.UUID;

public class Account {
    private static Transaction[] transactionList;
    private UUID id;
    private String name;
    private double balance;
    private boolean roundUpEnabled;
    private String location;

    public Account(String name, double accountAmount) {
        this.name = name;
        this.balance = accountAmount;
    }

    public Account(UUID id, String name, double startingBalance, boolean roundUpEnabled) {
        this.id = id;
        this.name = name;
        this.balance = startingBalance;
        this.roundUpEnabled = roundUpEnabled;
    }

    public Account(UUID id, String name, double startingBalance, boolean roundUpEnabled, String location) {
        this.id = id;
        this.name = name;
        this.balance = startingBalance;
        this.roundUpEnabled = roundUpEnabled;
        this.location = location;
    }

    public static void setTransactionList(Transaction[] transactionList) {
        Account.transactionList = transactionList;
    }

    public void deposit(double amount) {
        this.balance += amount;
        this.balance = (double)Math.round(this.balance * 100.0);
        this.balance /= 100.0;
    }

    public void withdraw(double amount) {
        if (amount > this.balance) {
            throw new ArithmeticException();
        } else {
            this.balance -= amount;
            this.balance = (double)Math.round(this.balance * 100.0);
            this.balance /= 100.0;
        }
    }

    public double getBalance() {
        return this.balance;
    }

    public UUID getId(){return this.id;}

    public String getName(){return this.name;}

    public boolean isRoundUpEnabled(){return this.roundUpEnabled;}

    @Override
    public String toString() {
        return "ID: " + id + " Name: " + name + ", Balance: £" + balance + ", Round Up Enabled: " + roundUpEnabled;
    }

    public Object getLocation() {return this.location;}

    public static double getSpendingAmount(Account account) {
        double spendingAmount = 0;

        for (Transaction transaction : transactionList) {
            // Check if the transaction is an expense (negative amount)
            if (transaction.getAmount() < 0) {
                spendingAmount += Math.abs(transaction.getAmount());
            }
        }
        return spendingAmount;
    }
}