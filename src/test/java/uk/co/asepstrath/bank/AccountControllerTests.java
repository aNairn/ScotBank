package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AccountControllerTests {
    @Test
    void testGenerateAccountNumber() {
        // Arrange
        String uuidString = "0d8f8c38-58b3-4a79-a3e0-9b69b7f593a6"; // Sample UUID string
        UUID uuid = UUID.fromString(uuidString);
        AccountController accountController = new AccountController(null, null, null, null, null);

        // Act
        String accountNumber = accountController.generateAccountNumber(uuid);

        // Assert
        assertEquals("03858238", accountNumber); // Expected account number for the given UUID
    }

    @Test
    void testGenerateSortCode() {
        // Arrange
        String uuidString = "0d8f8c38-58b3-4a79-a3e0-9b69b7f593a6"; // Sample UUID string
        UUID uuid = UUID.fromString(uuidString);
        AccountController accountController = new AccountController(null, null, null, null, null);

        // Act
        String sortCode = accountController.generateSortCode(uuid);

        // Assert
        assertEquals("58-13-40", sortCode); // Expected sort code for the given UUID
    }

    @Test
    void testFindTransactionById_ValidId() {
        // Arrange
        String validTransactionId = "123456"; // Sample valid transaction ID
        List<Transactions> transactions = new ArrayList<>();
        transactions.add(new Transactions("123456", 100.0, "sender1", "transaction1", "receiver1", "type1"));
        transactions.add(new Transactions("789012", 200.0, "sender2", "transaction2", "receiver2", "type2"));
        AccountController accountController = new AccountController(null, null, null, transactions, null);

        // Act
        Transactions foundTransaction = accountController.findTransactionById(validTransactionId);

        // Assert
        assertNull(foundTransaction);

    }

    @Test
    void testCalculateCurrentAmountFromTransactions() {
        // Arrange
        String username = "sampleUser";
        AccountController accountController = new AccountController(null, null, null, null, null);

        // Mock transactions for the user
        List<Transactions> userTransactions = new ArrayList<>();
        userTransactions.add(new Transactions("123456", 50.0, "sampleUser", "transaction1", "receiver1", "type1"));
        userTransactions.add(new Transactions("789012", 25.0, "sampleUser", "transaction2", "receiver2", "type2"));

        // Mock getStartingAmountFromUUID and filterTransactionsByFrom methods
        AccountController mockController = Mockito.spy(accountController);
        Mockito.doReturn(100.0).when(mockController).getStartingAmountFromUUID(username);
        Mockito.doReturn(userTransactions).when(mockController).filterTransactionsByFrom(username);

        // Act
        double result = mockController.calculateCurrentAmountFromTransactions(username);

        // Assert
        assertEquals(100.0 - 50.0 - 25.0, result);
    }
}