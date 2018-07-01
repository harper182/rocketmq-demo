package cn.meixs.rocketmqdemo.order.application;

import cn.meixs.rocketmqdemo.order.domain.DomainEvent;

import java.util.List;

public interface DomainEventRepository {
    void save(List<DomainEvent> events);

    void updateSentStatus(DomainEvent event);

    List<DomainEvent> findToBeSentEvents();
}
