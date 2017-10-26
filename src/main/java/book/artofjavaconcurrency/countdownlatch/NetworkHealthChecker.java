package book.artofjavaconcurrency.countdownlatch;

import java.util.concurrent.CountDownLatch;

/**
 * Created by chuang on 2017/10/22.
 */
public class NetworkHealthChecker extends BaseHealthChecker {
    public NetworkHealthChecker(CountDownLatch latch) {
        super("Network Service", latch);
    }

    @Override
    public void verifyService() {
        System.out.println("Checking " + this.getServiceName());
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(this.getServiceName() + " is UP");
    }
}
