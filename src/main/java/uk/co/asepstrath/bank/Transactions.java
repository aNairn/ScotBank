package uk.co.asepstrath.bank;

public class Transactions {
    private String timestamp;
    private double amount;
    private String from;
    private String id;
    private String to;
    private String type;

    public Transactions(String timestamp, double amount, String from, String id, String to, String type) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.from = from;
        this.id = id;
        this.to = to;
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    //public void setTimestamp(String timestamp) { this.timestamp = timestamp;}

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    //public void setFrom(String from) { this.from = from;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "timestamp='" + timestamp + '\'' +
                ", amount=" + amount +
                ", from='" + from + '\'' +
                ", id='" + id + '\'' +
                ", to='" + to + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

