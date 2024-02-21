package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void getDescription() {
        // Arrange
        Transaction transaction = new Transaction("", 1000,"","","");
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
        Transaction transaction = new Transaction("", 1000,"","","");
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
        Transaction transaction = new Transaction("", 1000,"","","");
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
        Transaction transaction = new Transaction("", 1000,"","","");
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
        Transaction transaction = new Transaction("", 1000,"","","");
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
        Transaction transaction = new Transaction("", 1000,"","","");
        String expectedCategory = "Test Category";

        // Act
        transaction.setCategory(expectedCategory);
        String actualCategory = transaction.getCategory();

        // Assert
        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    void testToString() {
        // Arrange
        Transaction transaction = new Transaction("", 1000,"","","");
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