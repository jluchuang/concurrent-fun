package book.artofjavaconcurrency.connectionpool;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chuang on 2017/4/1.
 */
public class ConnectionPoolTest {
    static ConnectionPool pool = new ConnectionPool(10);

    // 保证所有的ConnectionRunner能够同时开始
    static CountDownLatch start = new CountDownLatch(1);

    // main线程将会等待所有ConnectionRunner结束后才能执行
    static CountDownLatch end;

    public static void main(String[] args) throws Exception {
        int threadCount = 20;
        end = new CountDownLatch(threadCount);

        int count = 20;

        AtomicInteger got = new AtomicInteger();
        AtomicInteger notGot = new AtomicInteger();

        for (int i = 0; i < threadCount; i ++) {
            Thread thread = new Thread(new ConnectionRunner(count, got, notGot), "ConnectionRunnerThread");
            thread.start();
        }

        start.countDown();
        end.await();

        System.out.println("Total invoke: " + (threadCount * count));
        System.out.println("Got connection: " + got);
        System.out.println("Not got connection: " + notGot);
    }

    static class ConnectionRunner implements Runnable{

        int count;
        AtomicInteger got;
        AtomicInteger notGot;

        public ConnectionRunner(int count, AtomicInteger got, AtomicInteger notGot) {
            this.count = count;
            this.got = got;
            this.notGot = notGot;
        }

        @Override
        public void run() {
            try {
                start.wait();
            }catch (Exception ex) {

            }

            while (count > 0 ) {
                try {
                    Connection connection = pool.fetchConnection(1000);
                    if(connection != null) {
                        try {
                            connection.createStatement();
                            connection.commit();
                        }
                        finally {
                            pool.releaseConnection(connection);
                            got.incrementAndGet();
                        }
                    }
                    else {
                        notGot.incrementAndGet();
                    }
                } catch (Exception e) {
                }finally {
                    count --;
                }
            }
            end.countDown();
        }
    }
}
