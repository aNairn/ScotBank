import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.jooby.MediaType;
import io.jooby.test.MockContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.AccountController;

import javax.sql.DataSource;

import io.jooby.test.MockRouter;
import uk.co.asepstrath.bank.App;
import io.jooby.StatusCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnitTest {
<<<<<<< HEAD
    @Test
    public void welcome() {
        MockRouter router = new MockRouter(new App());
        router.get("/example", rsp -> {
            assertEquals("Welcome to Jooby!", rsp.value());
            assertEquals(StatusCode.OK, rsp.getStatusCode());
        });
=======
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        // Initialize the accountController
        DataSource dataSource = Mockito.mock(DataSource.class);
        Logger logger = Mockito.mock(Logger.class);
        List<Account> accounts = new ArrayList<>();
        // Adding some dummy accounts for testing
        accounts.add(new Account("John", 100.00));
        accounts.add(new Account("Alice", 200.00));
        accounts.add(new Account("Bob", 300.00));

        accountController = new AccountController(dataSource, logger, accounts);
    }

    @Test
    public void getHomePage() throws IOException {
        // Mocking Jooby's Context
        MockContext ctx = new MockContext();

        // Set up session data by directly setting attributes
        ctx.setAttribute("fromPost", "john_doe");

        // Mocking Handlebars and Template
        Handlebars handlebars = mock(Handlebars.class);
        Template template = mock(Template.class);
        when(handlebars.compile("templates/homepage")).thenReturn(template);
        when(accountController.getHandlebars()).thenReturn(handlebars);

        // Call the method under test
        String html = accountController.getHomePage(ctx);

        // Assert the response
        assertEquals("Expected HTML content here", html); // Replace "Expected HTML content here" with your expected HTML content
        assertEquals(MediaType.text.html, ctx.getResponseType());
>>>>>>> a51f68ec3c357f63ebfe1f5e319a7b6c02daca5d
    }
}
