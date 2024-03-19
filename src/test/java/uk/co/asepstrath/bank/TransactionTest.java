package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    public void getDescription() {
        // Arrange
        Transaction transaction = new Transaction("", 1000,"","","","","");
        String expectedDescription = "Test Description";
        transaction.setDescription(expectedDescription);

        // Act
        String actualDescription = transaction.getDescription();

        // Assert
        assertEquals(expectedDescription, actualDescription);
    }

    @Test
    public void setDescription() {
        // Arrange
        Transaction transaction = new Transaction("", 1000,"","","","","");
        String expectedDescription = "Test Description";

        // Act
        transaction.setDescription(expectedDescription);
        String actualDescription = transaction.getDescription();

        // Assert
        assertEquals(expectedDescription, actualDescription);
    }

    @Test
    public void getAmount() {
        // Arrange
        Transaction transaction = new Transaction("", 1000,"","","","","");
        double expectedAmount = 100.50;
        transaction.setAmount(expectedAmount);

        // Act
        double actualAmount = transaction.getAmount();

        // Assert
        assertEquals(expectedAmount, actualAmount, 0.001); // Using delta for double comparison
    }

    @Test
    public void setAmount() {
        // Arrange
        Transaction transaction = new Transaction("", 1000,"","","","","");
        double expectedAmount = 100.50;

        // Act
        transaction.setAmount(expectedAmount);
         double actualAmount = transaction.getAmount();

        // Assert
        assertEquals(expectedAmount, actualAmount, 0.001); // Using delta for double comparison
    }
    @Test
    public void testGetAmountFormatted() {
        // Create a Transactions object with a known amount
        Transactions transaction = new Transactions("2024-03-19", 123.456, "sender", "1", "receiver", "PAYMENT");

        // Call the setAmountFormatted method
        transaction.setAmountFormatted();

        // Call the getAmountFormatted method
        String formattedAmount = transaction.getAmountFormatted();

        // Check if the formatted amount matches the expected value
        assertEquals("123.46", formattedAmount); // The expected value depends on how you want to round the amount
    }
    @Test
    public void getCategory() {
        // Arrange
        Transaction transaction = new Transaction("", 1000,"","","","","");
        String expectedCategory = "Test Category";
        transaction.setCategory(expectedCategory);

        // Act
        String actualCategory = transaction.getCategory();

        // Assert
        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    public void setCategory() {
        // Arrange
        Transaction transaction = new Transaction("", 1000,"","","","","");
        String expectedCategory = "Test Category";

        // Act
        transaction.setCategory(expectedCategory);
        String actualCategory = transaction.getCategory();

        // Assert
        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    public void testTransactionID() {
        // Arrange
        Transaction transaction = new Transaction("", 1000,"","","","","");
        String expectedTransactionID = "XYZ789";

        // Act
        transaction.setTransactionID(expectedTransactionID);
        String actualTransactionID = transaction.getTransactionID();

        // Assert
        assertEquals(expectedTransactionID, actualTransactionID);
    }

    @Test
    public void testDate() {
        // Arrange
        Transaction transaction = new Transaction("", 1000,"","","","","");
        String expectedDate = "2022-02-22";

        // Act
        transaction.setDate(expectedDate);
        String actualDate = transaction.getDate();

        // Assert
        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void testType() {
        // Arrange
        Transaction transaction = new Transaction("", 1000,"","","","","");
        String expectedType = "Payment";

        // Act
        transaction.setDate(expectedType);
        String actualDate = transaction.getDate();

        // Assert
        assertEquals(expectedType, actualDate);
    }

    @Test
    public void testAccountUsed() {
        // Arrange
        Transaction transaction = new Transaction("", 1000,"","","","","");
        String expectedAccountUsed = "12345678";

        // Act
        transaction.setDate(expectedAccountUsed);
        String actualDate = transaction.getDate();

        // Assert
        assertEquals(expectedAccountUsed, actualDate);
    }

    @Test
    public void testParameterizedConstructor() {
        // Arrange
        String description = "Test Description";
        double amount = 100.50;
        String category = "Test Category";
        String transactionID = "ABC123";
        String date = "2022-02-21";
        String type = "Payment";
          String accountUsed = "12345678";

        // Act
        Transaction transaction = new Transaction(description, amount, category, transactionID, date, type, accountUsed);

        // Assert
        assertEquals(description, transaction.getDescription());
        assertEquals(amount, transaction.getAmount(), 0.001); // Using delta for double comparison
        assertEquals(category, transaction.getCategory());
        assertEquals(transactionID, transaction.getTransactionID());
        assertEquals(date, transaction.getDate());
    }

    @Test
    void testGetType() {
        // Create an instance of the Transaction class
        Transaction transaction = new Transaction("", 1000,"","","","","");

        // Set up test data
        String expectedType = "Payment";
        transaction.setType(expectedType);

        // Call the getType method and verify the result
        String actualType = transaction.getType();
        assertEquals(expectedType, actualType);
    }

    @Test
    public void testToString() {
        // Arrange
        Transaction transaction = new Transaction("", 1000,"","","","","");
        transaction.setDescription("Test Description");
        transaction.setAmount(100.50);
        transaction.setCategory("Test Category");

        // Act
        String toStringResult = transaction.toString();

        // Assert
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("Test Description"));
        assertEquals(100.50, transaction.getAmount());

        assertTrue(toStringResult.contains("Test Category"));
    }

    @Test
    void testSetAmount() {
        Transactions transactions = new Transactions("", 1000,"","","","");

        // Set up test data
        double testAmount = 50.0;

        // Call the setter method
        transactions.setAmount(testAmount);

        // Verify that the property has been set correctly
        assertEquals(testAmount, transactions.getAmount());
    }

    @Test
    void testSetId() {
        Transactions transactions = new Transactions("", 1000,"","","","");

        // Set up test data
        String testId = "123456";

        // Call the setter method
        transactions.setId(testId);

        // Verify that the property has been set correctly
        assertEquals(testId, transactions.getId());
    }

    @Test
    void testSetTo() {
        Transactions transactions = new Transactions("", 1000,"","","","");

        // Set up test data
        String testTo = "Receiver";

        // Call the setter method
        transactions.setTo(testTo);

        // Verify that the property has been set correctly
        assertEquals(testTo, transactions.getTo());
    }

    @Test
    void testSetType() {
        Transactions transactions = new Transactions("", 1000,"","","","");

        // Set up test data
        String testType = "Payment";

        // Call the setter method
        transactions.setType(testType);

        // Verify that the property has been set correctly
        assertEquals(testType, transactions.getType());
    }

    @Test
    void testGetOutgoingTransactions() {
        Transactions transaction1 = new Transactions("", 10, "fromAccount1","","toAccount1","DEPOSIT");
        Transactions transaction2 = new Transactions("",10,"fromAccount1","", "toAccount2", "WITHDRAW");
        Transactions transaction3 = new Transactions("",10,"fromAccount2","", "toAccount1", "WITHDRAW");

        List<Transactions> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);

        AccountController transactionsProcessor = new AccountController(null,null,null,null,null);

        List<Transactions> outgoingTransactions = transactionsProcessor.getOutgoingTransactions(transactions, "fromAccount1");

        assertEquals(1, outgoingTransactions.size());
        assertEquals(transaction2, outgoingTransactions.get(0));
    }

    @Test
    void testGetIncomingTransactions() {
        Transactions transaction1 = new Transactions("", 10, "fromAccount1","","toAccount1","DEPOSIT");
        Transactions transaction2 = new Transactions("",10,"fromAccount1","", "toAccount2", "WITHDRAW");
        Transactions transaction3 = new Transactions("",10,"fromAccount2","", "toAccount1", "WITHDRAW");

        List<Transactions> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);

        AccountController transactionsProcessor = new AccountController(null,null,null,null,null);

        List<Transactions> incomingTransactions = transactionsProcessor.getIncomingTransactions(transactions, "toAccount1");

        assertEquals(1, incomingTransactions.size());
        assertEquals(transaction3, incomingTransactions.get(0));
    }

    /*@Test
    void testGetAllTransactionsInvolvingAccount() {
        Transactions transaction1 = new Transactions("", 10, "fromAccount1","","toAccount1","DEPOSIT");
        Transactions transaction2 = new Transactions("",10,"fromAccount1","", "toAccount2", "WITHDRAW");
        Transactions transaction3 = new Transactions("",10,"fromAccount2","", "toAccount1", "WITHDRAW");

        List<Transactions> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);

        AccountController transactionsProcessor = new AccountController(null,null,null,null,null);

        List<Transactions> allTransactions = transactionsProcessor.getAllTransactionsInvolvingAccount("fromAccount1");

        assertEquals(2, allTransactions.size());
        assertEquals(transaction1, allTransactions.get(0));
        assertEquals(transaction2, allTransactions.get(1));
    }*/
}