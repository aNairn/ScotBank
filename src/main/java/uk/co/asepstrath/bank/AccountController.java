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
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;

@Path({"/example"})
public class AccountController {
    private final DataSource dataSource;
    private final Logger logger;
    private final List<Account> accounts;

    private Handlebars handlebars;



    public AccountController(DataSource ds, Logger log, List<Account> accounts) {
        this.dataSource = ds;
        this.logger = log;
        this.accounts = accounts;
        this.handlebars = new Handlebars();
    }

    @GET("/templateTest")
    public String getAccountz(Context ctx) throws IOException {


        Template template = handlebars.compile("templates/accounts");


        String html = template.apply(accounts);

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
}
