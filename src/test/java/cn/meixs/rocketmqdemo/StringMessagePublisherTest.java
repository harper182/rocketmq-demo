package cn.meixs.rocketmqdemo;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class StringMessagePublisherTest {
    public static final String NAMESRV_ADDR = "127.0.0.1:9876";
    public static final String GROUP = "GROUP";
    public static final String TOPIC = "TOPIC";
    public static final String TAG = "AA";

    private static SimpleProducer producer;
    private static SimpleConsumer consumer;

    @BeforeClass
    public static void setUp() throws Exception {
        producer = new SimpleProducer(NAMESRV_ADDR, GROUP);
        producer.init();

        consumer = new SimpleConsumer(String.class, NAMESRV_ADDR, GROUP, TOPIC, TAG);
        consumer.init();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        consumer.destroy();
        producer.destroy();
    }

    @Test
    public void should_publish_and_receive_string_message() throws Exception {
        producer.send(TOPIC + ":" + TAG, "hello");

        TimeUnit.SECONDS.sleep(1);

        assertEquals("hello", consumer.getReceivedObject());
    }
}
