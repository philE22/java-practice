package com.example.javapractice.transactional;

import com.example.javapractice.transactional.domain.Inventory;
import com.example.javapractice.transactional.domain.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repository;

    @Transactional // REQUIRED
    public void reserve(String sku, int qty) {
        Inventory inventory = repository.findBySku(sku).orElseThrow();
        inventory.decrease(qty);
    }
}
