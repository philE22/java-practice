package com.example.javapractice.transactional;

import com.example.javapractice.transactional.domain.Order;
import com.example.javapractice.transactional.domain.TestEntity;
import com.example.javapractice.transactional.domain.TestEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotSupportedService {

    private final TestEntityRepository testEntityRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void apply(Order order, FailFlag flag) {
        // 저장 작업
        TestEntity entity = new TestEntity(null, "NOT_SUPPORTED");
        testEntityRepository.save(entity);

        // 다른 엔티티 수정
        order.setMessage("NOT_SUPPORTED");

        if (flag == FailFlag.NOT_SUPPORTED) throw new RuntimeException("not supported error");
    }
}
