package cn.meixs.rocketmqdemo.order.infrastructure;

import cn.meixs.rocketmqdemo.mq.SimpleProducer;
import cn.meixs.rocketmqdemo.order.application.DomainEventDispatcher;
import cn.meixs.rocketmqdemo.order.application.DomainEventRepository;
import cn.meixs.rocketmqdemo.order.domain.DomainEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;

@Service
public class DomainEventDispatcherImpl implements DomainEventDispatcher {
    private DomainEventRepository repository;
    private SimpleProducer simpleProducer;

    @Autowired
    public DomainEventDispatcherImpl(DomainEventRepository repository,
                                     @Value("${rocketmq.name-server}") final String namesrvAddr,
                                     @Value("${rocketmq.producer.group}") final String producerGroup) {
        this.repository = repository;
        simpleProducer = new SimpleProducer(namesrvAddr, producerGroup);
        simpleProducer.init();
    }

    @Override
    public void saveAndDispatch(List<DomainEvent> events) {
        repository.save(events);

        dispatchEvents(events);
    }

    @Override
    @Async
    public void dispatchEvents(List<DomainEvent> events) {
        //异步把消息发送出去
        for (DomainEvent event : events) {
            dispatch(event);
        }
    }

    private void dispatch(DomainEvent event) {
        System.out.println("sent msg :"+event.getTopic() +":"+event.getTag());
        simpleProducer.send(event.getTopic() + ":" + event.getTag(), event);
        repository.updateSentStatus(event);
    }

    @PreDestroy
    public void cleanUp() {
        simpleProducer.destroy();
    }

}
