package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    public void testGetAccountsFromDatabase() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Logger logger = mock(Logger.class);
        List<Account> accountsList = new ArrayList<>();
        List<Transactions> transactionsList = new ArrayList<>();
        List<Business> businessesList = new ArrayList<>();

        // Create a mock Connection
        Connection connection = mock(Connection.class);

        // Create a mock PreparedStatement
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        // Mock DataSource.getConnection() to return the mock Connection
        when(dataSource.getConnection()).thenReturn(connection);

        // Mock Connection.prepareStatement() to return the mock PreparedStatement
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Create an instance of AccountController with the mocked DataSource and other dependencies
        AccountController accountController = new AccountController(dataSource, logger, accountsList, transactionsList, businessesList);

        // Mock ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false); // Simulate one row in the result set
        when(resultSet.getString("id")).thenReturn("550e8400-e29b-41d4-a716-446655440000");
        when(resultSet.getString("name")).thenReturn("Test Account");
        when(resultSet.getDouble("startingBalance")).thenReturn(100.0);
        when(resultSet.getBoolean("roundUpEnabled")).thenReturn(true);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Call the method to test
        List<Account> accounts = accountController.getAccountsFromDatabase();

        // Assert the results
        assertNotNull(accounts);
        assertEquals(1, accounts.size()); // We expect only one account

        Account account = accounts.get(0);
        assertEquals("550e8400-e29b-41d4-a716-446655440000", account.getId().toString());
        assertEquals("Test Account", account.getName());
        assertEquals(100.0, account.getBalance(), 0.01); // Delta is used for double comparison
        assertEquals(true, account.isRoundUpEnabled());
    }

    @Test
    public void testGetUserNameFromUUID() throws SQLException {
        // Mock DataSource, Connection, and PreparedStatement
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        // Create an instance of AccountController with the mocked DataSource
        AccountController accountController = new AccountController(dataSource, null, null, null, null);

        // Mock DataSource.getConnection() to return the mock Connection
        when(dataSource.getConnection()).thenReturn(connection);

        // Mock Connection.prepareStatement() to return the mock PreparedStatement
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Mock ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true); // Simulate one row in the result set
        when(resultSet.getString("name")).thenReturn("John Doe"); // Simulate returning a name from the database
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Call the method to test
        String username = "550e8400-e29b-41d4-a716-446655440000"; // Sample UUID
        String name = accountController.getUserNameFromUUID(username);

        // Assert the result
        assertEquals("John Doe", name); // We expect the name retrieved from the database
    }

    @Test
    public void testGetStartingAmountFromUUID() throws SQLException {
        // Mock DataSource, Connection, and PreparedStatement
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        // Create an instance of AccountController with the mocked DataSource
        AccountController accountController = new AccountController(dataSource, null, null, null, null);

        // Mock DataSource.getConnection() to return the mock Connection
        when(dataSource.getConnection()).thenReturn(connection);

        // Mock Connection.prepareStatement() to return the mock PreparedStatement
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Mock ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true); // Simulate one row in the result set
        when(resultSet.getDouble("startingBalance")).thenReturn(500.0); // Simulate returning a starting balance from the database
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Call the method to test
        String uuid = "550e8400-e29b-41d4-a716-446655440000"; // Sample UUID
        double startingBalance = accountController.getStartingAmountFromUUID(uuid);

        // Assert the result
        assertEquals(500.0, startingBalance); // We expect the starting balance retrieved from the database
    }

    @Test
    void testGetOutgoingTransactions() {
        // Create sample transactions
        List<Transactions> transactions = new ArrayList<>();
        transactions.add(new Transactions("1", 100.0, "sender1", "transaction1", "receiver1", "type1"));
        transactions.add(new Transactions("2", 200.0, "sender2", "transaction2", "receiver2", "type2"));
        transactions.add(new Transactions("3", 300.0, "sender1", "transaction3", "receiver1", "type3"));

        // Create an instance of AccountController
        AccountController accountController = new AccountController(null, null, null, null, null);

        // Call the method to test
        List<Transactions> outgoingTransactions = accountController.getOutgoingTransactions(transactions, "sender1");

        // Assert
        assertEquals(2, outgoingTransactions.size()); // Expecting 1 outgoing transaction for "sender1"
        assertEquals("transaction1", outgoingTransactions.get(0).getId()); // Ensure the correct transaction is retrieved
    }

    @Test
    void testGetIncomingTransactions() {
        // Create sample transactions
        List<Transactions> transactions = new ArrayList<>();
        transactions.add(new Transactions("1", 100.0, "sender1", "transaction1", "receiver1", "type1"));
        transactions.add(new Transactions("2", 200.0, "sender2", "transaction2", "receiver2", "type2"));
        transactions.add(new Transactions("3", 300.0, "sender3", "transaction3", "receiver1", "type3"));

        // Create an instance of AccountController
        AccountController accountController = new AccountController(null, null, null, null, null);

        // Call the method to test
        List<Transactions> incomingTransactions = accountController.getIncomingTransactions(transactions, "receiver1");

        // Assert
        assertEquals(2, incomingTransactions.size()); // Expecting 2 incoming transactions for "receiver1"
        assertEquals("transaction1", incomingTransactions.get(0).getId()); // Ensure the correct transaction is retrieved
        assertEquals("transaction3", incomingTransactions.get(1).getId()); // Ensure the correct transaction is retrieved
    }

    @Test
    void testGetAllTransactionsInvolvingAccount() {
        // Create sample transactions
        List<Transactions> transactions = new ArrayList<>();
        transactions.add(new Transactions("1", 100.0, "sender1", "transaction1", "receiver1", "type1"));
        transactions.add(new Transactions("2", 200.0, "sender2", "transaction2", "receiver2", "type2"));
        transactions.add(new Transactions("3", 300.0, "sender3", "transaction3", "receiver1", "type3"));

        // Create an instance of AccountController
        AccountController accountController = new AccountController(null, null, null, transactions, null);

        // Call the method to test
        List<Transactions> relevantTransactions = accountController.getAllTransactionsInvolvingAccount("receiver1");

        // Assert
        assertEquals(2, relevantTransactions.size()); // Expecting 2 transactions involving "receiver1"
        assertEquals("transaction1", relevantTransactions.get(0).getId()); // Ensure the correct transaction is retrieved
        assertEquals("transaction3", relevantTransactions.get(1).getId()); // Ensure the correct transaction is retrieved
    }

    @Test
    void testCalculateCurrentAmount() {
        // Create sample transactions
        List<Transactions> transactions = new ArrayList<>();
        transactions.add(new Transactions("2", 200.0, "sender2", "transaction2", "receiver2", "DEPOSIT"));
        transactions.add(new Transactions("3", 300.0, "sender3", "transaction3", "receiver1", "TRANSFER"));

        // Calculate current amount
        AccountController accountController = new AccountController(null, null, null, transactions, null);
        double currentAmount = accountController.calculateCurrentAmount(1000.0, transactions, "receiver1");

        // Assert
        assertEquals(1000.0 + 300.0, currentAmount); // Expecting total amount after transactions
    }

    @Test
    void testFilterTransactionsByFrom() {
        // Create sample transactions
        List<Transactions> transactions = new ArrayList<>();
        transactions.add(new Transactions("1", 100.0, "sender1", "transaction1", "receiver1", "type1"));
        transactions.add(new Transactions("2", 200.0, "sender2", "transaction2", "receiver2", "type2"));
        transactions.add(new Transactions("3", 300.0, "sender1", "transaction3", "receiver1", "type3"));

        // Filter transactions by sender
        AccountController accountController = new AccountController(null, null, null, transactions, null);
        List<Transactions> filteredTransactions = accountController.filterTransactionsByFrom("sender1");

        // Assert
        assertEquals(2, filteredTransactions.size()); // Expecting 2 transactions sent by "sender1"
        assertEquals("transaction1", filteredTransactions.get(0).getId()); // Ensure the correct transaction is retrieved
        assertEquals("transaction3", filteredTransactions.get(1).getId()); // Ensure the correct transaction is retrieved
    }


    @Test
    void testCalculateSpendingSummary() throws SQLException {
        // Create mock DataSource, Connection, and PreparedStatement
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        // Create an instance of AccountController with the mocked DataSource
        AccountController accountController = new AccountController(dataSource, null, null, null, null);

        // Mock DataSource.getConnection() to return the mock Connection
        when(dataSource.getConnection()).thenReturn(connection);

        // Mock Connection.prepareStatement() to return the mock PreparedStatement
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Mock ResultSet
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false); // Simulate two rows in the result set
        when(resultSet.getString("category")).thenReturn("Groceries").thenReturn("Shopping"); // Mock category values
        when(resultSet.getDouble("totalAmount")).thenReturn(50000000.0).thenReturn(75000000.0); // Mock total amount values
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Call the method to test
        HashMap<String, Double> spendingSummary = (HashMap<String, Double>) accountController.calculateSpendingSummary("username");

        // Assert the results
        assertEquals(2, spendingSummary.size()); // Expecting two categories in the spending summary
        assertEquals(5000.0, spendingSummary.get("Groceries")); // Expecting total amount for Groceries category
        assertEquals(7500.0, spendingSummary.get("Shopping")); // Expecting total amount for Shopping category
    }

    @Test
    void testGetSanctionedTransactions() throws SQLException {
        // Mock DataSource, Connection, PreparedStatement, and ResultSet
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        // Create an instance of AccountController with the mocked DataSource
        AccountController accountController = new AccountController(dataSource, null, null, null, null);

        // Mock DataSource.getConnection() to return the mock Connection
        when(dataSource.getConnection()).thenReturn(connection);

        // Mock Connection.prepareStatement() to return the mock PreparedStatement
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Mock ResultSet
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false); // Simulate two rows in the result set
        when(resultSet.getString("timestamp")).thenReturn("2024-03-19 10:00:00").thenReturn("2024-03-19 11:00:00"); // Mock timestamp values
        when(resultSet.getDouble("amount")).thenReturn(100.0); // Mock amount value
        when(resultSet.getString("sender")).thenReturn("sender1"); // Mock sender value
        when(resultSet.getString("transaction_id")).thenReturn("123456").thenReturn("789012"); // Mock transaction ID values
        when(resultSet.getString("receiver")).thenReturn("receiver1").thenReturn("receiver2"); // Mock receiver values
        when(resultSet.getString("type")).thenReturn("type1").thenReturn("type2"); // Mock type values
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Call the method to test
        List<Transactions> sanctionedTransactions = accountController.getSanctionedTransactions();

        // Assert the results
        assertEquals(2, sanctionedTransactions.size()); // Expecting two sanctioned transactions
    }


    @Test
    void testGetBusinessNameById() {
        // Prepare test data for businesses
        List<Business> businesses = new ArrayList<>();
        businesses.add(new Business("1", "Business1", "food",false));
        businesses.add(new Business("2", "Business2","food", false));
        businesses.add(new Business("3", "Business3", "food", false));

        // Create an instance of AccountController with the test businesses
        AccountController accountController = new AccountController(null, null, null, null, businesses);

        // Call the method to test with an existing business ID
        String businessName1 = accountController.getBusinessNameById("2");

        // Assert the result for an existing business ID
        assertEquals("Business2", businessName1);

        // Call the method to test with a non-existing business ID
        String businessName2 = accountController.getBusinessNameById("4");

        // Assert the result for a non-existing business ID
        assertNull(businessName2);
    }

}