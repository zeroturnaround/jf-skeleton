package ee.ut.jf2013.homework5;

import ee.ut.jf2013.homework5.AccountsFactory.Account;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MoneyTransfer {

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private PrintStream stream = new PrintStream(new BufferedOutputStream(System.out));

    public static void main(String[] args) throws InterruptedException {
        int n = 5;
        if (args.length > 0) {
            n = Integer.parseInt(args[0]);
        }

        new MoneyTransfer().start(n);
    }

    private void start(int n) throws InterruptedException {
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

        factory.startTransfersBetweenAccounts();
        countDown.await();

        printAllBalancesAndSum(accounts);
    }


    void printAllBalancesAndSum(Collection<Account> accounts) {
        lock.writeLock().lock();
        int sum = 0;
        for (Account account : accounts) {
            sum += account.getBalance();
            stream.append(Integer.toString(account.getBalance())).append(" ");
        }
        stream.append(Integer.toString(sum)).println();
        stream.flush();
        lock.writeLock().unlock();
    }
}
