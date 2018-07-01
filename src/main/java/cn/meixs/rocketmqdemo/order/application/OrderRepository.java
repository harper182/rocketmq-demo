package cn.meixs.rocketmqdemo.order.application;

import cn.meixs.rocketmqdemo.order.domain.Order;

public interface OrderRepository {
    Order findBy(String orderId);

    void save(Order order);
}
