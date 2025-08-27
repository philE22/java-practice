package com.example.javapractice.transactional;

import com.example.javapractice.transactional.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AuditService audit;
    private final PaymentService payment;
    private final InventoryService inventory;
    private final NotSupportedService notsupportedService;
    private final RequiresNewService requiresNewService;

    @Transactional // REQUIRED (outer tx)
    public Long placeOrder(String sku, int qty, int amount, FailFlag flag) {
        // 일반적인 비지니스 흐름
        Order order = orderRepository.save(new Order(null, OrderStatus.CREATED, null));
        audit.record("order created: " + order.getId(), flag);

        inventory.reserve(sku, qty, order, flag);

        try {
            payment.pay(order.getId(), amount, flag);
            order.setStatus(OrderStatus.PAID);
        } catch (RuntimeException payEx) {
            order.setStatus(OrderStatus.FAILED);
            // 여기서 예외를 던지면 outer 전체 롤백, 삼키면 outer를 계속 진행
            throw payEx; // 또는 주석처리해 비교 실험
        }

        audit.record("order finished: " + order.getId() + " status=" + order.getStatus(), flag);

        // 테스트를 위한 흐름
        notsupportedService.apply(order, flag);
        requiresNewService.apply(order, flag);

        return order.getId();
    }

}