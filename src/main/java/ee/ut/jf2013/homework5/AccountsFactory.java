package ee.ut.jf2013.homework5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;

public class AccountsFactory {

    private final CountDownLatch countDownLatch;
    private final ReadWriteLock lock;

    private final List<Account> createdAccounts = new ArrayList<>();

    private final int ONE = 1;

    public AccountsFactory(CountDownLatch countDownLatch, ReadWriteLock lock) {
        this.countDownLatch = countDownLatch;
        this.lock = lock;
    }

    public Account createAccount(int initialBalance) {
        Account account = new Account(initialBalance);
        createdAccounts.add(account);
        return account;
    }


    class Account {
        private AtomicInteger balance;

        private Thread donator;

        private Account(int balance) {
            this.balance = new AtomicInteger(balance);
            donator = new Thread(new Runnable() {
                List<Account> recipients = createdAccounts;

                @Override
                public void run() {
                    for (Account recipient : recipients) {
                        if (recipient != Account.this)
                            try {
                                transferMoneyTo(recipient);
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                System.err.println("Interrupted exception occurred.");
                            }
                    }
                    countDownLatch.countDown();
                }
            });
        }

        public int getBalance() {
            return balance.get();
        }

        public void withdraw(int amount) {
            if (balance.get() < amount) {
                throw new RuntimeException("Insufficient funds!");
            }
            balance.addAndGet(-amount);
        }

        public void deposit(int amount) {
            balance.addAndGet(amount);
        }

        public void transferMoneyTo(Account recipient) {
            lock.readLock().lock();
            withdraw(ONE);
            recipient.deposit(ONE);
            lock.readLock().unlock();
        }

        public void startDonation() {
            donator.start();
        }
    }
}
