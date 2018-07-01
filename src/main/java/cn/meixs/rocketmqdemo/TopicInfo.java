package cn.meixs.rocketmqdemo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TopicInfo {
    private String topic;
    private String subExpression;
    private Set<String> tags;

    public TopicInfo(String topic, String subExpression) {
        this.topic = topic;
        this.subExpression = subExpression;
        tags = new HashSet<>();
        tags.addAll(Arrays.asList(subExpression.split("\\|\\|")));
    }

    public String getTopic() {
        return topic;
    }

    public String getSubExpression() {
        return subExpression;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopicInfo topicInfo = (TopicInfo) o;

        if (!topic.equals(topicInfo.topic)) return false;
        return subExpression.equals(topicInfo.subExpression);
    }

    @Override
    public int hashCode() {
        int result = topic.hashCode();
        result = 31 * result + subExpression.hashCode();
        return result;
    }
}
