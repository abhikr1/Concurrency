import sun.lwawt.macosx.CSystemTray;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BankApp {
    public static void main(String args[]){
        Banking banking = new Banking(1000);
        Thread thread1 = new Thread(new Customer(banking, 400, true));
        Thread thread2 = new Thread(new Customer(banking, 200, false));
        Thread thread3 = new Thread(new Customer(banking, 1400, false));
        Thread thread4 = new Thread(new Customer(banking, 4000, true));
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }
}

class Banking {
    private int balance;
    ReentrantLock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    Banking(int balance) {
        this.balance = balance;
    }

    public void deposit(int amount) {
        try {
            lock.lock();
            balance = balance + amount;
            System.out.println("Amount after crediting : " + balance);
        } finally {
            condition.signal();
            lock.unlock();
        }
    }

    public void withdrawl(int amount) {
        lock.lock();
        try {
            while (balance < amount) {
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
            balance = balance - amount;
            System.out.println("Amount after debiting : " + balance);
        } finally {
            lock.unlock();
        }
    }
}

class Customer implements Runnable{
    private Banking banking;
    private int amount;
    private boolean isDeposit;
    Customer(Banking banking, int amount, boolean isDeposit){
        this.banking = banking;
        this.amount = amount;
        this.isDeposit = isDeposit;
    }
    @Override
    public void run() {
        if(isDeposit){
            banking.deposit(amount);
        }
        else{
            banking.withdrawl(amount);
        }
    }
}
