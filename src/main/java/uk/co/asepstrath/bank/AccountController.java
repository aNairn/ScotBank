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

        for (Account account : accounts) {
            System.out.println(account);
        }

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
            formattedAccount.put("startingBalance", df.format(account.getBalance())); // Convert to String with two decimal places
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


   // @GET({"/accounts"})
   // public List<Account> getAccounts() {
       // return accounts;
    //}

  /*  @GET({"/accounts"})
    public List<Map<String, String>> getAccounts() {
        List<Map<String, String>> accountsFromDB = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Example");

            while (resultSet.next()) {
                Map<String, String> account = new HashMap<>();
                account.put("key", resultSet.getString("Key"));
                account.put("value", resultSet.getString("Value"));
                accountsFromDB.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accountsFromDB;
    }
*/

    /*
    @POST({"/accounts"})
    public Account createAccount(@QueryParam String name, @QueryParam double accountAmount) {
        Account account = new Account(name, accountAmount);
        this.accounts.add(account);
        return account;
    }*/

    @POST("/")
    public String getLogin(Context ctx) throws IOException {
       // String fromPost = ctx.form("username").value();
       // System.out.println(fromPost);
       // Template template = handlebars.compile("views/templates/homepage");


       // String html = template.apply(fromPost);

        // Set response type and return HTML
       // ctx.setResponseType(MediaType.text.html);
       // return html;

       // ~~~~~~~~~~~~~~~~~~~~~~~~~~

        //new one to get name from login

        String fromPost = ctx.form("username").value();
        System.out.println("Submitted username: " + fromPost); // Add this line for debugging

        // Check if the user is the manager



        // Retrieve user's name from the database based on UUID
        String username = getUserNameFromUUID(fromPost);

        // Pass the retrieved name to the homepage template
        Template template = handlebars.compile("views/templates/homepage");
        Map<String, Object> model = new HashMap<>();
        model.put("fromPost", username);
        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;

    }


    private String getUserNameFromUUID(String uuid) {
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

    private double getStartingAmountFromUUID(String uuid) {
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
            ctx.send(errorMessage);
        }
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

        // Filter transactions based on the 'from' field
        List<Transactions> filteredTransactions = filterTransactionsByFrom(fromPost);

        // Calculate starting amount
        double startingAmount = getStartingAmountFromUUID(fromPost);

        // Calculate current amount after transactions
        double currentAmount = calculateCurrentAmount(startingAmount, filteredTransactions);

        DecimalFormat df = new DecimalFormat("0.00");
        String formattedStartingAmount = df.format(startingAmount);
        String formattedCurrentAmount = df.format(currentAmount);
        // Render the transactions page template with the filtered transactions data
        Template template = handlebars.compile("views/templates/transactions");
        Map<String, Object> model = new HashMap<>();
        model.put("transactions", filteredTransactions);
        model.put("startingAmount", startingAmount);
        model.put("currentAmount", currentAmount);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;
    }

    private double calculateCurrentAmount(double startingAmount, List<Transactions> transactions) {
        double currentAmount = startingAmount;
        for (Transactions transaction : transactions) {
            if(transaction.getTimestamp().contains(""))
            currentAmount -= transaction.getAmount();
        }
        return currentAmount;
    }


    private List<Transactions> filterTransactionsByFrom(String fromPost) { ////////////
        List<Transactions> filteredTransactions = new ArrayList<>();
        for (Transactions transaction : transactions) {
            if (transaction.getFrom().equals(fromPost)) {
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions;
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

    private Map<String, Double> calculateSpendingSummary(String username) {
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
            e.printStackTrace();
        }

        return spendingSummary;
    }

    @POST("/roundup")
    public void toggleRoundUp(Context ctx) {
        boolean roundUpEnabled = ctx.body().booleanValue();
        String username = ctx.session().get("fromPost").value();

        // Update round-up status in the database
        updateRoundUpStatus(username, roundUpEnabled);
    }

    void updateRoundUpStatus(String username, boolean roundUpEnabled) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Accounts SET roundUpEnabled = ? WHERE id = ?");
            preparedStatement.setBoolean(1, roundUpEnabled);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @GET("/bigspenders")
    public String getSanctionedBusinessesReport(Context ctx) throws IOException {
        // Fetch all transactions related to sanctioned businesses
        List<Transactions> sanctionedTransactions = getSanctionedTransactions();

        // Aggregate transactions by business
        Map<String, String> aggregatedReport = aggregateTransactionsByBusiness(sanctionedTransactions);

        // Render the report template with the aggregated data
        Template template = handlebars.compile("views/templates/sanctionedBusinessesReport");
        Map<String, Object> model = new HashMap<>();
        model.put("report", aggregatedReport);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;
    }

    private List<Transactions> getSanctionedTransactions() {
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
            e.printStackTrace();
        }

        return sanctionedTransactions;
    }

    private Map<String, String> aggregateTransactionsByBusiness(List<Transactions> transactions) {
        Map<String, String> aggregatedReport = new HashMap<>();

        DecimalFormat df = new DecimalFormat("0.00");

        for (Transactions transaction : transactions) {
            String businessId = transaction.getTo();
            double amount = Math.round(transaction.getAmount() * 1) / 100.0;
            String businessName = getBusinessNameById(businessId);
            String formattedAmount = df.format(amount);

            if (aggregatedReport.containsKey(businessName)) {
                double currentAmount = Double.parseDouble(aggregatedReport.get(businessName));
                aggregatedReport.put(businessName, df.format(currentAmount + amount));
            } else {
                aggregatedReport.put(businessName, formattedAmount);
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
