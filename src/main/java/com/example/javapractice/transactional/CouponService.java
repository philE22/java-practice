package com.example.javapractice.transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    @Transactional(propagation = Propagation.NESTED)
    public void apply(Long orderId, String code) {
        // 일부 작업 기록 후, 조건에 따라 예외 발생해 nested 롤백 실험
        if ("BOOM".equals(code)) throw new RuntimeException("coupon failed");
    }
}
