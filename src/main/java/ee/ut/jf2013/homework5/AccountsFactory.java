package ee.ut.jf2013.homework5;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ONE;

public class AccountsFactory {

    static List<Account> createdAccounts = new ArrayList<>();

    public static Account createAccount(int initialBalance) {
        Account account = new Account(createdAccounts);
        account.balance = new BigDecimal(initialBalance);
        createdAccounts.add(account);
        return account;
    }


    static class Account {
        private BigDecimal balance;

        Thread donator;

        private Account(final List<Account> createdAccounts) {
            donator = new Thread(new Runnable() {
                List<Account> recipients = createdAccounts;

                @Override
                public void run() {
                    for (Account recipient : recipients) {
                        if (recipient != Account.this)
                            try {
                                recipient.deposit(withdraw(ONE));
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                System.err.println("Interrupted exception occurred.");
                            }
                    }
                }
            });
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public BigDecimal withdraw(BigDecimal amount) {
            balance = balance.subtract(amount);
            return amount;
        }

        public void deposit(BigDecimal amount) {
            balance = balance.add(amount);
        }
    }
}
