package com.example.javapractice.completablefuture;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;

@Slf4j
class CompetableFutureRunTest {

    @Test
    void runAsyncTest() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            log.info("run!");
        });

        future.join();
        log.info("done");
    }

    @Test
    void supplyAsync() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            log.info("supplyAsync start!");
            return "Hello";
        });

        String result = future.join();
        log.info("Done! result: {}", result);
    }

    @Test
    void thenApplyTest() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            log.info("thenApply start!");
            return "Hello";
        });

        String result = future.thenApply(s -> {
            log.info("string : {}", s);
            return s.toUpperCase();
        }).join();
        log.info("Done! result: {}", result);
    }

    @Test
    void thenAccept() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            log.info("thenAccept() Start!");
            return "hello";
        });

        future.thenAccept(s -> {
            log.info("consume result: {}", s);
        }).join();

        log.info("thenAccept() Done!");
    }

    @Test
    void thenRun() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            log.info("thenRun() Start!");
            return "hello";
        });

        future.thenRun(() -> {
            log.info("thenRun other!");
        }).join();

        log.info("thenRun() Done!");
    }

    @Test
    void thenCompose() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            log.info("First thread");
            return "Hello";
        });

        CompletableFuture<String> future2 = future.thenCompose(s ->
                CompletableFuture.supplyAsync(() -> {
                    log.info("Second thread");
                    return s + " World";
                }));

        String result = future2.join();
        System.out.println("result = " + result);
    }

    @Test
    void thenCombine() {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World!");

        String result = future1.thenCombine(future2, (a, b) -> a + " " + b)
                .join();

        System.out.println("result = " + result);
    }

    @Test
    void allOf() {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");

        List<CompletableFuture<String>> futures = List.of(future1, future2);

        List<String> result = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v ->
                        futures.stream()
                                .map(CompletableFuture::join)
                                .toList()
                ).join();

        System.out.println("result = " + result);
    }

    @Test
    void anyOf() {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Hello";
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");

        List<CompletableFuture<String>> futures = List.of(future1, future2);

        CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0]))
                .thenAccept(s -> System.out.println("s = " + s))
                .join();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void exceptionally(boolean doThrow) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            if (doThrow) throw new IllegalArgumentException("Invalid Argument!");

            return "Thread: " + Thread.currentThread().getName();
        }).exceptionally(e -> {
            return e.getMessage();
        });

        String result = future.join();
        System.out.println("result = " + result);
    }


    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void handle(boolean doThrow) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            if (doThrow) throw new IllegalArgumentException("Invalid Argument!");

            return "Thread: " + Thread.currentThread().getName();
        }).handle((result, e) -> {
            return e == null ? result : e.getMessage();
        });

        String result = future.join();
        System.out.println("result = " + result);
    }

    @Test
    void join_get_test() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Test get");

        try {
            assertThat(future.get()).isEqualTo("Test get");
        } catch (InterruptedException | ExecutionException e) {
            fail("Exception should not be thrown");
        }

        CompletableFuture<String> exceptionFuture = CompletableFuture.failedFuture(new RuntimeException("Test get exception"));

        assertThatThrownBy(exceptionFuture::get)
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(RuntimeException.class)
                .hasRootCauseMessage("Test get exception");

    }
}