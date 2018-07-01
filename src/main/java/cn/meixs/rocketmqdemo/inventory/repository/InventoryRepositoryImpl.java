package cn.meixs.rocketmqdemo.inventory.repository;

import cn.meixs.rocketmqdemo.inventory.application.InventoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryRepositoryImpl implements InventoryRepository {
    private boolean saved = false;
    @Override
    public void save() {
        saved = true;
    }

    @Override
    /**
     * just for test
     */
    public boolean isSaved() {
        return saved;
    }
}
