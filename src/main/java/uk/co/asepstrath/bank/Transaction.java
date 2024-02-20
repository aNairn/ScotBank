package uk.co.asepstrath.bank;

public class Transaction {
    private String description;
    private double amount;
    private String category;

    public Transaction(String description, double amount, String category) {
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

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

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }


    @Override
    public String toString() {
        return "Transaction: " +
                " Category: " + category + ' ' +
                " Buisness: " + description + ' ' +
                " Amount " + amount
                ;
    }
}