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
public class RequiresNewService {

    private final TestEntityRepository testEntityRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void apply(Order order, FailFlag flag) {
        testEntityRepository.save(new TestEntity(null, "REQUIRES_NEW"));

        order.setMessage("REQUIRES_NEW");

        if (flag == FailFlag.REQUIRES_NEW) throw new RuntimeException("REQUIRES NEW error");
    }
}
