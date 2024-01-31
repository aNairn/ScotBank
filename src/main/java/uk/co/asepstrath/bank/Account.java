//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package uk.co.asepstrath.bank;

public class Account {
    String name;
    double balance;

    public Account(String name, double accountAmount) {
        this.name = name;
        this.balance = accountAmount;
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
}
