package book.artofjavaconcurrency.connectionpool;


import java.sql.Connection;
import java.util.LinkedList;

/**
 * Created by chuang on 2017/4/1.
 */
public class ConnectionPool {
    private LinkedList<Connection> pool = new LinkedList<>();

    public ConnectionPool (int initialSize) {
        if(initialSize > 0) {
            for(int i = 0; i < initialSize; i++) {
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }

    public void releaseConnection(Connection connection) {
        if(connection != null ) {
            synchronized (pool) {
                // 链接释放后需进行通知, 这样其他消费者能够感知到连接池中已经归还了一个链接
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {
            // 完全超时
            if(mills < 0) {
                while (pool.isEmpty()) {
                    pool.wait();
                }
                return pool.removeFirst();
            }
            else {
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;

                while (pool.isEmpty() && remaining > 0) {
                    pool.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                }
                Connection res = null;
                if(!pool.isEmpty()) {
                    res = pool.removeFirst();
                }

                return res;
            }
        }
    }
}
