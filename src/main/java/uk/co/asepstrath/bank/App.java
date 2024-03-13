
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;
import org.slf4j.Logger;

@Path({"/example"})
public class App extends Jooby {
    private static final List<Account> accounts = Collections.synchronizedList(new ArrayList<>());
    private static final List<Business> businessList = Collections.synchronizedList(new ArrayList<>());
    private static final List<Transactions> transactionsList = Collections.synchronizedList(new ArrayList<>());;


    public App() {
        this.install(new UniRestExtension());
        this.install(new HandlebarsModule());
        this.install(new HikariModule("mem"));
        this.assets("/assets/*", "/assets");
        this.assets("/service_worker.js", "/service_worker.js");
        DataSource ds = (DataSource)this.require(DataSource.class);
        Logger log = this.getLog();
        this.mvc(new AccountController(ds, log, this.accounts,this.transactionsList,this.businessList));
        this.onStarted(() -> {
            this.onStart();
        });
        this.onStop(() -> {
            this.onStop();
        });
    }

    String fetchOAuth2Token() throws Exception {
        String authHeaderValue = "Basic " + Base64.getEncoder().encodeToString("scotbank:this1password2is3not4secure".getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.asep-strath.co.uk/oauth2/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", authHeaderValue)
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            return jsonResponse.getString("access_token");
        } else {
            throw new RuntimeException("Failed to fetch OAuth2 token. HTTP status code: " + response.statusCode());
        }
    }

