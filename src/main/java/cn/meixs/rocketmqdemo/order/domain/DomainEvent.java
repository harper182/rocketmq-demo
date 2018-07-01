package cn.meixs.rocketmqdemo.order.domain;

public abstract class DomainEvent {
    protected String topic;
    protected String tag;

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public abstract String getTopic();
    public abstract String getTag();
}
