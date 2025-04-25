package com.imin.newprinter.demo.utils;

/**
 * @Author: hy
 * @date: 2024/11/4
 * @description:
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceManager {
    private static final int THREAD_POOL_SIZE = 16;

    // 使用单例模式来管理ExecutorService的实例
    private static ExecutorService executorService;

    // 获取ExecutorService实例 (线程安全)
    public static synchronized ExecutorService getExecutorService() {
        if (executorService == null || executorService.isShutdown()) {
            // 根据具体情况调整线程池类型，这里使用CachedThreadPool
            executorService = Executors.newCachedThreadPool();
        }
        return executorService;
    }

    // 关闭ExecutorService，确保其释放资源
    public static void shutdownExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown(); // 防止新任务被提交

            try {
                // 等待已经提交的任务完成 (例如，最多等待 60 秒)
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    // 超时后强制关闭
                    executorService.shutdownNow();

                    // 再次等待任务完成
                    if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        System.err.println("ExecutorService did not terminate");
                    }
                }
            } catch (InterruptedException e) {
                // 当前线程中断，重新中断并立即关闭
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // 提供取消Future任务的功能
    public static void cancelTask(Future<?> future) {
        if (future != null && !future.isDone()) {
            future.cancel(true); // true 表示如果任务正在运行，可以中断它
        }
    }
}
