package cn.meixs.rocketmqdemo.core;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DomainEventPublishTest {
    private static final String NAMESRV_ADDR = "127.0.0.1:9876";
    private static final String GROUP = "GROUP-DOMAIN-EVENT";
    private static final String TOPIC = "TOPIC";
    private static final int WAIT_SECONDS = 1;
    private SimpleConsumer consumer;

    @Test
    public void should_support_domain_event() throws Exception {
        SimpleProducer producer = getSimpleProducer(GROUP);
        String tag = "should_support_domain_event";
        DummySubscriber subscriber = prepareConsumerAndSubscriber(GROUP, TOPIC, tag);
        try {
            SampleDomainEvent event = getDomainEvent();
            producer.send(TOPIC + ":" + tag, event);

            TimeUnit.SECONDS.sleep(WAIT_SECONDS);

            assertEquals(event, subscriber.getReceivedObject());
        } finally {
            consumer.destroy();
            producer.destroy();
        }
    }

    @Test
    public void should_support_retry_when_event_consumer_failed() throws Exception {
        SimpleProducer producer = getSimpleProducer(GROUP);
        String tag = "should_support_retry_when_event_consumer_failed";
        DummySubscriber subscriber = prepareConsumerAndSubscriber(GROUP, TOPIC, tag);
        subscriber.setMockFail(true);
        try {
            SampleDomainEvent event = getDomainEvent();
            producer.send(TOPIC + ":" + tag, event);

            waitingMessage(subscriber, 1);

            assertTrue(subscriber.getFailedTimes() >= 1);
            assertEquals(event, subscriber.getReceivedObject());
        } finally {
            producer.destroy();
            consumer.destroy();
        }
    }

    @Test
    public void should_support_subscribe_multiple_topic() throws Exception {
        SimpleProducer producer = getSimpleProducer(GROUP);
        String tag = "should_support_subscribe_multiple_topic";
        DummySubscriber subscriber1 = new DummySubscriber(GROUP, TOPIC, tag);
        DummySubscriber subscriber2 = new DummySubscriber(GROUP, TOPIC+"1", tag);
        consumer = new SimpleConsumer(NAMESRV_ADDR, GROUP, Arrays.asList(subscriber1, subscriber2));
        consumer.init();
        try {
            SampleDomainEvent event = getDomainEvent();
            producer.send(TOPIC + ":" + tag, event);
            producer.send(TOPIC + "1:" + tag, event);

            waitingMessage(subscriber1, 1);
            waitingMessage(subscriber2, 1);

            assertEquals(1, subscriber1.getReceivedCount());
            assertEquals(1, subscriber2.getReceivedCount());
        } finally {
            consumer.destroy();
            producer.destroy();
        }
    }

    @Test
    public void should_support_subscribe_multiple_tag() throws Exception {
        int expectedCount = 1;
        String tag1 = "should_support_subscribe_multiple_tag";
        String tag2 = "1"+tag1;
        multiple_tag_test(tag1, tag2, expectedCount);
    }

    @Test
    public void should_support_subscribe_multiple_tag2() throws Exception {
        int expected = 2;
        String tag1 = "should_support_subscribe_multiple_tag2";
        String tag2 = tag1 + "||1" + tag1;

        multiple_tag_test(tag1, tag2, expected);
    }

    private void multiple_tag_test(String tag1, String tag2, int expectedCount) throws Exception {
        SimpleProducer producer = getSimpleProducer(GROUP);
        DummySubscriber subscriber1 = new DummySubscriber(GROUP, TOPIC, tag1);
        DummySubscriber subscriber2 = new DummySubscriber(GROUP, TOPIC, tag2);
        consumer = new SimpleConsumer(NAMESRV_ADDR, GROUP, Arrays.asList(subscriber2, subscriber1));
        consumer.init();
        try {
            SampleDomainEvent event = getDomainEvent();
            producer.send(TOPIC + ":" + tag1, event);
            producer.send(TOPIC + ":1" + tag1, event);

            waitingMessage(subscriber1, 1);
            waitingMessage(subscriber2, expectedCount);

            assertEquals(1, subscriber1.getReceivedCount());
            assertEquals(expectedCount, subscriber2.getReceivedCount());
        } finally {
            consumer.destroy();
            producer.destroy();
        }
    }

    @Test
    public void should_support_multiple_group_handle_same_topic() throws Exception {
        SimpleProducer producer = getSimpleProducer(GROUP);
        String tag = "should_support_multiple_group_handle_same_topic";
        DummySubscriber subscriber1 = prepareConsumerAndSubscriber(GROUP, TOPIC, tag);
        DummySubscriber subscriber2 = new DummySubscriber(GROUP, TOPIC, tag);
        SimpleConsumer anotherConsumer = new SimpleConsumer(NAMESRV_ADDR, GROUP+"1", Arrays.asList(subscriber2));
        anotherConsumer.init();
        try {
            SampleDomainEvent event = getDomainEvent();
            producer.send(TOPIC + ":" + tag, event);

            waitingMessage(subscriber1, 1);
            waitingMessage(subscriber2, 1);

            assertEquals(1, subscriber1.getReceivedCount());
            assertEquals(1, subscriber2.getReceivedCount());
        } finally {
            consumer.destroy();
            anotherConsumer.destroy();
            producer.destroy();
        }
    }

    private void waitingMessage(DummySubscriber subscriber, int minCount) throws InterruptedException {
        int i = 0;
        while (i <= 100) {
            i++;
            if (subscriber.getReceivedCount() >= minCount) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(300);
        }
    }

    private SampleDomainEvent getDomainEvent() {
        return new SampleDomainEvent("1", 123L, new Date(), new BigDecimal("12.88"), new BusinessNo("asdfafe"));
    }

    private DummySubscriber prepareConsumerAndSubscriber(String group, String topic, String tags) throws Exception {
        DummySubscriber subscriber = new DummySubscriber(GROUP, topic, tags);

        consumer = new SimpleConsumer(NAMESRV_ADDR, group, Arrays.asList(subscriber));
        consumer.init();

        return subscriber;
    }

    private SimpleProducer getSimpleProducer(String group) throws Exception {
        SimpleProducer simpleProducer = new SimpleProducer(NAMESRV_ADDR, group);
        simpleProducer.init();
        return simpleProducer;
    }

    @Test
    public void should_split_tags_given_express() throws Exception {
        Set<String> tags = new HashSet<>();
        String subExpression = "asbsdf_sadf";
        tags.addAll(Arrays.asList(subExpression.split("\\|\\|")));

        assertEquals(1, tags.size());
    }

    @Test
    public void should_split_tags_given_multi_express() throws Exception {
        Set<String> tags = new HashSet<>();
        String subExpression = "asbsdf_sadf||dbe";
        tags.addAll(Arrays.asList(subExpression.split("\\|\\|")));

        assertEquals(2, tags.size());
    }

    @Test
    public void should_init_correct_topic_infos() throws Exception {
        List<RocketMQListener> subscribers = Arrays.asList(
                new DummySubscriber(GROUP, "topic1", "tag1"),
                new DummySubscriber(GROUP, "topic1", "tag1"),
                new DummySubscriber(GROUP, "topic2", "tag1"),
                new DummySubscriber(GROUP, "topic2", "tag2"),
                new DummySubscriber(GROUP, "topic3", "tag3||tag4"),
                new DummySubscriber(GROUP, "topic3", "tag4||tag3"),
                new DummySubscriber(GROUP, "topic3", "tag5")
        );

        consumer = new SimpleConsumer("", "", subscribers);

        List<TopicInfo> topicInfos = consumer.getTopicInfos();

        assertEquals(3, topicInfos.size());
        assertEquals(1, topicInfos.get(0).getTags().size());
        assertEquals(2, topicInfos.get(1).getTags().size());
        assertEquals(3, topicInfos.get(2).getTags().size());
    }
}
