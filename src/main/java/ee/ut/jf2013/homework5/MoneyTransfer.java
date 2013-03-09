package ee.ut.jf2013.homework5;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import static ee.ut.jf2013.homework5.AccountsFactory.Account;

public class MoneyTransfer {
    public static void main(String[] args) {
        int n = 5;
        if (args.length > 0) {
            n = Integer.parseInt(args[0]);
        }

        final Collection<Account> accounts = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            Account account = AccountsFactory.createAccount(n);
            accounts.add(account);
        }

    }

    static synchronized boolean transferMoney(Account from, Account to, BigDecimal amount) {
        if (from.getBalance().compareTo(amount) < 0) {
            return false;
        }
        to.deposit(from.withdraw(amount));
        return true;
    }
}
