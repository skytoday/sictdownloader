package cn.ac.sict.learn;

import java.util.concurrent.*;

/**
 * JDK提供的便捷创建线程池的方式
 */
public class PoolTest03 {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        ExecutorService executorService1 = Executors.newCachedThreadPool();
        ExecutorService executorService2 = Executors.newSingleThreadExecutor();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    }
}
