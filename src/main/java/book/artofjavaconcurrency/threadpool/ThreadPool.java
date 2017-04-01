package book.artofjavaconcurrency.threadpool;

/**
 * Created by chuang on 2017/4/1.
 */
public interface ThreadPool<Job extends Runnable> {

    // 执行一个Job, 这个Job需要实现Runnable
    void execute(Job job);

    // 关闭线程池
    void shutdown();

    // 增加工作者线程数量
    void addWorkers(int num);

    // 减少工作者线程的数量
    void removeWorker(int num);

    // 得到正在等待执行的任务数量
    int getJobSize();
}
