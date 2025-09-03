package com.example.javapractice.reentrantlock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockMain {

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private static final Map<String, String> sharedData = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- ReentrantReadWriteLock 동작 방식 테스트 ---");
        // 스레드를 관리하기 위해 ExecutorService 사용
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // --- 1. 쓰기 스레드가 초기 데이터를 추가합니다 ---
        System.out.println("\n[Phase 1] Writer 스레드가 초기 데이터를 작성합니다.");
        executor.submit(new Writer("InitialWriter", "key1", "value1"));

        // 쓰기 스레드가 작업을 마칠 시간을 줍니다.
        sleep(200);

        // --- 2. 여러 읽기 스레드가 동시에 데이터에 접근합니다 ---
        System.out.println("\n[Phase 2] 여러 Reader 스레드가 동시에 접근을 시도합니다. (서로 방해하지 않음)");
        executor.submit(new Reader("Reader1"));
        executor.submit(new Reader("Reader2"));
        executor.submit(new Reader("Reader3"));

        // 읽기 스레드들이 함께 실행되는 것을 보여주기 위해 잠시 기다립니다.
        sleep(200);

        // --- 3. 쓰기 스레드가 잠금을 획득하려고 시도합니다 ---
        // 이 스레드는 현재 실행 중인 모든 읽기 스레드가 잠금을 해제할 때까지 대기해야 합니다.
        System.out.println("\n[Phase 3] Writer 스레드가 접근을 시도합니다. (모든 Reader가 끝날 때까지 대기)");
        executor.submit(new Writer("UpdaterWriter", "key2", "value2"));

        // 쓰기 스레드가 대기하는 것을 보여주기 위해 잠시 기다립니다.
        sleep(100);

        // --- 4. 더 많은 읽기 스레드가 시작됩니다 ---
        // 이 스레드들은 쓰기 스레드가 작업을 마칠 때까지 대기해야 합니다.
        System.out.println("\n[Phase 4] 더 많은 Reader 스레드가 접근을 시도합니다. (Writer가 끝날 때까지 대기)");
        executor.submit(new Reader("Reader4"));
        executor.submit(new Reader("Reader5"));

        // ExecutorService를 종료합니다.
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("\n[최종 결과] 최종 공유 데이터: " + sharedData);
    }

    // 데이터를 읽는 역할을 하는 스레드
    static class Reader implements Runnable {
        private final String name;

        Reader(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            readLock.lock(); // 읽기 잠금을 획득
            try {
                System.out.println("  " + name + " [Read Lock 획득]. 읽는 중...");
                // 읽는 데 시간이 걸리는 것을 시뮬레이션
                sleep(150);
                System.out.println("    " + name + " -> 현재 데이터: " + sharedData);
            } finally {
                System.out.println("  " + name + " [Read Lock 해제]");
                readLock.unlock(); // 반드시 잠금을 해제
            }
        }
    }

    // 데이터를 쓰는 역할을 하는 스레드
    static class Writer implements Runnable {
        private final String name;
        private final String key;
        private final String value;

        Writer(String name, String key, String value) {
            this.name = name;
            this.key = key;
            this.value = value;
        }

        @Override
        public void run() {
            writeLock.lock(); // 쓰기 잠금을 획득
            try {
                System.out.println(">>> " + name + " [Write Lock 획득]. 쓰는 중...");
                // 쓰는 데 시간이 걸리는 것을 시뮬레이션
                sleep(300);
                sharedData.put(key, value);
                System.out.println(">>> " + name + " -> 데이터 작성 완료: " + key + "=" + value);
            } finally {
                System.out.println(">>> " + name + " [Write Lock 해제]");
                writeLock.unlock(); // 반드시 잠금을 해제
            }
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}