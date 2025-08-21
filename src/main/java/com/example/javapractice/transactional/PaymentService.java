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
    public void pay(Long orderId, int amount, boolean failFlag) {
        paymentRepository.save(new Payment(null, orderId, failFlag ? "FAILED" : "PAID", amount));

        if (failFlag) throw new RuntimeException("payment gateway error");
    }
}
