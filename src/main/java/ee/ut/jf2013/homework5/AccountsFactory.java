package ee.ut.jf2013.homework5;

import java.math.BigDecimal;

public class AccountsFactory {
    public static Account createAccount(int initialBalance) {
        Account account = new Account();
        account.balance = new BigDecimal(initialBalance);
        return account;
    }


    static class Account {
        private BigDecimal balance;

        private Account() {
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
