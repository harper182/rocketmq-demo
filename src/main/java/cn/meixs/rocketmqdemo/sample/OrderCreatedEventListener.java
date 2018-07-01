package cn.meixs.rocketmqdemo.sample;

import cn.meixs.rocketmqdemo.mq.RocketMQListener;
import cn.meixs.rocketmqdemo.mq.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener
public class OrderCreatedEventListener implements RocketMQListener<OrderCreatedEvent> {
    private OrderCreatedEvent receivedEvent;
    private int receivedCount;

    @Value("${rocketmq.consumer.group}")
    private String consumerGroup;

    @Override
    public Class getMessageType() {
        return OrderCreatedEvent.class;
    }

    @Override
    public String getGroup() {
        return consumerGroup;
    }

    public String getTopic() {
        return "ORDER";
    }

    @Override
    public String getTags() {
        return "OrderCreated";
    }

    @Override
    public void handle(OrderCreatedEvent message) {
        receivedEvent = message;
        receivedCount++;
    }

    public OrderCreatedEvent getReceivedEvent() {
        return receivedEvent;
    }

    public int getReceivedCount() {
        return receivedCount;
    }

    public void clear() {
        receivedCount = 0;
        receivedEvent = null;
    }
}
