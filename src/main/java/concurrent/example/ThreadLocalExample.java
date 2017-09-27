package concurrent.example;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chuang on 17-9-27.
 */
public class ThreadLocalExample {
    public static class ThreadLocalRunnable implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getId() + ":" + ThreadId.get());
            ThreadId.inc();
            System.out.println(Thread.currentThread().getId() + ":" + ThreadId.get());
        }
    }

    private static class ThreadId {
        private static final AtomicInteger nextId = new AtomicInteger();

        private static final ThreadLocal<Integer> threadId = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return nextId.getAndIncrement();
            }
        };

        private static int get() {
            return threadId.get();
        }

        private static void inc() {
            threadId.set(threadId.get() + 1);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadLocalRunnable sharedRunnableInstance = new ThreadLocalRunnable();

        Thread thread1 = new Thread(sharedRunnableInstance);
        Thread thread2 = new Thread(sharedRunnableInstance);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }
}
