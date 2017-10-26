package book.artofjavaconcurrency.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by chuang on 2017/9/26.
 */
public class FutureTest {

    public static class A implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            Thread.sleep(2000);
            return 3;
        }
    }

    public static class B implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            Thread.sleep(2000);
            return 1;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Integer>> futures = new ArrayList<Future<Integer>>();

        Callable a = new A();
        Callable b = new B();
        //submit Callable tasks to be executed by thread pool
        Future<Integer> futureB = executor.submit(b);
        Future<Integer> futureA = executor.submit(a);


        //add Future to the list, we can get return value using Future
        futures.add(futureA);
        futures.add(futureB);


        int i = 0;
        while (true) {
            int idx = i % futures.size();
            if (futures.get(idx).isDone()) {
                System.out.println(futures.get(idx).get());
                break;
            }
            System.out.println("wait ......");
            i ++;
        }
        //shut down the executor service now
        executor.shutdown();
    }
}
