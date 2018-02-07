package book.artofjavaconcurrency.connectionpool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class PrintNum implements Runnable {
    private static AtomicInteger a = new AtomicInteger(1);
    private static ReentrantLock reentrantLock = new ReentrantLock();
    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    int expected;
    CountDownLatch countDownLatch;
    public PrintNum(int excepted, CountDownLatch countDownLatch) {
        this.expected = excepted;
        this.countDownLatch = countDownLatch;
    }

    public void print() {
        System.out.println(a.getAndIncrement());
    }

    @Override
    public void run() {
        while(a.get() != expected) {
        }
        reentrantLock.lock();
        try {
            System.out.println(a.getAndIncrement());
        }
        finally {
            countDownLatch.countDown();
            reentrantLock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int i = 0; i < 100; i ++) {
            executorService.submit(new PrintNum(i + 1, countDownLatch));
        }
        countDownLatch.await();
        executorService.shutdown();
    }
}
