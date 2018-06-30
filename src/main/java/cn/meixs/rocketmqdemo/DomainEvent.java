package cn.meixs.rocketmqdemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
class DomainEvent {
    private String id;
    private long quantity;
    private Date date;
    private BigDecimal price;
    private BusinessNo businessNo;

}
