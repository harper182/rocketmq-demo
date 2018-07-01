package cn.meixs.rocketmqdemo;

import cn.meixs.rocketmqdemo.core.SimpleProducer;
import cn.meixs.rocketmqdemo.sample.OrderCreatedEvent;
import cn.meixs.rocketmqdemo.sample.OrderCreatedEventListener;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderCreatedListenerTest {
    private static final String NAMESRV_ADDR = "127.0.0.1:9876";
    private static final String ORDER_ID = "ORDER_ID";

    @Autowired
    private OrderCreatedEventListener listener;

    @After
    public void tearDown() throws Exception {
        listener.clear();
    }

    @Test
    public void should_support_annotation_subscriber() throws Exception {
        SimpleProducer simpleProducer = new SimpleProducer(NAMESRV_ADDR, "GROUP");
        simpleProducer.init();

        OrderCreatedEvent message = new OrderCreatedEvent(ORDER_ID);
        simpleProducer.send(listener.getTopic() + ":" + listener.getTags(), message);

        waitingMessage(1);

        assertEquals(message, listener.getReceivedEvent());
        assertEquals(1, listener.getReceivedCount());
    }

    private void waitingMessage(int minCount) throws InterruptedException {
        int i = 0;
        while (i <= 100) {
            i++;
            if (listener.getReceivedCount() >= minCount) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(300);
        }
    }
}
