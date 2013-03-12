package ee.ut.jf2013.homework5;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ee.ut.jf2013.homework5.AccountsFactory.Account;

public class MoneyTransfer {

    static ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void main(String[] args) throws InterruptedException {
        int n = 5;
        if (args.length > 0) {
            n = Integer.parseInt(args[0]);
        }

        final Collection<Account> accounts = new ArrayList<>(n);

        final CountDownLatch countDown = new CountDownLatch(n);

        AccountsFactory factory = new AccountsFactory(countDown, lock);

        for (int i = 0; i < n; i++) {
            Account account = factory.createAccount(n);
            accounts.add(account);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (countDown.getCount() > 0) {
                    printAllBalancesAndSum(accounts);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }).start();

        for (Account account : accounts) {
            account.startDonation();
        }
        countDown.await();

        printAllBalancesAndSum(accounts);

    }


    static synchronized void printAllBalancesAndSum(Collection<Account> accounts) {
        lock.writeLock().lock();
        int sum = 0;
        StringBuilder balances = new StringBuilder();
        for (Account account : accounts) {
            sum += account.getBalance();
            balances.append(account.getBalance()).append(" ");
        }
        balances.append(sum);
        System.out.println(balances);
        lock.writeLock().unlock();
    }
}
