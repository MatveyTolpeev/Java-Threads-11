import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Lucky {
    static int x = 0;
    static int count = 0;
    static Lock lock = new ReentrantLock();

    static class LuckyThread extends Thread {


        @Override
        public void run() {

            while (x < 999999) {
                lock.lock();
                x++;
                if ((x % 10) + (x / 10) % 10 + (x / 100) % 10 == (x / 1000) % 10 + (x / 10000) % 10 + (x / 100000) % 10) {
                    System.out.println(x);
                    count++;
                }
                lock.unlock();
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new LuckyThread();
        Thread t2 = new LuckyThread();
        Thread t3 = new LuckyThread();
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
        System.out.println("Total: " + count);
    }
}
