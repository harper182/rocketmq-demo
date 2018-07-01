package cn.meixs.rocketmqdemo.mq;

public interface RocketMQListener<T> {
    Class getMessageType();

    String getGroup();

    String getTopic();

    String getTags();

    void handle(T message);
}
