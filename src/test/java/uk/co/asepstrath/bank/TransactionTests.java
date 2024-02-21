//package uk.co.asepstrath.bank;
//
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class TransactionTests {
//
//    @Test
//    public void testGettersAndSetters() {
//        Transaction transaction = new Transaction("Groceries", 50.0, "Food", "123", "2024-02-21");
//
//        assertEquals("123", transaction.getTransactionID());
//        assertEquals("Groceries", transaction.getDescription());
//        assertEquals(50.0, transaction.getAmount(), 0.001);
//        assertEquals("Food", transaction.getCategory());
//        assertEquals("2024-02-21", transaction.getDate());
//
//        transaction.setTransactionID("456");
//        transaction.setDescription("Clothing");
//        transaction.setAmount(75.0);
//        transaction.setCategory("Shopping");
//        transaction.setDate("2024-02-22");
//
//        assertEquals("456", transaction.getTransactionID());
//        assertEquals("Clothing", transaction.getDescription());
//        assertEquals(75.0, transaction.getAmount(), 0.001);
//        assertEquals("Shopping", transaction.getCategory());
//        assertEquals("2024-02-22", transaction.getDate());
//    }
//
//    @Test
//    public void testToString() {
//        Transaction transaction = new Transaction("Groceries", 50.0, "Food", "123", "2024-02-21");
//        String expected = "Transaction: ID: 123 Category: Food Business: Groceries Amount: 50.0 Date: 2024-02-21";
//        assertEquals(expected, transaction.toString());
//    }
//}
