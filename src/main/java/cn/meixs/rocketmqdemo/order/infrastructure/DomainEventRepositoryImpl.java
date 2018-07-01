package cn.meixs.rocketmqdemo.order.infrastructure;

import cn.meixs.rocketmqdemo.order.application.DomainEventRepository;
import cn.meixs.rocketmqdemo.order.domain.DomainEvent;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DomainEventRepositoryImpl implements DomainEventRepository {
    @Override
    public void save(List<DomainEvent> events) {

    }

    @Override
    public void updateSentStatus(DomainEvent event) {

    }

    @Override
    public List<DomainEvent> findToBeSentEvents() {
        return null;
    }
}
