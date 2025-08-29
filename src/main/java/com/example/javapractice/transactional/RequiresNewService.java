package com.example.javapractice.transactional;

import com.example.javapractice.transactional.domain.SomeEntity;
import com.example.javapractice.transactional.domain.SomeEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequiresNewService {

    private final SomeEntityRepository someEntityRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void apply(FailFlag flag) {
        SomeEntity entity = new SomeEntity(null, "REQUIRES_NEW");
        someEntityRepository.save(entity);

        entity.setMessage("REQUIRES_NEW modified");

        if (flag == FailFlag.REQUIRES_NEW) throw new RuntimeException("REQUIRES NEW error");
    }
}
