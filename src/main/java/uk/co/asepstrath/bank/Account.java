//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package uk.co.asepstrath.bank;

import java.text.DecimalFormat;
import java.util.UUID;

public class Account {
    private UUID id;
    private String name;
    private double balance;
    private boolean roundUpEnabled;
    private String address;
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

   /* public Account(UUID fromString, String name, double startingBalance, boolean roundUpEnabled, String address) {
        this.id = fromString;
        this.name = name;
        this.balance = startingBalance;
        this.roundUpEnabled = roundUpEnabled;
        this.address = address;

    }*/

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




    // Method to set the formatted amount



    @Override
    public String toString() {
        return "ID: " + id + " Name: " + name + ", Balance: £" + balance + ", Round Up Enabled: " + roundUpEnabled ;
    }
}