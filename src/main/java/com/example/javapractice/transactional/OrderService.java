package com.example.javapractice.transactional;

import com.example.javapractice.transactional.domain.Order;
import com.example.javapractice.transactional.domain.OrderRepository;
import com.example.javapractice.transactional.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        Order order = orderRepository.save(new Order(null, OrderStatus.CREATED, null));
        audit.record("order created: " + order.getId(), flag);

        inventory.reserve(sku, qty, order, flag);

        try {
            payment.pay(order.getId(), amount, flag);
            order.setStatus(OrderStatus.PAID);
        } catch (RuntimeException payEx) {
            order.setStatus(OrderStatus.FAILED);
            order.setMessage("결제 실패");

            audit.record("payment fail order: " + order.getId(), flag);
            log.error("{} 주문 결제 실패", order.getId(), payEx);
        }

        audit.record("order finished: " + order.getId() + " status=" + order.getStatus(), flag);

        // 전파속성 테스트
        notsupportedService.apply(flag);
        requiresNewService.apply(flag);

        return order.getId();
    }

}