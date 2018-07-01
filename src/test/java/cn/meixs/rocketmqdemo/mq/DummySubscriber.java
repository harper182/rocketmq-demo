package cn.meixs.rocketmqdemo.mq;

public class DummySubscriber implements RocketMQListener {
    private String topic;
    private String tags;
    private String consumerGroup;

    private Object receivedObject;
    private int receivedCount;

    private int failedTimes = 0;
    private boolean mockFail = false;

    public DummySubscriber(String consumerGroup, String topic, String tags) {
        this.topic = topic;
        this.tags = tags;
        this.consumerGroup = consumerGroup;
    }

    @Override
    public Class getMessageType() {
        return SampleDomainEvent.class;
    }

    @Override
    public String getGroup() {
        return consumerGroup;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public String getTags() {
        return tags;
    }

    @Override
    public void handle(Object message) {
        if (failedTimes == 0 && mockFail) {
            failedTimes++;
            throw new RuntimeException("Failed to handle message");
        }

        receivedObject = message;
        receivedCount++;
    }

    public Object getReceivedObject() {
        return receivedObject;
    }

    public int getReceivedCount() {
        return receivedCount;
    }

    public void setMockFail(boolean mockFail) {
        this.mockFail = mockFail;
    }

    public int getFailedTimes() {
        return failedTimes;
    }
}
