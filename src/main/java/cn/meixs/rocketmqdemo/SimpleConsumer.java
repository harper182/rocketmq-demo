package cn.meixs.rocketmqdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

@Slf4j
public class SimpleConsumer {
    private final String charset = "UTF-8";
    private ObjectMapper objectMapper = new ObjectMapper();

    private DefaultMQPushConsumer consumer;
    private Object receivedObject;

    private Class messageType;
    private String group;
    private String namesrvAddr;
    private String topic;
    private String subExpression;

    public SimpleConsumer(Class messageType, String namesrvAddr, String group, String topic, String subExpression) {
        this.messageType = messageType;
        this.group = group;
        this.namesrvAddr = namesrvAddr;
        this.topic = topic;
        this.subExpression = subExpression;
    }

    public synchronized void init() throws Exception {
        if (Objects.isNull(consumer)) {
            Assert.notNull(group, "group cannot be null");
            Assert.notNull(namesrvAddr, "namesrvAddr cannot be null");
            Assert.notNull(topic, "topic cannot be null");
            Assert.notNull(subExpression, "subExpression cannot be null");

            DefaultMQPushConsumer dummyConsumer = new DefaultMQPushConsumer(group);
            dummyConsumer.setNamesrvAddr(namesrvAddr);
            dummyConsumer.setMessageModel(MessageModel.CLUSTERING);
            dummyConsumer.subscribe(topic, subExpression);
            dummyConsumer.setMessageListener(new DefaultMessageListenerConcurrently());
            dummyConsumer.start();

            consumer = dummyConsumer;
        }
    }

    public void destroy() {
        if (Objects.nonNull(consumer)) {
            consumer.shutdown();
        }
        log.info("consumer destroyed, {}", this.toString());
    }

    protected class DefaultMessageListenerConcurrently implements MessageListenerConcurrently {
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt messageExt : msgs) {
                log.debug("received msg: {}", messageExt);
                try {
                    handleMessage(doConvertMessage(messageExt));
                } catch (Exception e) {
                    log.error("consume message failed. messageExt:{}", messageExt, e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    protected void handleMessage(Object object) {
        this.receivedObject = object;
    }

    Object getReceivedObject() {
        return receivedObject;
    }

    private Object doConvertMessage(MessageExt messageExt) {
        if (Objects.equals(messageType, MessageExt.class)) {
            return messageExt;
        } else {
            String str = new String(messageExt.getBody(), Charset.forName(charset));
            if (Objects.equals(messageType, String.class)) {
                return str;
            } else {
                // if msgType not string, use objectMapper change it.
                try {
                    return objectMapper.readValue(str, messageType);
                } catch (Exception e) {
                    log.info("convert failed. str:{}, msgType:{}", str, messageType);
                    throw new RuntimeException("cannot convert message to " + messageType, e);
                }
            }
        }
    }

}
