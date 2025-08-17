package com.example.javapractice.optimisticlock.retry;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
// @Transactional은 Ordered.LOWEST_PRECEDENCE 이므로 그것보다 우선적으로 실행되게 설정
// 기본적으로 @Transactional 보다 더 낮은 우선순위는 설정 안됨
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@Aspect
@Component
public class OptimisticLockRetryAspect {

    @Around("@annotation(retry)")
    public Object retryOptimisticLock(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {

        System.out.println(">> retry before / isTxStart: " + TransactionSynchronizationManager.isActualTransactionActive());
        Exception exceptionHolder = null;
        for (int attempt = 0; attempt < retry.maxRetries(); attempt++) {
            try {
                return joinPoint.proceed();
            } catch (OptimisticLockingFailureException e) {
                log.error("{} 번 낙관적락 발생!", attempt + 1);
                exceptionHolder = e;
                Thread.sleep(retry.retryDelay());
            }finally {
                System.out.println(">> retry after / isTxStart: " + TransactionSynchronizationManager.isActualTransactionActive());
            }
        }
        throw exceptionHolder;
    }
}
