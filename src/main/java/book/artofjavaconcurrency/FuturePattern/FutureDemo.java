package book.artofjavaconcurrency.FuturePattern;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by chuang on 2017/4/1.
 */
public class FutureDemo {

    static class DemoTask implements Callable<String> {
        public String call() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            return Thread.currentThread().getName();
        }
    }


    public static void main(String args[]) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<Future<String>> list = new ArrayList<>();
        Callable<String> task = new DemoTask();

        for (int i = 0; i < 100; i ++) {
            Future<String> future = executorService.submit(task);

            // Add Future to the list, we can get return value using future
            list.add(future);
        }

        for (Future<String> fut : list) {
            try {
                // just call Future.get() to get the result
                System.out.println(new Date() + "::" + fut.get(1990, TimeUnit.MILLISECONDS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println(new Date() + ":: time limit out !!");
            }
        }

        executorService.shutdown();
    }

}
