package cn.meixs.rocketmqdemo.inventory.application;

import cn.meixs.rocketmqdemo.mq.RocketMQListener;
import cn.meixs.rocketmqdemo.mq.RocketMQMessageListener;
import cn.meixs.rocketmqdemo.order.domain.OrderPaidEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener
public class OrderPaidEventListener implements RocketMQListener<OrderPaidEvent>{
    private InventoryService service;

    @Autowired
    public OrderPaidEventListener(InventoryService service) {
        this.service = service;
    }

    @Override
    public Class getMessageType() {
        return OrderPaidEvent.class;
    }

    @Override
    public String getGroup() {
        return "ORDER_GROUP";
    }

    @Override
    public String getTopic() {
        return "ORDER";
    }

    @Override
    public String getTags() {
        return "ORDERPAID";
    }

    @Override
    public void handle(OrderPaidEvent message) {
        System.out.println("listener triggered......");
        service.prepareInventory(message.getOrderId());
    }
}
