package cn.meixs.rocketmqdemo;

import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DomainEventPublishTest {
    private static final String NAMESRV_ADDR = "127.0.0.1:9876";
    private static final String GROUP = "GROUP-DOMAIN-EVENT";
    private static final String TOPIC = "TOPIC";
    private static final String TAG = "EVENT_TAG";
    private static final int WAIT_SECONDS = 2;

    @Test
    public void should_support_domain_event() throws Exception {
        SimpleProducer producer = getSimpleProducer(GROUP);
        String tag = "should_support_domain_event";
        SimpleConsumer consumer = getSimpleConsumer(GROUP, Arrays.asList(new TopicInfo(TOPIC, tag)));
        try {
            DomainEvent event = new DomainEvent("1", 123L, new Date(), new BigDecimal("12.88"), new BusinessNo("asdfafe"));
            producer.send(TOPIC + ":" + tag, event);

            TimeUnit.SECONDS.sleep(WAIT_SECONDS);

            assertEquals(event, consumer.getReceivedObject());
        } finally {
            consumer.destroy();
            producer.destroy();
        }
    }

    @Ignore
    @Test
    public void should_support_retry_when_event_consumer_failed() throws Exception {
        SimpleProducer producer = getSimpleProducer(GROUP);
        String tag = "should_support_retry_when_event_consumer_failed";
        FakeSimpleConsumer fakeSimpleConsumer = new FakeSimpleConsumer(DomainEvent.class, NAMESRV_ADDR, GROUP, Arrays.asList(new TopicInfo(TOPIC, tag)));
        fakeSimpleConsumer.init();
        try {
            DomainEvent event = new DomainEvent("1", 123L, new Date(), new BigDecimal("12.88"), new BusinessNo("asdfafe"));

            producer.send(TOPIC + ":" + tag, event);
            TimeUnit.SECONDS.sleep(10);

            assertTrue(fakeSimpleConsumer.getRetriedTimes() > 1);
        } finally {
            producer.destroy();
            fakeSimpleConsumer.destroy();
        }
    }

    @Test
    public void should_support_subscribe_multiple_topic() throws Exception {
        SimpleProducer producer = getSimpleProducer(GROUP);
        String tag = "should_support_subscribe_multiple_topic";
        SimpleConsumer consumer = getSimpleConsumer(GROUP, Arrays.asList(
                new TopicInfo(TOPIC, tag), new TopicInfo(TOPIC + "1", tag)));
        try {
            DomainEvent event = new DomainEvent("1", 123L, new Date(), new BigDecimal("12.88"), new BusinessNo("asdfafe"));
            producer.send(TOPIC + ":" + tag, event);
            producer.send(TOPIC + "1:" + tag, event);

            TimeUnit.SECONDS.sleep(WAIT_SECONDS);

            assertEquals(2, consumer.getReceivedObjectCount());
        } finally {
            consumer.destroy();
            producer.destroy();
        }
    }

    @Test
    public void should_support_multiple_group_handle_same_topic() throws Exception {
        SimpleProducer producer = getSimpleProducer(GROUP);
        String tag = "should_support_multiple_group_handle_same_topic";
        SimpleConsumer consumer = getSimpleConsumer(GROUP, Arrays.asList(new TopicInfo(TOPIC, tag)));
        SimpleConsumer anotherConsumer = getSimpleConsumer(GROUP + "1", Arrays.asList(new TopicInfo(TOPIC, tag)));
        try {
            DomainEvent event = new DomainEvent("1", 123L, new Date(), new BigDecimal("12.88"), new BusinessNo("asdfafe"));
            producer.send(TOPIC + ":" + tag, event);

            TimeUnit.SECONDS.sleep(WAIT_SECONDS);

            assertEquals(1, consumer.getReceivedObjectCount());
            assertEquals(1, anotherConsumer.getReceivedObjectCount());
        } finally {
            anotherConsumer.destroy();
            producer.destroy();
        }
    }

    private SimpleConsumer getSimpleConsumer(String group, List<TopicInfo> topicInfos) throws Exception {
        SimpleConsumer simpleConsumer = new SimpleConsumer(DomainEvent.class, NAMESRV_ADDR, group, topicInfos);
        simpleConsumer.init();
        return simpleConsumer;
    }

    private SimpleProducer getSimpleProducer(String group) throws Exception {
        SimpleProducer simpleProducer = new SimpleProducer(NAMESRV_ADDR, group);
        simpleProducer.init();
        return simpleProducer;
    }

    public static class FakeSimpleConsumer extends SimpleConsumer {
        private int i = 0;

        FakeSimpleConsumer(Class messageType, String namesrvAddr, String group, List<TopicInfo> topicInfos) {
            super(messageType, namesrvAddr, group, topicInfos);
        }

        @Override
        protected void handleMessage(Object object) {
            i++;
            if (i <= 2) {
                throw new RuntimeException("Failed to handle message");
            }
            super.handleMessage(object);
        }

        int getRetriedTimes() {
            return i;
        }
    }

}
