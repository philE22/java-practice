package com.example.javapractice.transactional;

import com.example.javapractice.transactional.domain.AuditLog;
import com.example.javapractice.transactional.domain.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String msg, FailFlag flag) {
        auditLogRepository.save(new AuditLog(null, msg, null));
        if (flag == FailFlag.AUDIT_FIRST) throw new RuntimeException("audit 1 error");
        if (flag == FailFlag.AUDIT_SECOND) throw new RuntimeException("audit 2 error");
    }
}
