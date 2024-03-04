package uk.co.asepstrath.bank;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.jooby.MediaType;
import io.jooby.test.MockContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import javax.sql.DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnitTest {
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        // Initialize the accountController
        DataSource dataSource = Mockito.mock(DataSource.class);
        Logger logger = Mockito.mock(Logger.class);
        List<Account> accounts = new ArrayList<>();
        List<Transactions> transactions = new ArrayList<>();
        List<Business> businesses = new ArrayList<>();

        // Adding some dummy accounts for testing
        accounts.add(new Account("John", 100.00));
        accounts.add(new Account("Alice", 200.00));
        accounts.add(new Account("Bob", 300.00));

        accountController = new AccountController(dataSource, logger, accounts,transactions,businesses);
    }


    public void getHomePage() throws IOException, IOException {
        // Mocking Jooby's Context
        MockContext ctx = new MockContext();

        // Set up session data by directly setting attributes
        ctx.setAttribute("fromPost", "john_doe");

        // Mocking Handlebars and Template
        Handlebars handlebars = mock(Handlebars.class);
        Template template = mock(Template.class);
        when(handlebars.compile("templates/homepage")).thenReturn(template);

        // Mocking the accountController and its getter method for handlebars
        AccountController accountController = mock(AccountController.class);
        when(accountController.getHandlebars()).thenReturn(handlebars);

        // Call the method under test
        String html = accountController.getHomePage(ctx);

        // Assert the response

        assertEquals("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Accounts</title>
                    <style>
                        /* Styles for the navigation bar */
                        /* Style the navigation menu */
                .navbar {
                  width: 100%;
                  background-color: #555;
                  overflow: auto;
                  display: flex;
                  justify-content: center;
                }
                ul{
                list-style-type: none;
                }

                /* Navigation links */
                .navbar a {
                  float: left;
                  padding: 12px;
                  color: white;
                  text-decoration: none;
                  font-size: 17px;
                  width: 33.33%; /* Four equal-width links. If you have two links, use 50%, and 33.33% for three links, etc.. */
                  text-align: center; /* If you want the text to be centered */
                }

                /* Add a background color on mouse-over */
                .navbar a:hover {
                  background-color: #000;
                }

                /* Style the current/active link */


                /* Add responsiveness - on screens less than 500px, make the navigation links appear on top of each other, instead of next to each other */
                @media screen and (max-width: 500px) {
                  .navbar a {
                    float: none;
                    display:inline-block; ;
                    width: 100%;

                    text-align: center; /* If you want the text to be left-aligned on small screens */
                  }
                }
                        /* Additional styles to make the page look better */
                        body {
                            font-family: Arial, sans-serif;
                            margin: 0; /* Remove default margin */
                            padding: 0; /* Remove default padding */
                        }

                        .content {
                            padding: 20px; /* Add padding to content area */
                            border: 1px solid #ccc;
                            border-radius: 5px;

                            margin-left: 2%;
                            margin-right: 5%;
                            margin-top: 3%;
                        }
                        #welcome-title{
                        margin-left: 2%;
                        }
                    </style>
                </head>
                <body>

                <div class="navbar">
                    <a href="/homepage">Account</a>
                    <a href="/transactions" >Transactions</a>
                    <a href="/spending">Spending</a>
                </div>

                <h1 id="welcome-title">Welcome back {{fromPost}}</h1>
                <div class="content">
                    <p>Account Details:</p>
                    <ul>
                        <li>Amount: £{{amount}}</li>
                        <li>Sort-Code: {{sortCode}}</li>
                        <li>Account-Number: {{accountNumber}}</li>
                    </ul>
                </div>

                </body>
                </html>
                """, html); // Replace "Expected HTML content here" with your expected HTML content
        assertEquals(MediaType.text.html, ctx.getResponseType());
    }

}

