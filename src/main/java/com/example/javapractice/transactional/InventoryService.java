package com.example.javapractice.transactional;

import com.example.javapractice.transactional.domain.Inventory;
import com.example.javapractice.transactional.domain.InventoryRepository;
import com.example.javapractice.transactional.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repository;

    @Transactional
    public void reserve(String sku, int qty, Order order, FailFlag flag) {
        Inventory inventory = repository.findBySku(sku).orElseThrow();
        inventory.decrease(qty);

        if (flag == FailFlag.INVENTORY) throw new RuntimeException("inventory error");
    }
}
