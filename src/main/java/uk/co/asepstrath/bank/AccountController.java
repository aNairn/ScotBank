//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package uk.co.asepstrath.bank;
import io.jooby.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.jooby.MediaType;
import io.jooby.StatusCode;
import io.jooby.annotation.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.slf4j.Logger;



@Path({"/"})
public class AccountController {
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
        for (Account account : accounts) {
            Map<String, String> formattedAccount = new HashMap<>();
            formattedAccount.put("id", account.getId().toString());
            formattedAccount.put("name", account.getName());
            formattedAccount.put("startingBalance", String.valueOf(account.getBalance())); // Convert to String
            formattedAccount.put("roundUpEnabled", String.valueOf(account.isRoundUpEnabled()));
            formattedAccounts.add(formattedAccount);
        }

        model.put("accounts", formattedAccounts);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;
    }


    private List<Account> getAccountsFromDatabase() {
        List<Account> accounts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts");
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
            e.printStackTrace();
        }

        return accounts;
    }








    @GET("/homepage")
    public String getHomePage(Context ctx) throws IOException {
        String username = ctx.session().get("fromPost").value();

        // Retrieve user's name from the database based on UUID
        String name = getUserNameFromUUID(username);

        double amount = getStartingAmountFromUUID(username);
        // Retrieve account details (replace these with actual values from your system)

        String sortCode = "12-34-56";
        String accountNumber = "12345678";

        // Render the homepage template with the username and account details
        Template template = handlebars.compile("views/templates/homepage");

        // Create a model object with the username and account details
        Map<String, Object> model = new HashMap<>();
        model.put("fromPost", name); // Pass the retrieved name instead of UUID
        model.put("amount", amount);
        model.put("sortCode", sortCode);
        model.put("accountNumber", accountNumber);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;
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
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM Accounts WHERE id = ?");
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if user not found or any error occurs
    }

    private double getStartingAmountFromUUID(String uuid) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT startingBalance FROM Accounts WHERE id = ?");
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("startingBalance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM Accounts WHERE id = ?");
            preparedStatement.setString(1, providedUUID);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // If there's a matching UUID in the database, return true
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // If an exception occurs or no matching UUID is found, return false
        }
    }
    @GET("/transactions")
    public String getTransactionsPage(Context ctx) throws IOException {
        String fromPost = ctx.session().get("fromPost").value();

        // Filter transactions based on the 'from' field
        List<Transactions> filteredTransactions = filterTransactionsByFrom(fromPost);

        // Render the transactions page template with the filtered transactions data
        Template template = handlebars.compile("views/templates/transactions");
        Map<String, Object> model = new HashMap<>();
        model.put("transactions", filteredTransactions);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;
    }

    private List<Transactions> filterTransactionsByFrom(String fromPost) {
        List<Transactions> filteredTransactions = new ArrayList<>();
        for (Transactions transaction : transactions) {
            if (transaction.getFrom().equals(fromPost)) {
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions;
    }

  /*  @GET("/spending")
    public String getSpendingPage(Context ctx) throws IOException {
        String username = ctx.session().get("fromPost").value();

        // Calculate spending summary
        Map<String, Double> spendingSummary = calculateSpendingSummary();

        // Render the spending page template with the spending summary and username
        Template template = handlebars.compile("views/templates/spending");

        // Create a model object with the spending summary and username
        Map<String, Object> model = new HashMap<>();
        model.put("fromPost", username);
        model.put("spendingSummary", spendingSummary);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.html);
        return html;
    }*/
/*
    private Map<String, Double> calculateSpendingSummary() {
        Map<String, Double> spendingSummary = new HashMap<>();

        // Iterate through transactions and categorize spending
        for (Transaction transaction : transactions) {
            String category = transaction.getCategory(); // Assuming Transaction class has getCategory method
            double amount = transaction.getAmount(); // Assuming Transaction class has getAmount method

            // Update spending summary
            spendingSummary.put(category, spendingSummary.getOrDefault(category, 0.0) + amount);
        }

        return spendingSummary;
    }
    */
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

    private Transactions findTransactionById(String transactionId) {
        for (Transactions transaction : transactions) {
            if (transaction.getId().equals(transactionId)) {
                return transaction;
            }
        }
        return null;
    }

    private String getBusinessNameById(String businessId) {
        for (Business business : businesses) {
            if (business.getbusinessID().equals(businessId)) {
                return business.getBuisnessName();
            }
        }
        return null; // Return null if business not found
    }

}
