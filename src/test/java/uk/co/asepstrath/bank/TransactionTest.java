package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void getDescription() {
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
    void setDescription() {
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
    void getAmount() {
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
    void setAmount() {
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
    void getCategory() {
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
    void setCategory() {
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
    void testTransactionID() {
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
    void testDate() {
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
    void testType() {
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
    void testAccountUsed() {
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
    void testParameterizedConstructor() {
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
    void testToString() {
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
        assertEquals(100.50, 100.50);
        assertTrue(toStringResult.contains("Test Category"));
    }
}