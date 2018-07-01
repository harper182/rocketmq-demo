package cn.meixs.rocketmqdemo.order.application;

import cn.meixs.rocketmqdemo.order.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderService {
    private OrderRepository repository;
    private DomainEventDispatcher eventDispatcher;

    @Autowired
    public OrderService(OrderRepository repository, DomainEventDispatcher eventDispatcher) {
        this.repository = repository;
        this.eventDispatcher = eventDispatcher;
    }

    //@Transactional
    public void pay(String orderId, BigDecimal price) {
        Order order = repository.findBy(orderId);
        order.pay(price);
        repository.save(order);

        eventDispatcher.dispatch(order.getEvents());
    }
}
