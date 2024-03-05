package uk.co.asepstrath.bank;


public class Transaction {
    private String type;
    private  String accountUsed;
    private String transactionID;
    private String description;
    private double amount;
    private String category;
    private String date; // Add date field

    public Transaction(String description, double amount, String category, String transactionID, String date, String type, String accountUsed) {
        this.transactionID = transactionID;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.type = type;
        this.accountUsed = accountUsed;

    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //public String getAccountUsed() {return accountUsed;}

    //public void setAccountUsed(String accountUsed) {this.accountUsed = accountUsed;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transaction: " +
                "ID: " + transactionID + ' ' +
                "Category: " + category + ' ' +
                "Business: " + description + ' ' +
                "Amount: " + amount +
                "Date: " + date;
    }
}
