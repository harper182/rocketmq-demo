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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SimpleConsumer {
    private static final String TAG_SEPARATOR_REGEX = "\\|\\|";
    private final String charset = "UTF-8";
    private ObjectMapper objectMapper = new ObjectMapper();

    private DefaultMQPushConsumer consumer;
    private Object receivedObject;
    private int count;

    private String group;
    private String namesrvAddr;
    private List<TopicInfo> topicInfos;
    private List<Subscriber> subscribers = new ArrayList<>();

    public SimpleConsumer(String namesrvAddr, String group, List<Subscriber> subscribers) {
        this.group = group;
        this.namesrvAddr = namesrvAddr;
        this.subscribers = new ArrayList<>(subscribers);
        initTopicInfos();
    }

    private void initTopicInfos() {
        topicInfos = new ArrayList<>();
        Map<String, Set<Set<String>>> topicMap = subscribers.stream().collect(
                Collectors.groupingBy(Subscriber::getTopic,
                        Collectors.mapping(Subscriber::getTags,
                                Collectors.mapping(a -> Arrays.stream(a.split("\\|\\|"))
                                        .collect(Collectors.toSet()), Collectors.toSet()))
                ));
        for (Map.Entry<String, Set<Set<String>>> entry : topicMap.entrySet()) {
            Set<String> collect = entry.getValue().stream().flatMap(x -> x.stream()).collect(Collectors.toSet());
            topicInfos.add(new TopicInfo(entry.getKey(), collect.stream().collect(Collectors.joining("||"))));
        }
    }

    public synchronized void init() throws Exception {
        if (Objects.isNull(consumer)) {
            Assert.notNull(group, "group cannot be null");
            Assert.notNull(namesrvAddr, "namesrvAddr cannot be null");
            Assert.notEmpty(topicInfos, "topicInfos cannot be null");

            DefaultMQPushConsumer dummyConsumer = new DefaultMQPushConsumer(group);
            dummyConsumer.setNamesrvAddr(namesrvAddr);
            dummyConsumer.setMessageModel(MessageModel.CLUSTERING);
            for (TopicInfo topicInfo : topicInfos) {
                dummyConsumer.subscribe(topicInfo.getTopic(), topicInfo.getSubExpression());
            }
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

    List<TopicInfo> getTopicInfos() {
        return topicInfos;
    }

    protected class DefaultMessageListenerConcurrently implements MessageListenerConcurrently {
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt messageExt : msgs) {
                try {
                    log.debug("received messageExt:{}", messageExt);
                    handleMessage(messageExt);
                } catch (Exception e) {
                    log.error("consume message failed. messageExt:{}", messageExt, e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    private void handleMessage(MessageExt messageExt) {
        for (Subscriber subscriber : subscribers) {
            if (isMatch(subscriber, messageExt)) {
                Object object = doConvertMessage(messageExt, subscriber.getMessageType());
                subscriber.handle(object);

                handleMessage2(object);
            }
        }
    }

    private boolean isMatch(Subscriber subscriber, MessageExt messageExt) {
        if (subscriber.getTopic().equalsIgnoreCase(messageExt.getTopic())) {
            String tags = messageExt.getTags();
            if (Objects.nonNull(tags) && tags.length() > 0) {
                Set<String> messageTags = new HashSet<>(Arrays.asList(messageExt.getTags().split(TAG_SEPARATOR_REGEX)));
                Set<String> subscriberTags = new HashSet<>(Arrays.asList(subscriber.getTags().split(TAG_SEPARATOR_REGEX)));
                subscriberTags.retainAll(messageTags);
                return subscriberTags.size() > 0;
            }
        }

        log.info("subscribe NOT match");
        return false;
    }

    private boolean isTagMatch(Set<String> tags1, String tagString) {
        Set<String> results = new HashSet<>(tags1);
        results.retainAll(Arrays.asList(tagString.split(TAG_SEPARATOR_REGEX)));

        return results.size() > 0;
    }

    private boolean isTopicMatch(String topic1, String topic2) {
        return Objects.equals(topic1, topic2);
    }

    protected void handleMessage2(Object object) {
        this.receivedObject = object;
        this.count++;
    }

    Object getReceivedObject() {
        return receivedObject;
    }

    int getReceivedObjectCount() {
        return count;
    }

    private Object doConvertMessage(MessageExt messageExt, Class messageType) {
        if (Objects.equals(messageType, MessageExt.class)) {
            return messageExt;
        }

        String messageBody = new String(messageExt.getBody(), Charset.forName(charset));
        if (Objects.equals(messageType, String.class)) {
            return messageBody;
        }

        try {
            return objectMapper.readValue(messageBody, messageType);
        } catch (Exception e) {
            log.error("convert message failed. msgType:{}, message:{}", messageType, messageBody);
            throw new RuntimeException("cannot convert message to " + messageType, e);
        }
    }

}
