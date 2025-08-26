package com.example.javapractice.transactional;

import com.example.javapractice.transactional.domain.Payment;
import com.example.javapractice.transactional.domain.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void pay(Long orderId, int amount, FailFlag flag) {
        paymentRepository.save(new Payment(null, orderId, "PAID", amount));

        if (flag == FailFlag.PAYMENT) throw new RuntimeException("payment error");
    }
}
