package cn.meixs.rocketmqdemo.order.application;

import cn.meixs.rocketmqdemo.inventory.application.InventoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderApplicationTest {
    @Autowired
    OrderService orderService;
    @Autowired
    InventoryRepository repository;

    @Test
    public void should_prepare_inventory_when_order_paid() throws Exception {
        orderService.pay("1111", new BigDecimal("100"));

        waitingMessage();

        assertTrue(repository.isSaved());
    }

    private void waitingMessage() throws InterruptedException {
        int i = 0;
        while (i <= 100) {
            i++;
            if (repository.isSaved()) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(300);
        }
    }
}