    // Method to make authenticated API calls
    private String makeAuthenticatedAPICall(String apiUrl, String accessToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("Failed to fetch data from the API. HTTP status code: " + response.statusCode());
        }
    }

    public static void importBusinessDataFromAPI(String apiUrl, String accessToken) {
        try {
            String apiResponse = new App().makeAuthenticatedAPICall(apiUrl, accessToken);

            // Create HttpClient
            HttpClient client = HttpClient.newHttpClient();
            // Create GET request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Accept", "text/csv")
                    .build();
            // Send/Receive
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                // Parse CSV response
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()))) {
                    String line;
                    // Skip the header row if needed
                    // reader.readLine();
                    while ((line = reader.readLine()) != null) {
                        // Split the CSV line by comma
                        String[] parts = line.split(",");
                        // Assuming the structure of the CSV is: id, name, category, sanctioned
                        String id = parts[0];
                        String name = parts[1];
                        String category = parts[2];
                        boolean sanctioned = Boolean.parseBoolean(parts[3]);
                        // Create a Business object
                        Business business = new Business(id, name, category, sanctioned);
                        // Add the business to the list
                        businessList.add(business);
                        System.out.println("Adding business: " + business);
                    }
                }
            } else {
                throw new Error("Failed to fetch data from the API. HTTP status code: " + response.statusCode());
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Failed to fetch data from the API.");
        }
    }

    public static void importTransactionDataFromAPI(String apiUrl, String accessToken, int size) {
        try {
            // Create HttpClient
            HttpClient client = HttpClient.newHttpClient();

            int page = 1; // Start from page 1

            while (true) {
                // Create GET request with pagination parameters
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl + "?page=" + page + "&size=" + size))
                        .header("Accept", "application/xml")
                        .build();

                // Send/Receive
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    // Convert XML response to JSON
                    JSONObject json = XML.toJSONObject(response.body());
                    // Extract 'pageResult' object from JSON
                    JSONObject pageResult = json.getJSONObject("pageResult");
                    if (pageResult.getBoolean("hasNext")) {
                        // Assuming 'results' is a JSON array
                        JSONArray results = pageResult.getJSONArray("results");
                        // Process each transaction
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject transactionJson = results.getJSONObject(i);
                            // Extract transaction details
                            String timestamp = transactionJson.getString("timestamp");
                            double amount = transactionJson.getDouble("amount");
                            String from = transactionJson.getString("from");
                            String id = transactionJson.getString("id");
                            String to = transactionJson.getString("to");
                            String type = transactionJson.getString("type");
                            // Process the transaction, e.g., save to database, etc.
                            // Example:
                            Transactions transaction = new Transactions(timestamp, amount, from, id, to, type);

                            transactionsList.add(transaction);
                            System.out.println("Adding transaction: " + transactionsList.get(i));

                            // Add the transaction to your list or process it as required
                            System.out.println("Processed transaction: " + transaction);
                        }
                        // Move to the next page
                        page++;
                    } else {
                        // No more pages, break out of the loop
                        break;
                    }
                } else {
                    throw new Error("Failed to fetch data from the API. HTTP status code: " + response.statusCode());
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Could not fetch data from API: " + e.getMessage());
        }
    }
    public static void importAccDataFromAPI(String apiUrl, String accessToken){


        try {
            String apiResponse = new App().makeAuthenticatedAPICall(apiUrl, accessToken);
            //Create HttpClient
            HttpClient client = HttpClient.newHttpClient();
            //Create GET request
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).header("Accept", "application/json").build();
            //Send/Receive
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
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
                    //System.out.println("Parsed JSON Array:\n" + jsonArray.get(i));

                    // Add the account to your accounts list
                    accounts.add(account);
                    System.out.println("Adding account: " + accounts.get(i));
                }

                System.out.println("The Selected Account: " + accounts.get(2));


            } else {
                throw new Error("Failed to fetch data from the API. HTTP status code: " + response.statusCode());
            }

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Unable to fetch data from the API.");
        }
    }

    public static List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public static void main(final String[] args) {
        App app = new App();
        //importAccDataFromAPI("https://api.asep-strath.co.uk/api/accounts");





        runApp(args, App::new);
    }

    public void onStart() throws Exception {
        String accessToken = fetchOAuth2Token();
        System.out.println("Accounts michael importing from API:");

        importAccDataFromAPI("https://api.asep-strath.co.uk/api/accounts", accessToken);
        importTransactionDataFromAPI("https://api.asep-strath.co.uk/api/transactions", accessToken,100); //uses xml then need to parse it through
         importBusinessDataFromAPI("https://api.asep-strath.co.uk/api/businesses", accessToken);

        //Get the accounts after importing from the API
        List<Account> accounts = getAccounts();
        System.out.println("Accounts after importing from API:");
        for (Account account : accounts) {
            System.out.println(account);
        }


        System.out.println("transactions after importing from API:");
        for(Transactions transactions : transactionsList){
        System.out.println(transactions);
    }
        System.out.println("businesses after importing from API:");
        for(Business business : businessList){
        System.out.println(business);
    }


        Logger log = this.getLog();
        log.info("Starting Up...");

        for (Account account : accounts) {

            DataSource ds = (DataSource)this.require(DataSource.class);

            try {
                Connection connection = ds.getConnection();

                try {
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS transactions2 (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "timestamp TIMESTAMP," +
                            "amount DECIMAL(10, 2)," +
                            "sender VARCHAR(255)," +
                            "receiver VARCHAR(255)," +
                            "transaction_id VARCHAR(255)," +
                            "type VARCHAR(255)" +
                            ")");
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Accounts (" +
                            "id VARCHAR(36) PRIMARY KEY," +     // Account ID
                            "name VARCHAR(255)," +              // Account holder's name
                            "startingBalance DECIMAL(10, 2)," + // Starting balance (assuming 2 decimal places)
                            "roundUpEnabled BOOLEAN)");         // Round-up enabled (true or false)

                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS businesses (" +
                            "id VARCHAR(36)," +  // Business ID
                            "name VARCHAR(255)," +           // Business name
                            "category VARCHAR(255)," +       // Business category
                            "sanctioned BOOLEAN)");          // Sanctioned (true or false)


                    String insertAccountSQL = "INSERT INTO Accounts (id, name, startingBalance, roundUpEnabled) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(insertAccountSQL)) {
                        pstmt.setString(1, account.getId().toString());
                        pstmt.setString(2, account.getName());
                        pstmt.setDouble(3, account.getBalance());
                        pstmt.setBoolean(4, account.isRoundUpEnabled());
                        pstmt.executeUpdate();
                    }

                    String insertTransactionsSQL = "INSERT INTO transactions2 (timestamp, amount, sender, receiver, transaction_id, type) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
                   try (PreparedStatement pstmt = connection.prepareStatement(insertTransactionsSQL)) {
                        for (Transactions transaction : transactionsList) {
                            pstmt.setString(1, transaction.getTimestamp());
                            pstmt.setDouble(2, transaction.getAmount());
                            pstmt.setString(3, transaction.getFrom());
                            pstmt.setString(4, transaction.getTo());
                            pstmt.setString(5, transaction.getId());
                            pstmt.setString(6, transaction.getType());
                            pstmt.executeUpdate();
                        }
                    }
                    String insertBusinessesSQL = "INSERT INTO businesses (id, name, category, sanctioned) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(insertBusinessesSQL)) {
                        for (Business business : businessList) {
                            pstmt.setString(1, business.getBusinessID());
                            pstmt.setString(2, business.getBusinessName());
                            pstmt.setString(3, business.getCategory());
                            pstmt.setBoolean(4, business.getBeenSanctioned());
                            pstmt.executeUpdate();
                        }
                    }



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
    }

    public void onStop() {
        System.out.println("Shutting Down...");
    }
}

