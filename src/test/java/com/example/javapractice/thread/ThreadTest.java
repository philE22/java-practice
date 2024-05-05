package com.example.javapractice.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

@Slf4j
class ThreadTest {

    @Test
    void threadTest() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            MyThread thread = new MyThread();
            thread.start();
        }

        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    void runnable_람다를_활용한_스레드생성_테스트() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> log.info("thread start! {}", Thread.currentThread().getName()));
            thread.start();
        }

        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    void name() throws InterruptedException {
        int requestNumber = 5;
        CountDownLatch countDownLatch = new CountDownLatch(requestNumber);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Runnable command = () -> {
            log.info("{} Thread Start!!", Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("{} Thread End!!", Thread.currentThread().getName());
            countDownLatch.countDown();
        };

        for (int i = 0; i < requestNumber; i++) {
            executorService.execute(command);
        }

        countDownLatch.await();
    }
}