//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.annotation.Path;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import java.util.UUID;

@Path({"/example"})
public class App extends Jooby {
    List<Account> accounts = new ArrayList();
    double[] accountAmounts = new double[]{50.0, 100.0, 76.0, 23.9, 3.0, 54.32};
    String[] accountNames = new String[]{"Rachel", "Monica", "Phoebe", "Joey", "Chandler", "Ross"};

    public App() {
        this.install(new UniRestExtension());
        this.install(new HandlebarsModule());
        this.install(new HikariModule("mem"));
        this.assets("/assets/*", "/assets");
        this.assets("/service_worker.js", "/service_worker.js");
        DataSource ds = (DataSource)this.require(DataSource.class);
        Logger log = this.getLog();
        this.mvc(new AccountController(ds, log, this.accounts));
        this.onStarted(() -> {
            this.onStart();
        });
        this.onStop(() -> {
            this.onStop();
        });
    }

    public void importAccDataFromAPI(String apiUrl){
        try{
            //Create HttpClient
            HttpClient client = HttpClient.newHttpClient();
            //Creare GET request
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).header("Accept", "application/json").build();
            //Send/Receive
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                JSONArray jsonArray = new JSONArray(new JSONTokener(response.body()));
                System.out.println("API Response:\n" + response.body());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject accountJson = jsonArray.getJSONObject(i);

                        String id = accountJson.getString("id");
                        String name = accountJson.getString("name");
                        double startingBalance = accountJson.getDouble("startingBalance");
                        boolean roundUpEnabled = accountJson.getBoolean("roundUpEnabled");
                        // Create an Account object using the constructor for API data
                        Account account = new Account(UUID.fromString(id), name, startingBalance, roundUpEnabled);
                    System.out.println("Parsed JSON Array:\n" + jsonArray);

                        // Add the account to your accounts list
                        accounts.add(account);
                    System.out.println("Adding account: " + account);
                    }
            }else{
                throw new Error("Failed to fetch data from the API. HTTP status code: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {
        App app = new App();
        app.importAccDataFromAPI("https://api.asep-strath.co.uk/api/accounts");
        runApp(args, App::new);
    }

    public void onStart() {
        Logger log = this.getLog();
        log.info("Starting Up...");

        for(int i = 0; i < this.accountNames.length; ++i) {
            this.accounts.add(new Account(this.accountNames[i], this.accountAmounts[i]));
        }

        //Results.html("accountTemplate").put("accounts",accounts);
        // Print the accounts list
        System.out.println("Accounts after importing from API:");
        for (Account account : accounts) {
            System.out.println(account);
        }

        DataSource ds = (DataSource)this.require(DataSource.class);

        try {
            Connection connection = ds.getConnection();

            try {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("CREATE TABLE `Example` (`Key` varchar(255),`Value` varchar(255))");
                stmt.executeUpdate("INSERT INTO Example VALUES ('WelcomeMessage', 'Welcome to A Bank')");
            } catch (Throwable var7) {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }
                }

                throw var7;
            }

            if (connection != null) {
                connection.close();
            }
        } catch (SQLException var8) {
            log.error("Database Creation Error", var8);
        }

    }

    public void onStop() {
        System.out.println("Shutting Down...");
    }
}
