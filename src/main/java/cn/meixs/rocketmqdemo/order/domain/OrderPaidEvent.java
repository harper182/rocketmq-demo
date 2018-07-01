package cn.meixs.rocketmqdemo.order.domain;

public class OrderPaidEvent extends DomainEvent {
    private String orderId;

    public OrderPaidEvent(String orderId) {
        super();
        this.orderId = orderId;
    }

    public OrderPaidEvent() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String getTopic() {
        return "ORDER";
    }

    @Override
    public String getTag() {
        return "ORDERPAID";
    }


}
