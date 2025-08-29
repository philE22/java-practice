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
    SomeEntityRepository testRepo;

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
        //given
        FailFlag flag = FailFlag.NONE;

        //when
        Long orderId = orderService.placeOrder("SKU1", 2, 1000, flag);

        //then
        Order order = orderRepo.findById(orderId).get();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        int quantity = invRepo.findBySku("SKU1").get().getQuantity();
        assertThat(quantity).isEqualTo(8);

        List<Payment> pays = payRepo.findAll();
        assertThat(pays).hasSize(1);
        assertThat(pays.getFirst().getOrderId()).isEqualTo(orderId);
        assertThat(pays.getFirst().getStatus()).isEqualTo("PAID");

        List<AuditLog> auditLogs = auditRepo.findAll();
        assertThat(auditLogs).hasSize(2)
                .extracting(AuditLog::getMessage)
                .containsExactly(
                        "order created: " + orderId,
                        "order finished: " + orderId + " status=PAID"
                );

        List<SomeEntity> tests = testRepo.findAll();
        assertThat(tests).hasSize(2)
                .extracting(SomeEntity::getMessage)
                .containsExactly(
                        "NOT_SUPPORTED",    // NOT_SUPPORTED 는 트랜잭션이 적용되지 않음으로 변경안됨
                        "REQUIRES_NEW modified"     // REQUIRES_NEW 는 트랜잭션이 적용되므로 변경
                );
    }

    @Test
    void 결제가_실패하는_경우() {
        //given
        FailFlag flag = FailFlag.PAYMENT;

        // when
        Long orderId = orderService.placeOrder("SKU1", 2, 1000, flag);

        //then
        Order order = orderRepo.findById(orderId).get();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);

        Inventory sku = invRepo.findBySku("SKU1").get();
        assertThat(sku.getQuantity()).isEqualTo(8);

        List<Payment> pays = payRepo.findAll();
        assertThat(pays).hasSize(0);

        List<AuditLog> auditLogs = auditRepo.findAll();
        assertThat(auditLogs).hasSize(3)
                .extracting(AuditLog::getMessage)
                .containsExactly(
                        "order created: " + orderId,
                        "payment fail order: " + orderId,
                        "order finished: " + orderId + " status=FAILED"
                );
        List<SomeEntity> tests = testRepo.findAll();
        assertThat(tests).hasSize(2)
                .extracting(SomeEntity::getMessage)
                .containsExactly(
                        "NOT_SUPPORTED",    // NOT_SUPPORTED 는 트랜잭션이 적용되지 않음으로 변경안됨
                        "REQUIRES_NEW modified"     // REQUIRES_NEW 는 트랜잭션이 적용되므로 변경
                );
    }






















}
