package cn.meixs.rocketmqdemo.order.domain;

import java.math.BigDecimal;

public class Order extends AggregateRoot{
    private String orderId;
    private OrderStatus status;
    private BigDecimal price;

    public Order(String orderId, BigDecimal price) {
        super();
        this.orderId = orderId;
        this.price = price;
        status = OrderStatus.CREATED;
    }

    public void pay(BigDecimal price) {
        if (this.price.equals(price)) {
            status = OrderStatus.PAID;
            addEvent(new OrderPaidEvent(orderId));
        } else {
            throw new RuntimeException("money is not equals.");
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
