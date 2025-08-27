package com.example.javapractice.transactional;

import com.example.javapractice.transactional.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Transactional 을 생략하는 것과 동작은 동일하지만, `NOT_SUPPORTED` 를 명시하여 의도를 명확히 합니다.
 * 이 설정은 테스트 코드의 실행과 서비스의 트랜잭션을 분리하여,
 * 서비스 로직이 DB에 남긴 최종 결과를 외부 관점에서 검증하기 위함입니다.
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest
public class TxPropagationTest {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepo;
    @Autowired
    InventoryRepository invRepo;
    @Autowired
    PaymentRepository payRepo;
    @Autowired
    AuditLogRepository auditRepo;
    @Autowired
    TestEntityRepository testRepo;

    @BeforeEach
    void setup() {
        // teardown
        orderRepo.deleteAllInBatch();
        invRepo.deleteAllInBatch();
        payRepo.deleteAllInBatch();
        testRepo.deleteAllInBatch();
        auditRepo.deleteAllInBatch();

        // init
        invRepo.save(new Inventory(null, "SKU1", 10));
    }

    @Test
    void 전체_성공_시나리오() {
        // when
        Long orderId = orderService.placeOrder("SKU1", 2, 1000, FailFlag.NONE);

        // then
        Order order = orderRepo.findById(orderId).orElseThrow();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
//        assertThat(order.getMessage()).isEqualTo("REQUIRED");   // REQUIRED인 전파 속성에서만 영속성이 유지된다

        assertThat(invRepo.findBySku("SKU1").get().getQuantity()).isEqualTo(8);

        List<Payment> pays = payRepo.findAll();
        pays.forEach(System.out::println);
        assertThat(pays).hasSize(1);

        List<AuditLog> auditLogs = auditRepo.findAll();
        auditLogs.forEach(System.out::println);
        assertThat(auditLogs).hasSize(2);

        List<TestEntity> tests = testRepo.findAll();
        assertThat(tests).hasSize(2);
        tests.forEach(System.out::println);
    }
}
