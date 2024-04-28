package com.example.javapractice.concurrency;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

@Slf4j
public class MultiThreadTest {
    @Test
    void multiRequestTest() {
        TestService service = new TestService();

        IntStream.range(0, 100).parallel().forEach(i -> service.run());

        log.info("result : {}", service.getNum());
        Assertions.assertThat(service.getNum()).isNotEqualTo(100);
    }

    public static class TestService {

        private Integer num = 0;

        public void run() {
            int getNum = num;
            log.info("num = {}", getNum);
            try {
                long l = Math.round(Math.random() * 1000);
                log.info("sleep millis : {}", l);
                Thread.sleep(l);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            num = getNum + 1;
        }

        public Integer getNum() {
            return num;
        }
    }
}
