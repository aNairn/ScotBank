//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package uk.co.asepstrath.bank;
import io.jooby.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.jooby.Jooby;
import io.jooby.MediaType;
import io.jooby.StatusCode;
import io.jooby.annotation.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.slf4j.Logger;
import java.text.DecimalFormat;



@Path({"/"})
public class AccountController extends Jooby {
    private final List<Transactions> transactions;
    private final List<Business> businesses;
    private final DataSource dataSource;
    private final Logger logger;
    private final List<Account> accounts;


    private final Handlebars handlebars;



    public AccountController(DataSource ds, Logger log, List<Account> accounts, List<Transactions> transactions, List<Business> businesses) {
        this.dataSource = ds;
        this.logger = log;
        this.accounts = accounts;
        this.transactions = transactions;
        this.businesses = businesses;

        // Fill the transactions list with sample transactions





        this.handlebars = new Handlebars();

    }


    public Handlebars getHandlebars() {
        return this.handlebars;
    }

    @GET("/")
    public String getLoginPage(Context ctx) throws IOException {

        Template template = handlebars.compile("views/templates/login");


        String html = template.apply(accounts);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;
    }

    @GET("/manager")
    public String getAccountsPage(Context ctx) throws IOException {
        // Fetch account details from the database
        List<Account> accounts = getAccountsFromDatabase();

        // Render the manager.hbs template with the account details
        Template template = handlebars.compile("views/templates/managerView");

        // Create a model object with the account details
        Map<String, Object> model = new HashMap<>();
        List<Map<String, String>> formattedAccounts = new ArrayList<>();

        // Convert startingBalance to String representation in the model
        DecimalFormat df = new DecimalFormat("0.00");

        for (Account account : accounts) {
            Map<String, String> formattedAccount = new HashMap<>();
            formattedAccount.put("id", account.getId().toString());
            formattedAccount.put("name", account.getName());
            formattedAccount.put("currentAmount", df.format(calculateCurrentAmountFromTransactions(account.getId().toString()))); // Get current amount for each account
            formattedAccount.put("roundUpEnabled", String.valueOf(account.isRoundUpEnabled()));
            formattedAccounts.add(formattedAccount);
        }

        model.put("accounts", formattedAccounts);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;
    }


    List<Account> getAccountsFromDatabase() {
        List<Account> accounts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts")) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                String name = resultSet.getString("name");
                double startingBalance = resultSet.getDouble("startingBalance");
                boolean roundUpEnabled = resultSet.getBoolean("roundUpEnabled");

                // Create Account object and add it to the list
                Account account = new Account(id, name, startingBalance, roundUpEnabled);
                accounts.add(account);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("Error connecting to database");
        }

