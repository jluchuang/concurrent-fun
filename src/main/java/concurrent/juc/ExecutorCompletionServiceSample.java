package concurrent.juc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by chuang on 17-12-7.
 */
public class ExecutorCompletionServiceSample {

    private static class TestTask implements Callable<String> {

        private String taskName;

        public TestTask(String taskName) {
            this.taskName = taskName;
        }

        @Override
        public String call() throws Exception {
            Thread.sleep(ThreadLocalRandom.current().nextInt(0, 1000));
            return "Result from " + taskName;
        }
    }

    private static void simpleDemo() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        CompletionService<String> executorCompletionService = new ExecutorCompletionService<>(executorService);

        List<Future<String>> futures = new ArrayList<>();
        futures.add(executorCompletionService.submit(new TestTask("A")));
        futures.add(executorCompletionService.submit(new TestTask("B")));
        futures.add(executorCompletionService.submit(new TestTask("C")));
        futures.add(executorCompletionService.submit(new TestTask("D")));

        for (int i = 0; i < futures.size(); i++) {
            String result = executorCompletionService.take().get();
            System.out.println(result);
        }
    }

    private static void firstResultDemo() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        CompletionService<String> executorCompletionService = new ExecutorCompletionService<>(executorService);

        List<Future<String>> futures = new ArrayList<>();
        futures.add(executorCompletionService.submit(new TestTask("A")));
        futures.add(executorCompletionService.submit(new TestTask("B")));
        futures.add(executorCompletionService.submit(new TestTask("C")));
        futures.add(executorCompletionService.submit(new TestTask("D")));

        String firstResultStr = null;
        try {
            for (int i = 0; i < futures.size(); i++) {
                try {
                    firstResultStr = executorCompletionService.take().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        finally {
            for (Future<String> f : futures) {
                f.cancel(true);
            }
        }

        System.out.println("First Result is :" + firstResultStr);
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        simpleDemo();
        firstResultDemo();
    }

}
