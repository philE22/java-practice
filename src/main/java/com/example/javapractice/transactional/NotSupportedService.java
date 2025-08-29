package com.example.javapractice.transactional;

import com.example.javapractice.transactional.domain.SomeEntity;
import com.example.javapractice.transactional.domain.SomeEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotSupportedService {

    private final SomeEntityRepository someEntityRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void apply(FailFlag flag) {
        // 저장 작업
        SomeEntity entity = new SomeEntity(null, "NOT_SUPPORTED");
        someEntityRepository.save(entity);

        entity.setMessage("NOT_SUPPORTED modified");
        if (flag == FailFlag.NOT_SUPPORTED) throw new RuntimeException("not supported error");
    }
}
