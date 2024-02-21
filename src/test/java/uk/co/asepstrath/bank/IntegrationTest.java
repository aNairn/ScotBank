package uk.co.asepstrath.bank;

import io.jooby.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.jooby.Session;
import io.jooby.StatusCode;
import io.jooby.Value;
import io.jooby.test.JoobyTest;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;


import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@JoobyTest(App.class)
public class IntegrationTest {

    private final OkHttpClient client = new OkHttpClient();
/*
    @Test
    public void shouldRedirectToHomePageAfterSuccessfulLogin(int serverPort) throws IOException {
        // Make a POST request to simulate login
        Request loginReq = new Request.Builder()
                .url("http://localhost:" + serverPort + "/")
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "username=test"))
                .build();

        try (Response loginResponse = client.newCall(loginReq).execute()) {
            assertEquals("enter in html content", loginResponse.body().string());
            assertEquals(StatusCode.OK.value(), loginResponse.code());
        }

        // Make a GET request to the homepage after successful login
        Request homepageReq = new Request.Builder()
                .url("http://localhost:" + serverPort + "/homepage")
                .build();

        try (Response homepageResponse = client.newCall(homepageReq).execute()) {
            assertEquals("Expected HTML content for homepage", homepageResponse.body().string());
            assertEquals(StatusCode.OK.value(), homepageResponse.code());
        }
    }



*/@Test
void testGetHomePage() throws IOException {
// Mock username
String username = "testUsername";

    // Create a mock Value object
    Value sessionValue = mock(Value.class);
// Return the username when value() method of the sessionValue is called
    when(sessionValue.value()).thenReturn(username);

// Create a mock Session object
    Session session = mock(Session.class);
// Return the sessionValue when session.get("fromPost") method is called
    when(session.get("fromPost")).thenReturn(sessionValue);

    // Create a mock io.jooby.Context object
    io.jooby.Context ctx = mock(io.jooby.Context.class);
    when(ctx.session()).thenReturn(session);

    // Create a mock Handlebars object
    Handlebars handlebars = mock(Handlebars.class);
    when(handlebars.compile("templates/homepage")).thenReturn(mock(Template.class));

    // Create an instance of AccountController with mocked dependencies
    AccountController controller = new AccountController(mock(DataSource.class), mock(Logger.class), new ArrayList<>());
    controller.handlebars = handlebars;

    // Call the getHomePage method
    String result = controller.getHomePage(ctx);

    // Verify that the correct HTML is returned
    assertEquals(null, result);
}
    @Test
    public void testHandleLoginFormSubmissionWithEmptyUsername(int serverPort) throws IOException {
        // Create a form body with an empty username
        RequestBody formBody = new FormBody.Builder()
                .add("username", "")
                .build();

        // Create a request with form data
        Request request = new Request.Builder()
                .url("http://localhost:" + serverPort + "/homepage")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // Expecting a 400 Bad Request response
            assertEquals(StatusCode.BAD_REQUEST.value(), response.code());

            // Ensure the error message is correct
            assertEquals("Error: Username cannot be empty", response.body().string().trim());
        }
    }

    @Test
    public void shouldReturnLoginPage(int serverPort) throws IOException {
        Request req = new Request.Builder()
                .url("http://localhost:" + serverPort + "/")
                .build();

        try (Response rsp = client.newCall(req).execute()) {
            assertEquals("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <link rel=\"stylesheet\" href = \"styles/Styles.css\">\n" +
                    "\n" +
                    "    <title>Login Form</title>\n" +
                    "    <style>\n" +
                    "       body {\n" +
                    "    font-family: Arial, sans-serif;\n" +
                    "    color: rgb(7, 151, 151);\n" +
                    "    display: flex;\n" +
                    "    justify-content: center; /* Center horizontally */\n" +
                    "    align-items: center; /* Center vertically */\n" +
                    "    height: 100vh; /* Make the container full height of the viewport */\n" +
                    "    margin: 0; /* Remove default margin */\n" +
                    "}\n" +
                    "\n" +
                    ".container {\n" +
                    "    max-width: 400px;\n" +
                    "    padding: 20px;\n" +
                    "    border: 1px solid #ccc;\n" +
                    "    border-radius: 5px;\n" +
                    "    background-color: #e2b3ef;\n" +
                    "}\n" +
                    "\n" +
                    ".container h2 {\n" +
                    "    text-align: center;\n" +
                    "    margin-bottom: 20px; /* Add some space below the heading */\n" +
                    "}\n" +
                    "\n" +
                    ".form-group {\n" +
                    "    margin-bottom: 20px;\n" +
                    "}\n" +
                    "\n" +
                    ".form-group label {\n" +
                    "    display: block;\n" +
                    "    margin-bottom: 5px;\n" +
                    "}\n" +
                    "\n" +
                    ".form-group input {\n" +
                    "    width: 100%;\n" +
                    "    padding: 10px;\n" +
                    "    border: 1px solid #ccc;\n" +
                    "    border-radius: 5px;\n" +
                    "    margin-right: 5px;\n" +
                    "}\n" +
                    "\n" +
                    ".form-group input[type=\"submit\"] {\n" +
                    "    background-color: #9329d9;\n" +
                    "    color: rgb(255, 255, 255);\n" +
                    "    cursor: pointer;\n" +
                    "}\n" +
                    "\n" +
                    ".form-group input[type=\"submit\"]:hover {\n" +
                    "    background-color: #57197f;\n" +
                    "}\n" +
                    "\n" +
                    "#login {\n" +
                    "    margin-right: 20px;\n" +
                    "}\n" +
                    "\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div class=\"container\">\n" +
                    "    <h2>THE BANK</h2>\n" +
                    "    <form action=\"/homepage\" method=\"POST\">\n" +
                    "        <div class=\"form-group\">\n" +
                    "            <div id=\"login\">\n" +
                    "                <label for=\"username\">Unique ID:</label>\n" +
                    "                <input type=\"text\" id=\"username\" name=\"username\" required>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "\n" +
                    "        <div class=\"form-group\">\n" +
                    "            <input type=\"submit\" value=\"Login\" a href=\"example\">\n" +
                    "        </div>\n" +
                    "    </form>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>\n", rsp.body().string()); // Replace "Expected HTML content here" with your expected HTML content
            assertEquals(StatusCode.OK.value(), rsp.code());
        }
    }

    @Test
    public void shouldReturnHomePage(int serverPort) throws IOException {
        Request req = new Request.Builder()
                .url("http://localhost:" + serverPort + "/homepage")
                .build();

        try (Response rsp = client.newCall(req).execute()) {
            assertEquals("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Full-width Navigation Bar</title>\n" +
                    "    <style>\n" +
                    "        /* Styles for the navigation bar */\n" +
                    "        /* Style the navigation menu */\n" +
                    ".navbar {\n" +
                    "  width: 100%;\n" +
                    "  background-color: #555;\n" +
                    "  overflow: auto;\n" +
                    "  display: flex;\n" +
                    "  justify-content: center;\n" +
                    "}\n" +
                    "ul{\n" +
                    "list-style-type: none;\n" +
                    "}\n" +
                    "\n" +
                    "/* Navigation links */\n" +
                    ".navbar a {\n" +
                    "  float: left;\n" +
                    "  padding: 12px;\n" +
                    "  color: white;\n" +
                    "  text-decoration: none;\n" +
                    "  font-size: 17px;\n" +
                    "  width: 33.33%; /* Four equal-width links. If you have two links, use 50%, and 33.33% for three links, etc.. */\n" +
                    "  text-align: center; /* If you want the text to be centered */\n" +
                    "}\n" +
                    "\n" +
                    "/* Add a background color on mouse-over */\n" +
                    ".navbar a:hover {\n" +
                    "  background-color: #000;\n" +
                    "}\n" +
                    "\n" +
                    "/* Style the current/active link */\n" +
                    "\n" +
                    "\n" +
                    "/* Add responsiveness - on screens less than 500px, make the navigation links appear on top of each other, instead of next to each other */\n" +
                    "@media screen and (max-width: 500px) {\n" +
                    "  .navbar a {\n" +
                    "    float: none;\n" +
                    "    display:inline-block; ;\n" +
                    "    width: 100%;\n" +
                    "\n" +
                    "    text-align: center; /* If you want the text to be left-aligned on small screens */\n" +
                    "  }\n" +
                    "}\n" +
                    "        /* Additional styles to make the page look better */\n" +
                    "        body {\n" +
                    "            font-family: Arial, sans-serif;\n" +
                    "            margin: 0; /* Remove default margin */\n" +
                    "            padding: 0; /* Remove default padding */\n" +
                    "        }\n" +
                    "\n" +
                    "        .content {\n" +
                    "            padding: 20px; /* Add padding to content area */\n" +
                    "            border: 1px solid #ccc;\n" +
                    "            border-radius: 5px;\n" +
                    "\n" +
                    "            margin-left: 2%;\n" +
                    "            margin-right: 5%;\n" +
                    "            margin-top: 3%;\n" +
                    "        }\n" +
                    "        #welcome-title{\n" +
                    "        margin-left: 2%;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "<div class=\"navbar\">\n" +
                    "    <a href=\"/homepage\">Account</a>\n" +
                    "    <a href=\"/transactions\" >Transactions</a>\n" +
                    "    <a href=\"/spending\">Spending</a>\n" +
                    "</div>\n" +
                    "\n" +
                    "<h1 id=\"welcome-title\">Welcome back {{fromPost}}</h1>\n" +
                    "<div class=\"content\">\n" +
                    "    <p>Account Details:</p>\n" +
                    "    <ul>\n" +
                    "        <li>Amount: £{{amount}}</li>\n" +
                    "        <li>Sort-Code: {{sortCode}}</li>\n" +
                    "        <li>Account-Number: {{accountNumber}}</li>\n" +
                    "    </ul>\n" +
                    "</div>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>\n", rsp.body().string());
            assertEquals(StatusCode.OK.value(), rsp.code());
        }
    }
}