package cn.meixs.rocketmqdemo.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Order
@Configuration
public class RocketMQListenerConfiguration implements ApplicationContextAware, InitializingBean {

    private AtomicLong counter = new AtomicLong(0);
    private ConfigurableApplicationContext applicationContext;
    @Value("${rocketmq.name-server}")
    private String namesrvAddr;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(RocketMQMessageListener.class);

        Map<String, List<RocketMQListener>> subscribers = groupByConsumerGroup(beans);
        for (Map.Entry<String, List<RocketMQListener>> entry : subscribers.entrySet()) {
            createRocketMQContainer(entry.getKey(), entry.getValue());
        }
    }

    private void createRocketMQContainer(String consumerGroup, List<RocketMQListener> subscribers) {
        BeanDefinitionBuilder beanBuilder = BeanDefinitionBuilder.rootBeanDefinition(RocketMQConsumerContainer.class);
        beanBuilder.addPropertyValue("namesrvAddr", namesrvAddr);
        beanBuilder.addPropertyValue("subscribers", subscribers);
        beanBuilder.addPropertyValue("consumerGroup", consumerGroup);
        beanBuilder.setDestroyMethodName("destroy");

        String containerBeanName = String.format("%s_%s", RocketMQConsumerContainer.class.getName(), counter.incrementAndGet());
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
        beanFactory.registerBeanDefinition(containerBeanName, beanBuilder.getBeanDefinition());

        RocketMQConsumerContainer container = beanFactory.getBean(containerBeanName, RocketMQConsumerContainer.class);

        try {
            container.start();
        } catch (Exception e) {
            log.error("started rocketmq listener container failed. {}", container, e);
            throw new RuntimeException(e);
        }

        log.info("register rocketMQ listener to container, containerBeanName:{}", containerBeanName);
    }

    private Map<String, List<RocketMQListener>> groupByConsumerGroup(Map<String, Object> beans) {
        Map<String, List<RocketMQListener>> subscribers = new HashMap<>();
        for (Map.Entry<String, Object> beanEntry : beans.entrySet()) {
            Assert.notNull(beanEntry.getKey(), beanEntry.getValue() + " has no group");
            if (!RocketMQListener.class.isAssignableFrom(beanEntry.getValue().getClass())) {
                throw new IllegalStateException(beanEntry.getValue() + " is not instance of " + RocketMQListener.class.getName());
            }
            RocketMQListener rocketMQListener = (RocketMQListener) beanEntry.getValue();
            List<RocketMQListener> list = subscribers.get(rocketMQListener.getGroup());
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(rocketMQListener);
            subscribers.put(rocketMQListener.getGroup(), list);
        }
        return subscribers;
    }
}

