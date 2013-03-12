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
        Account account = new Account(createdAccounts);
        account.balance = new AtomicInteger(initialBalance);
        createdAccounts.add(account);
        return account;
    }


    class Account {
        private AtomicInteger balance;

        private Thread donator;

        private Account(final List<Account> createdAccounts) {
            donator = new Thread(new Runnable() {
                List<Account> recipients = createdAccounts;

                @Override
                public void run() {
                    for (Account recipient : recipients) {
                        if (recipient != Account.this)
                            try {
                                lock.readLock().lock();
                                withdraw(ONE);
                                recipient.deposit(ONE);
                                lock.readLock().unlock();
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
            //lock.writeLock().lock();
            if (balance.get() < amount) {
                throw new RuntimeException("Insufficient funds!");
            }
            balance.addAndGet(-amount);
            //lock.writeLock().unlock();
        }

        public void deposit(int amount) {
            //lock.writeLock().lock();
            balance.addAndGet(amount);
            //lock.writeLock().unlock();
        }

        public void startDonation() {
            donator.start();
        }
    }
}
