package cn.meixs.rocketmqdemo.sample;

public class OrderCreatedEvent {
    private String orderId;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderCreatedEvent that = (OrderCreatedEvent) o;

        return orderId.equals(that.orderId);
    }

    @Override
    public int hashCode() {
        return orderId.hashCode();
    }
}
