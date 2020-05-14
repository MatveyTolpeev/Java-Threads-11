import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PoolExample {

    static Object threadPoolMonitor = new Object();

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                3, 3, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(3));
        final AtomicInteger count = new AtomicInteger(0);

        final AtomicInteger inProgress = new AtomicInteger(0);

        boolean accepted = false;

        for (int i = 0; i < 30; i++) {
            final int number = i;
            Thread.sleep(10);
            accepted = false;
            System.out.println("creating #" + number);
            do {
                try {
                    executor.submit(() -> {
                        int working = inProgress.incrementAndGet();
                        System.out.println("start #" + number + ", in progress: " + working);
                        try {
                            Thread.sleep(Math.round(1000 + Math.random() * 2000));
                        } catch (InterruptedException e) {
                            //ignore
                        }
                        working = inProgress.decrementAndGet();
                        System.out.println("end #" + number + ", in progress: " + working + ", done tasks" + count.incrementAndGet());
                        return null;
                    });
                    accepted = true;
                }
                catch ( RejectedExecutionException e ) {
                    try {
                        synchronized ( threadPoolMonitor ) {
                            threadPoolMonitor.wait(100);
                        }
                    }
                    catch ( InterruptedException ignored ) {
                        //ignore
                    }
                }
            } while ( !accepted );
        }
        executor.shutdown();
    }
}
