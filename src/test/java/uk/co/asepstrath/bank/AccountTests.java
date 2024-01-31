package uk.co.asepstrath.bank;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountTests {

    @Test
    public void createAccount(){
        Account a = new Account("steven", 2);
        assertTrue(a != null);
    }

    @Test
    public void newAccountValue(){
        Account a = new Account("Steven", 0);
        assertTrue(a.getBalance() == 0);
    }
    @Test
    public void depositingAmounts(){
        Account a = new Account("Steven", 20);
        a.deposit(50);
        assertTrue(a.getBalance() == 70);
    }
    @Test
    public void withdrawingAmounts(){
        Account a = new Account("Steven",40);
        a.withdraw(20);
        assertTrue(a.getBalance() == 20);
    }
    @Test
    public void noOverdraft(){
        Account a = new Account("steven", 30);
        Assertions.assertThrows(ArithmeticException.class,() -> a.withdraw(100));
    }
    @Test
    public void superSaving(){
        Account a = new Account("Steven", 20);

        a.deposit(10);
        a.deposit(10);
        a.deposit(10);
        a.deposit(10);
        a.deposit(10);

        a.withdraw(20);
        a.withdraw(20);
        a.withdraw(20);

        assertTrue(a.getBalance() == 10);


    }
        @Test
    public void pennies(){
            Account a = new Account("steven", 0);
            a.deposit(5.45);
            a.deposit(17.56);

            assertTrue(a.getBalance() == 23.01);

        }

}
