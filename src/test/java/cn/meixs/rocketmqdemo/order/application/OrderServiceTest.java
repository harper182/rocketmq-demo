package cn.meixs.rocketmqdemo.order.application;

import cn.meixs.rocketmqdemo.order.domain.Order;
import cn.meixs.rocketmqdemo.order.domain.OrderStatus;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Test
    public void should_change_to_PAID_when_money_paid() throws Exception {
        Order order = new Order("1111", new BigDecimal("100"));
        OrderRepository repository = mock(OrderRepository.class);
        when(repository.findBy(anyString())).thenReturn(order);

        DomainEventDispatcher dispatcher = mock(DomainEventDispatcher.class);

        OrderService service = new OrderService(repository, dispatcher);
        service.pay("1111", new BigDecimal("100"));

        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(dispatcher, times(1)).saveAndDispatch(anyList());
    }
}