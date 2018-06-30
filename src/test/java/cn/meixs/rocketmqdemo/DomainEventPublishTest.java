package cn.meixs.rocketmqdemo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DomainEventPublishTest {
    private static final String NAMESRV_ADDR = "127.0.0.1:9876";
    private static final String GROUP = "GROUP-DOMAIN-EVENT";
    private static final String TOPIC = "TOPIC";
    private static final String TAG = "EVENT_TAG";
    private static final int WAIT_SECONDS = 1;

    private SimpleProducer producer;
    private SimpleConsumer consumer;

    @Before
    public void setUp() throws Exception {
        producer = new SimpleProducer(NAMESRV_ADDR, GROUP);
        producer.init();

        consumer = new SimpleConsumer(DomainEvent.class, NAMESRV_ADDR, GROUP, TOPIC, TAG);
        consumer.init();
    }

    @After
    public void tearDown() throws Exception {
        try {
            consumer.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            producer.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void should_support_domain_event() throws Exception {
        DomainEvent event = new DomainEvent("1", 123L, new Date(), new BigDecimal("12.88"), new BusinessNo("asdfafe"));
        producer.send(TOPIC + ":" + TAG, event);

        TimeUnit.SECONDS.sleep(WAIT_SECONDS);

        assertEquals(event, consumer.getReceivedObject());
    }

    @Test
    public void should_support_retry_when_event_consumer_failed() throws Exception {
        consumer.destroy();

        FakeSimpleConsumer fakeSimpleConsumer = new FakeSimpleConsumer(DomainEvent.class, NAMESRV_ADDR, GROUP, TOPIC, TAG);
        fakeSimpleConsumer.init();
        try {
            DomainEvent event = new DomainEvent("1", 123L, new Date(), new BigDecimal("12.88"), new BusinessNo("asdfafe"));

            producer.send(TOPIC + ":" + TAG, event);
            TimeUnit.SECONDS.sleep(5);

            assertTrue(fakeSimpleConsumer.getRetriedTimes() > 1);
        } finally {
            fakeSimpleConsumer.destroy();
        }
    }

    public static class FakeSimpleConsumer extends SimpleConsumer {
        FakeSimpleConsumer(Class messageType, String namesrvAddr, String group, String topic, String subExpression) {
            super(messageType, namesrvAddr, group, topic, subExpression);
        }

        private int i = 0;

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
