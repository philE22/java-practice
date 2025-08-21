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

@SpringBootTest
public class TxPropagationTest {

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepo;
    @Autowired InventoryRepository invRepo;
    @Autowired PaymentRepository payRepo;
    @Autowired NotificationRepository notiRepo;
    @Autowired AuditLogRepository auditRepo;

    @BeforeEach
    void seed() {
        invRepo.save(new Inventory(null, "SKU1", 10));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 전체_성공() {
        Long orderId = orderService.placeOrder("SKU1", 2, 1000, null, true, false);

        // 커밋 결과 재조회
        Order order = orderRepo.findById(orderId).orElseThrow();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        assertThat(invRepo.findBySku("SKU1").orElseThrow().getQuantity()).isEqualTo(8);

        // REQUIRES_NEW 커밋 여부
        List<Payment> pays = payRepo.findAll();
        pays.forEach(System.out::println);
        assertThat(pays).hasSize(1);

        List<AuditLog> auditLogs = auditRepo.findAll();
        auditLogs.forEach(System.out::println);
        assertThat(auditLogs).hasSize(2);

        // NOT_SUPPORTED 저장 여부
        List<Notification> notis = notiRepo.findAll();
        assertThat(notis).hasSize(1);
    }
}
