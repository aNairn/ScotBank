//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package uk.co.asepstrath.bank;

import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import io.jooby.annotation.QueryParam;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;

@Path({"/example"})
public class AccountController {
    private final DataSource dataSource;
    private final Logger logger;
    private final List<Account> accounts;

    public AccountController(DataSource ds, Logger log, List<Account> accounts) {
        this.dataSource = ds;
        this.logger = log;
        this.accounts = accounts;
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
