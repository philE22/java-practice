package com.example.javapractice.transactional;

public enum FailFlag {
    NONE,
    ORDER,
    AUDIT_FIRST,
    INVENTORY,
    PAYMENT,
    AUDIT_SECOND,
    REQUIRES_NEW,
    NOT_SUPPORTED,
    ;
}
