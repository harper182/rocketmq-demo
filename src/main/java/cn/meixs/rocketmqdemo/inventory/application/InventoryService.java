package cn.meixs.rocketmqdemo.inventory.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private InventoryRepository repository;

    @Autowired
    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    public void prepareInventory(String orderId) {
        //query order
        //prepare inventory
        //save inventory
        repository.save();
    }
}
