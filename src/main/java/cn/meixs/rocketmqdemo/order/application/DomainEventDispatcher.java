package cn.meixs.rocketmqdemo.order.application;

import cn.meixs.rocketmqdemo.order.domain.DomainEvent;
import org.springframework.scheduling.annotation.Async;

import java.util.List;


public interface DomainEventDispatcher {
    void saveAndDispatch(List<DomainEvent> events);

    @Async
    void dispatchEvents(List<DomainEvent> events);
}
