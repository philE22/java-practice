package com.example.javapractice.transactional;

import com.example.javapractice.transactional.domain.Notification;
import com.example.javapractice.transactional.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void send(Long orderId) {
        // 트랜잭션 없이 즉시 저장됨 (오토커밋처럼 동작)
        notificationRepository.save(new Notification(null, orderId, "EMAIL", true));
    }
}
