package cn.meixs.rocketmqdemo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class DomainEventPublishTest {
    public static final String NAMESRV_ADDR = "127.0.0.1:9876";
    public static final String GROUP = "GROUP-DOMAIN-EVENT";
    public static final String TOPIC = "TOPIC";
    public static final String TAG = "EVENT_TAG";
    private static final int TIMEOUT = 1;

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

        TimeUnit.SECONDS.sleep(TIMEOUT);

        assertEquals(event, consumer.getReceivedObject());

    }


}
