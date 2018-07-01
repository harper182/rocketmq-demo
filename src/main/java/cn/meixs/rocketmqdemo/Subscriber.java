package cn.meixs.rocketmqdemo;

import cn.meixs.rocketmqdemo.annotation.DomainEventHandler;

public interface Subscriber extends DomainEventHandler{
    Class getMessageType();

    String getTopic();

    String getTags();
}
