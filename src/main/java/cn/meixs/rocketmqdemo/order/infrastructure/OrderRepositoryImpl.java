package cn.meixs.rocketmqdemo.order.infrastructure;

import cn.meixs.rocketmqdemo.order.application.OrderRepository;
import cn.meixs.rocketmqdemo.order.domain.Order;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    @Override
    public Order findBy(String orderId) {
        return new Order("1111", new BigDecimal("100"));
    }

    @Override
    public void save(Order order) {

    }
}
