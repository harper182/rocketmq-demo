package cn.meixs.rocketmqdemo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class TopicInfo {
    private String topic;
    private String subExpression;
}
