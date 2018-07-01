package cn.meixs.rocketmqdemo.order.application;

import cn.meixs.rocketmqdemo.order.domain.DomainEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventCompensationService {
    private DomainEventRepository repository;
    private DomainEventDispatcher dispatcher;

    @Autowired
    public EventCompensationService(DomainEventRepository repository, DomainEventDispatcher dispatcher) {
        this.repository = repository;
        this.dispatcher = dispatcher;
    }

    /**
     * 消息补偿
     * todo: 定时触发
     */
    public void compensate() {
        List<DomainEvent> events = repository.findToBeSentEvents();
        dispatcher.dispatchEvents(events);
    }
}
