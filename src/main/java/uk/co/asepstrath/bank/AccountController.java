//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package uk.co.asepstrath.bank;
import io.jooby.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.jooby.MediaType;
import io.jooby.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;



@Path({"/"})
public class AccountController {
    private final List<Transaction> transactions;

    private final DataSource dataSource;
    private final Logger logger;
    private final List<Account> accounts;


    public Handlebars handlebars;



    public AccountController(DataSource ds, Logger log, List<Account> accounts) {
        this.dataSource = ds;
        this.logger = log;
        this.accounts = accounts;
        this.transactions = new ArrayList<>();
        // Fill the transactions list with sample transactions
        transactions.add(new Transaction("Tesco", 100.0, "Supermarket"));
        transactions.add(new Transaction("Asda", 200.0, "Supermarket"));
        transactions.add(new Transaction("Sainsburys", 300.0, "Supermarket"));
        transactions.add(new Transaction("Costco", 10000.0, "Wholesaler"));
        transactions.add(new Transaction("EE", 30.0, "Technology"));
        transactions.add(new Transaction("O2", 45.0, "Technology"));



        this.handlebars = new Handlebars();

    }

    @GET("/")
    public String getLoginPage(Context ctx) throws IOException {

            Template template = handlebars.compile("templates/login");


        String html = template.apply(accounts);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.text.html);
        return html;
    }
    @GET({"/homepage"})
    public String getHomePage(Context ctx) throws IOException {



/// Retrieve the username from the session
        String username = ctx.session().get("fromPost").value();

        // Render the homepage template with the username
        Template template = handlebars.compile("templates/homepage");

        // Create a model object with the username
        Map<String, Object> model = new HashMap<>();
        model.put("fromPost", username);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.text.html);
        return html;
    }


    @GET({"/accounts"})
    public List<Account> getAccounts() {
        return accounts;
    }

    @POST({"/accounts"})
    public Account createAccount(@QueryParam String name, @QueryParam double accountAmount) {
        Account account = new Account(name, accountAmount);
        this.accounts.add(account);
        return account;
    }

    @POST("/")
    public String getLogin(Context ctx) throws IOException {
        String fromPost = ctx.form("username").value();
        System.out.println(fromPost);
        Template template = handlebars.compile("templates/homepage");


        String html = template.apply(fromPost);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.text.html);
        return html;
    }
    @POST("/homepage")
    public void handleLoginFormSubmission(Context ctx) throws IOException {
        String username = ctx.form("username").value();

        // Store the username in the session
        ctx.session().put("fromPost", username);

        // Redirect to the homepage
        ctx.sendRedirect("/homepage");
    }

    @GET("/transactions")
    public String getTransactionsPage(Context ctx) throws IOException {
        // Render the transactions page template with the transactions data
        Template template = handlebars.compile("templates/transactions");

        // Create a model object with the transactions data
        Map<String, Object> model = new HashMap<>();
        model.put("transactions", transactions);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.text.html);
        return html;
    }
    @GET("/spending")
    public String getSpendingPage(Context ctx) throws IOException {
        String username = ctx.session().get("fromPost").value();

        // Calculate spending summary
        Map<String, Double> spendingSummary = calculateSpendingSummary();

        // Render the spending page template with the spending summary and username
        Template template = handlebars.compile("templates/spending");

        // Create a model object with the spending summary and username
        Map<String, Object> model = new HashMap<>();
        model.put("fromPost", username);
        model.put("spendingSummary", spendingSummary);

        String html = template.apply(model);

        // Set response type and return HTML
        ctx.setResponseType(MediaType.text.html);
        return html;
    }

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

}
