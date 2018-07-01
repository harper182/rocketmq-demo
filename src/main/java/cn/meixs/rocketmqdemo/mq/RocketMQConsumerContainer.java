package cn.meixs.rocketmqdemo.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;

@Slf4j
public class RocketMQConsumerContainer implements DisposableBean {
    private SimpleConsumer consumer;
    private String namesrvAddr;
    private String consumerGroup;
    private List<RocketMQListener> subscribers;

    @Override
    public void destroy() throws Exception {
        if (consumer != null) {
            try {
                consumer.destroy();
            } catch (Exception e) {
                log.error("failed to destroy consumer.", e);
            }
        }
    }

    public void start() {
        initConsumer();
    }

    private synchronized void initConsumer() {
        if (consumer == null) {
            SimpleConsumer simpleConsumer = new SimpleConsumer(namesrvAddr, consumerGroup, subscribers);

            consumer = simpleConsumer;
            try {
                consumer.init();
            } catch (Exception e) {
                log.error("failed to init consumer", e);
                throw new RuntimeException("failed to init consumer");
            }
        }
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public List<RocketMQListener> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<RocketMQListener> subscribers) {
        this.subscribers = subscribers;
    }
}
