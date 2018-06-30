package cn.meixs.rocketmqdemo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.Message;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringMessagePublisherTest {
    public static final String NAMESRV_ADDR = "127.0.0.1:9876";
    public static final String GROUP = "GROUP-STRING";
    public static final String TOPIC = "TOPIC";
    public static final String TAG = "AA";
    private static final int TIMEOUT = 1;

    private SimpleProducer producer;
    private SimpleConsumer consumer;

    @Before
    public void setUp() throws Exception {
        producer = new SimpleProducer(NAMESRV_ADDR, GROUP);
        producer.init();

        consumer = new SimpleConsumer(String.class, NAMESRV_ADDR, GROUP, TOPIC, TAG);
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
    public void should_publish_and_receive_string_message() throws Exception {
        producer.send(TOPIC + ":" + TAG, "hello");

        TimeUnit.SECONDS.sleep(TIMEOUT);

        assertEquals("hello", consumer.getReceivedObject());
    }

    @Test
    public void should_NOT_receive_specific_tag_message() throws Exception {
        String dummyTag = TAG + "1";
        producer.send(TOPIC + ":" + dummyTag, "hello");

        TimeUnit.SECONDS.sleep(TIMEOUT);

        assertNull(consumer.getReceivedObject());
    }

    @Test
    public void should_receive_multiple_tag_message() throws Exception {
        consumer.destroy(); //can not consumer the same group twice in a single process.

        String anotherTag = TAG + "1";
        SimpleConsumer anotherConsumer = new SimpleConsumer(String.class, NAMESRV_ADDR, GROUP, TOPIC, TAG + "||" + anotherTag);
        anotherConsumer.init();
        try {
            String message = "hello again";
            producer.send(TOPIC + ":" + TAG, message);
            producer.send(TOPIC + ":" + anotherTag, message);

            TimeUnit.SECONDS.sleep(TIMEOUT);

            assertEquals(message, anotherConsumer.getReceivedObject());
            assertEquals(2, anotherConsumer.getReceivedObjectCount());
        } finally {
            anotherConsumer.destroy();
        }
    }



}
