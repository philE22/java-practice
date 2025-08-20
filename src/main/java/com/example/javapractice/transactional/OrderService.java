package com.example.javapractice.transactional;

import com.example.javapractice.transactional.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CouponService couponService;
    private final PaymentRepository paymentRepository;

    @Transactional // REQUIRED (outer tx)
    public Long placeOrder(String sku, int qty, int amount, @Nullable String coupon, boolean callNotification) {

        Order order = orderRepository.save(new Order(null, OrderStatus.CREATED));
        audit.record("order created: " + order.getId());

        inventory.reserve(sku, qty);

        if (coupon != null) {
            try { couponService.apply(order.getId(), coupon); }
            catch (Exception e) { audit.record("coupon failed: " + e.getMessage()); }
        }

        try {
            payment.pay(order.getId(), amount);
            order.setStatus(OrderStatus.PAID);
        } catch (RuntimeException payEx) {
            order.setStatus(OrderStatus.FAILED);
            // 여기서 예외를 던지면 outer 전체 롤백, 삼키면 outer를 계속 진행
            throw payEx; // 또는 주석처리해 비교 실험
        }

        if (callNotification) notification.send(order.getId());

        audit.record("order finished: " + order.getId() + " status=" + order.getStatus());
        return order.getId();
    }
}