        return accounts;
    }

    @GET("/homepage")
    public String getHomePage(Context ctx) throws IOException {
        String username = ctx.session().get("fromPost").value();

        // Retrieve user's name from the database based on UUID
        String name = getUserNameFromUUID(username);

        // Retrieve current amount based on transactions
        double currentAmount = calculateCurrentAmountFromTransactions(username);

        // Format currentAmount to display with two decimal places
        DecimalFormat df = new DecimalFormat("0.00");
        String formattedCurrentAmount = df.format(currentAmount);

        // Retrieve account details (replace these with actual values from your system)
        String sortCode = generateSortCode(UUID.fromString(username));
        String accountNumber = generateAccountNumber(UUID.fromString(username));

        // Render the homepage template with the username and account details
        Template template = handlebars.compile("views/templates/homepage");

        // Create a model object with the username and account details
        Map<String, Object> model = new HashMap<>();
        model.put("fromPost", name); // Pass the retrieved name instead of UUID
        model.put("currentAmount", formattedCurrentAmount); // Use the formatted currentAmount
        model.put("sortCode", sortCode);
        model.put("accountNumber", accountNumber);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;
    }

    double calculateCurrentAmountFromTransactions(String username) {
        // Logic to calculate current amount based on transactions
        double startingAmount = getStartingAmountFromUUID(username);
        List<Transactions> userTransactions = filterTransactionsByFrom(username);
        double currentAmount = startingAmount;

        for (Transactions transaction : userTransactions) {
            currentAmount -= transaction.getAmount();
        }

        return currentAmount;
    }

    public static String generateAccountNumber(UUID uuid) {
        String uuidString = uuid.toString().replaceAll("-", "");
        StringBuilder accountNumber = new StringBuilder("");
        for (int i = 0; i < 8; i++) { // Changed to 8 characters
            char currentChar = uuidString.charAt(i);
            if (!Character.isDigit(currentChar)) {
                int mappedValue = (Character.getNumericValue(currentChar) - Character.getNumericValue('A') + 10) % 10; // Ensure single digit
                accountNumber.append(mappedValue);
            } else {
                accountNumber.append(currentChar);
            }
        }
        return accountNumber.toString();
    }

    public static String generateSortCode(UUID uuid) {
        String uuidString = uuid.toString().replaceAll("-", "");
        StringBuilder sortCode = new StringBuilder("");
        for (int i = 8; i < 14; i++) { // Changed to 14 characters
            char currentChar = uuidString.charAt(i);
            if (!Character.isDigit(currentChar)) {
                int mappedValue = (Character.getNumericValue(currentChar) - Character.getNumericValue('A') + 10) % 10; // Ensure single digit
                sortCode.append(mappedValue);
            } else {
                sortCode.append(currentChar);
            }
            if ((i - 8) % 2 == 1 && i < 13) { // Add hyphen after every two characters, except for the last group
                sortCode.append("-");
            }
        }
        return sortCode.toString();
    }

    String getUserNameFromUUID(String uuid) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM Accounts WHERE id = ?")) {


            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("Error connecting to database");
        }
        return null; // Return null if user not found or any error occurs
    }

    double getStartingAmountFromUUID(String uuid) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT startingBalance FROM Accounts WHERE id = ?")) {

            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("startingBalance");
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("Error connecting to database");
        }
        return 0.0; // Return 0.0 if user not found or any error occurs
    }
    @POST("/homepage")
    public void handleLoginFormSubmission(Context ctx) throws IOException {
        String username = ctx.form("username").value();

        boolean isValidUUID = validateUUID(username);
        // Check if username is not empty before storing it in the session
        if("MANAGER".equals(username)){
            ctx.session().put("fromPost", username);

            // Redirect to the homepage
            ctx.sendRedirect("/manager");
        }
      else  if (username != null && !username.isEmpty() && isValidUUID) {
            // Store the username in the session
            ctx.session().put("fromPost", username);

            // Redirect to the homepage
            ctx.sendRedirect("/homepage");
        }

        else {
            // Handle the case when username is empty
            String errorMessage = "Error: Username cannot be empty and must be in database";
            ctx.setResponseType(MediaType.TEXT);
            ctx.setResponseCode(StatusCode.BAD_REQUEST);
            ctx.sendRedirect("/loginError");
           // ctx.send(errorMessage);
           // ctx.sendRedirect("/loginError");

        }
    }

    @GET("/loginError")
    public String getLoginError(Context ctx) throws IOException {
        Template template = handlebars.compile("views/templates/loginError");


        String html = template.apply(accounts);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;
    }



        private boolean validateUUID(String providedUUID) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM Accounts WHERE id = ?")) {
            preparedStatement.setString(1, providedUUID);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // If there's a matching UUID in the database, return true
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("Error connecting to database");
            return false; // If an exception occurs or no matching UUID is found, return false
        }
    }
    @GET("/transactions")
    public String getTransactionsPage(Context ctx) throws IOException {
        String fromPost = ctx.session().get("fromPost").value();

        // Retrieve all transactions involving the specified account
        List<Transactions> relevantTransactions = getAllTransactionsInvolvingAccount(fromPost);

        // Calculate starting amount
        double startingAmount = getStartingAmountFromUUID(fromPost);

        // Calculate current amount after transactions
        double currentAmount = calculateCurrentAmount(startingAmount, relevantTransactions, fromPost);

        DecimalFormat df = new DecimalFormat("0.00");
        String formattedStartingAmount = df.format(startingAmount);
        String formattedCurrentAmount = df.format(currentAmount);

        // Filter out outgoing and incoming transactions (excluding deposits)
        List<Transactions> outgoingTransactions = getOutgoingTransactions(relevantTransactions, fromPost);
        List<Transactions> incomingTransactions = getIncomingTransactions(relevantTransactions, fromPost);

        for (Transactions transaction : relevantTransactions) {
            transaction.setAmountFormatted();
        }
        // Render the transactions page template with the filtered transactions data
        Template template = handlebars.compile("views/templates/transactions");
        Map<String, Object> model = new HashMap<>();
        model.put("outgoingTransactions", outgoingTransactions);
        model.put("incomingTransactions", incomingTransactions);
        model.put("startingAmount", formattedStartingAmount);
        model.put("currentAmount", formattedCurrentAmount);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;
    }

    List<Transactions> getOutgoingTransactions(List<Transactions> transactions, String accountUUID) {
        List<Transactions> outgoingTransactions = new ArrayList<>();
        for (Transactions transaction : transactions) {
            if (transaction.getFrom().equals(accountUUID) && !isDeposit(transaction)) {
                outgoingTransactions.add(transaction);
            }
        }
        return outgoingTransactions;
    }

    List<Transactions> getIncomingTransactions(List<Transactions> transactions, String accountUUID) {
        List<Transactions> incomingTransactions = new ArrayList<>();
        for (Transactions transaction : transactions) {
            if (transaction.getTo().equals(accountUUID) && !isDeposit(transaction)) {
                incomingTransactions.add(transaction);
            }
        }
        return incomingTransactions;
    }

    private boolean isDeposit(Transactions transaction) {
        return transaction.getType().equals("DEPOSIT");
    }



    List<Transactions> getAllTransactionsInvolvingAccount(String accountUUID) {
        List<Transactions> relevantTransactions = new ArrayList<>();
        for (Transactions transaction : transactions) {
            if (transaction.getFrom().equals(accountUUID) || transaction.getTo().equals(accountUUID)) {
                relevantTransactions.add(transaction);
            }
        }
        return relevantTransactions;
    }

    double calculateCurrentAmount(double startingAmount, List<Transactions> transactions, String accountUUID) {
        double currentAmount = startingAmount;
        for (Transactions transaction : transactions) {
            String transactionType = transaction.getType();
            switch (transactionType) {
                case "PAYMENT":
                case "WITHDRAWAL":
                    if (transaction.getFrom().equals(accountUUID)) {
                        currentAmount -= transaction.getAmount();
                    }
                    break;
                case "DEPOSIT":
                case "COLLECT_ROUNDUPS":
                    if (transaction.getTo().equals(accountUUID)) {
                        currentAmount += transaction.getAmount();
                    }
                    break;
                case "TRANSFER":
                    if (transaction.getFrom().equals(accountUUID)) {
                        currentAmount -= transaction.getAmount();
                    } else if (transaction.getTo().equals(accountUUID)) {
                        currentAmount += transaction.getAmount();
                    }
                    break;
                default:
                    // Handle unsupported transaction types
                    break;
            }
        }
        return currentAmount;
    }

    List<Transactions> filterTransactionsByFrom(String fromPost) { ////////////
        List<Transactions> filteredTransactions = new ArrayList<>();
        for (Transactions transaction : transactions) {
            if (transaction.getFrom().equals(fromPost)) {
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions;
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @GET("/spending")
    public String getSpendingPage(Context ctx) throws IOException {
        String username = ctx.session().get("fromPost").value();

        // Calculate spending summary
        Map<String, Double> spendingSummary = calculateSpendingSummary(username);

        // Render the spending page template with the spending summary and username
        Template template = handlebars.compile("views/templates/spending");

        // Create a model object with the spending summary and username
        Map<String, Object> model = new HashMap<>();

        model.put("spendingSummary", spendingSummary);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;
    }

    Map<String, Double> calculateSpendingSummary(String username) {
        Map<String, Double> spendingSummary = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT b.category, SUM(t.amount) AS totalAmount " +
                    "FROM transactions2 t " +
                    "JOIN businesses b ON t.receiver = b.id " +
                    "WHERE t.sender = ? " +
                    "GROUP BY b.category";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String category = resultSet.getString("category");
                double totalAmount = resultSet.getDouble("totalAmount")/10000;
                spendingSummary.put(category, totalAmount);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("Error connecting to database");
        }

        return spendingSummary;
    }


    @GET("/sanctionBusiness")
    public String getSanctionedBusinessesReport(Context ctx) throws IOException {
        // Fetch all transactions related to sanctioned businesses
        List<Transactions> sanctionedTransactions = getSanctionedTransactions();

        // Aggregate transactions by business
        Map<String, Map<String, Object>> aggregatedReport = aggregateTransactionsByBusiness(sanctionedTransactions);

        // Render the report template with the aggregated data
        Template template = handlebars.compile("views/templates/sanctionedBusinessesReport");
        Map<String, Object> model = new HashMap<>();
        model.put("report", aggregatedReport);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;
    }

    List<Transactions> getSanctionedTransactions() {
        List<Transactions> sanctionedTransactions = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transactions2 WHERE receiver IN (SELECT id FROM businesses WHERE sanctioned = true)");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Transactions transaction = new Transactions(
                        resultSet.getString("timestamp"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("sender"),
                        resultSet.getString("transaction_id"),
                        resultSet.getString("receiver"),
                        resultSet.getString("type")
                );
                sanctionedTransactions.add(transaction);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("Error connecting to database");
        }

        return sanctionedTransactions;
    }

    Map<String, Map<String, Object>> aggregateTransactionsByBusiness(List<Transactions> transactions) {
        Map<String, Map<String, Object>> aggregatedReport = new HashMap<>();

        for (Transactions transaction : transactions) {
            String businessId = transaction.getTo();
            String businessName = getBusinessNameById(businessId);

            if (!aggregatedReport.containsKey(businessName)) {
                aggregatedReport.put(businessName, new HashMap<>());
                aggregatedReport.get(businessName).put("transactions", new ArrayList<Transaction>());
                aggregatedReport.get(businessName).put("totalAmount", 0.0); // Initialize total amount to 0.0
            }

            List<Transactions> businessTransactions = (List<Transactions>) aggregatedReport.get(businessName).get("transactions");

            // Check if the transaction ID has already been displayed
            boolean isTransactionDisplayed = false;
            for (Transactions displayedTransaction : businessTransactions) {
                if (displayedTransaction.getId().equals(transaction.getId())) {
                    isTransactionDisplayed = true;
                    break;
                }
            }

            // If the transaction is not displayed, add it to the list and update total amount
            if (!isTransactionDisplayed) {
                businessTransactions.add(transaction);
                aggregatedReport.get(businessName).put("transactions", businessTransactions);
                double amount = transaction.getAmount();
                double currentAmount = (double) aggregatedReport.get(businessName).get("totalAmount");
                aggregatedReport.get(businessName).put("totalAmount", currentAmount + amount);
            }
        }

        return aggregatedReport;
    }

    @GET("/transactionsDetails")
  public String getTransactionDetailsPage(Context ctx) throws IOException {
      String transactionId = ctx.query("transactionId").value();
      Transactions transaction = findTransactionById(transactionId);

      String businessName = getBusinessNameById(transaction.getTo());

      // Render the transactionDetails page template with the transaction data
      Template template = handlebars.compile("views/templates/transactionDetails");
      Map<String, Object> model = new HashMap<>();
      model.put("transaction", transaction);
      model.put("business", businessName);


      String html = template.apply(model);

      // Set response type and return HTML
      ctx.setResponseType(MediaType.html);
      return html;
  }

    Transactions findTransactionById(String transactionId) {
        for (Transactions transaction : transactions) {
            if (transaction.getId().equals(transactionId)) {
                return transaction;
            }
        }
        return null;
    }

    String getBusinessNameById(String businessId) {
        for (Business business : businesses) {
            if (business.getBusinessID().equals(businessId)) {
                return business.getBusinessName();
            }
        }
        return null; // Return null if business not found
    }



}
