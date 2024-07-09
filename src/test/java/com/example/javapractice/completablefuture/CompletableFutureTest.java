package com.example.javapractice.completablefuture;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
public class CompletableFutureTest {
    private final Random random = new Random();


    @Test
    void runAsync_단일_실행_테스트() throws ExecutionException, InterruptedException {
        log.info("1");
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            log.info("supplyAsync 실행1");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("supplyAsync 실행2");
            return "1";
        });

        log.info("2");
//        String s = future.get();
        log.info("3");
//        log.info("result : {}", s);
    }

    @Test
    void runAsync_다중_실행_테스트() throws Exception {
        log.info("================== Main Thread Start: " + Thread.currentThread().getName());

        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(1001));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("{} 번째 로직 실행!", finalI);
                return finalI + " is work!";
            });
            futures.add(future);
        }


        log.info("");
        log.info("================== Main Thread End: " + Thread.currentThread().getName());
    }

    @Test
    void runAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            log.info("future run!");
        });

        future.get();
        log.info("main thread end!");
    }

    @Test
    void supplyAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            log.info("future run!");
            return "result";
        });

        String result = future.get();
        log.info("main thread end! result: {}", result);
    }

    @Test
    void thenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<String> fu = CompletableFuture.supplyAsync(() -> {
            log.info("supplyAsync work!!");
            return Thread.currentThread().getName();
        }).thenApply(name -> name + " is thread name");

        log.info(fu.get());
    }

    @Test
    void thenAccept() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            log.info("supplyAsync work!!");
            return Thread.currentThread().getName();
        }).thenAccept(name -> {
            log.info("thenAccept consume result!! {}", name);
        });

        future.get();
    }

    @Test
    void thenRun() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            log.info("supplyAsync Work!");
            return "result";
        }).thenRun(() -> {
            log.info("loging 1.....");
        }).thenApply(result -> {
            log.info("thenApply Work! {}", result);
            return result + " is pending";
        }).thenRun(() -> {
            log.info("loging 2.....");
        }).thenApply(result -> {
            return result + " is pending2";
        });

        log.info("end : {}", future.get());
    }

    @Test
    void thenCompose() throws ExecutionException, InterruptedException {
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> "Helllo");

        CompletableFuture<String> future = hello.thenCompose(s -> getWorld(s));

        log.info("result : {}", future.get());
    }

    private CompletableFuture<String> getWorld(String message) {
        return CompletableFuture.supplyAsync(() -> message + " World!");
    }

    @Test
    void thenCombine() throws ExecutionException, InterruptedException {
        CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> "world!");
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> "Hello");

        CompletableFuture<String> future = hello.thenCombine(world, (h, w) -> h + " " + w);

        System.out.println("future.get() = " + future.get());

    }

    @Test
    void allOfTest() {

        log.info("1");
        ArrayList<CompletableFuture<String>> futures = new ArrayList<>();
        Stream.of("a", "b", "c")
                .forEach(s -> {
                    log.info("2 {}", s);
                    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                        log.info("2-1 {}", s);
                        try {
                            TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        log.info("{} Work!", s);

                        return s;
                    });

                    futures.add(future);
                });

        log.info("3");
        CompletableFuture<Void> future = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> log.info("All done!!"));

        log.info("모든 준비 완료!!");

        future.join();
        log.info("finish!");
    }

    @Test
    void exceptionTest() {

        ArrayList<CompletableFuture<String>> futures = new ArrayList<>();
        Stream.of("a", "b", "c")
                .forEach(s -> {
                    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                        try {
                            TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        log.info("{} Work!", s);

                        if ("b".equals(s)) throw new RuntimeException();

                        return s;
                    }).exceptionally(ex -> "exception");

                    futures.add(future);
                });

        List<String> result = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList())
                .join();
        System.out.println("result = " + result);
    }

    @Test
    void name() {
        String s = method1("a", "b", "c");
        System.out.println("s = " + s);

        String[] array = {"x", "y", "z"};
        String s1 = method1(array);
        System.out.println("s1 = " + s1);
    }

    public String method1(String... strs) {
        String result = "";
        for (String str : strs) {
            result = result + " " + str;
        }

        return result;
    }
}
