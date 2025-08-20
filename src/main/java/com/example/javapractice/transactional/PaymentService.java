package com.example.javapractice.transactional;

import com.example.javapractice.transactional.domain.Payment;
import com.example.javapractice.transactional.domain.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final boolean shouldFail; // 테스트 주입용 토글
    private final PaymentRepository paymentRepository;

    public PaymentService(@Value("${test.payment.fail:false}") boolean shouldFail, PaymentRepository paymentRepository) {
        this.shouldFail = shouldFail;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void pay(Long orderId, int amount) {
        paymentRepository.save(new Payment(null, orderId, shouldFail ? "FAILED" : "PAID", amount));

        if (shouldFail) throw new RuntimeException("payment gateway error");
    }
}
