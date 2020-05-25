package com.mtcarpenter.gateway.cloud.client.global.filter.example;

import java.util.concurrent.*;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
public class JoinCountDownLatch {
    // 创建一个 CountDownLatch 实例
    private static volatile Semaphore semaphore = new Semaphore(0);

    public static void main(String[] args) throws InterruptedException {
        // 创建一个线程个数固定为 2 的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // 将线程 A 添加线程池
        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread()+"over 1");
               semaphore.release();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread()+"over 1");
                semaphore.release();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // 等待子线程执行完毕 返回
        semaphore.acquire(2);
        // 将线程 A 添加线程池
        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread()+"over 1");
                semaphore.release();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread()+"over 1");
                semaphore.release();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // 等待子线程执行完毕 返回
        semaphore.acquire(2);
        System.out.println("all child thread over");
        executorService.shutdown();
    }



}
