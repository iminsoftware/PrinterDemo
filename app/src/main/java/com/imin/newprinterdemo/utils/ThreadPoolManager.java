package com.imin.newprinterdemo.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPoolManager {

    private volatile static ThreadPoolManager INSTANCE;
    private ExecutorService mThreadPool;
    private int KEEP_ALIVE_TIME = 100;
    private int MAX_POOL_SIZE = 1000;

    private ThreadPoolManager() {
        mThreadPool = new ThreadPoolExecutor(MAX_POOL_SIZE,
                MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("FaceThread");
                        return thread;
                    }
                });
    }

    public static ThreadPoolManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ThreadPoolManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ThreadPoolManager();
                }
            }
        }
        return INSTANCE;
    }

    public void execute(Runnable runnable) {
        mThreadPool.execute(runnable);
    }

}