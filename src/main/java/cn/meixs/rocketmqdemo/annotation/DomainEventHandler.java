package cn.meixs.rocketmqdemo.annotation;

public interface DomainEventHandler<T> {
    void handle(T message);
}
