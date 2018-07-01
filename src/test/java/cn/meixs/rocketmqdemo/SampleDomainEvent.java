package cn.meixs.rocketmqdemo;

import java.math.BigDecimal;
import java.util.Date;

public class SampleDomainEvent {
    private String id;
    private long quantity;
    private Date date;
    private BigDecimal price;
    private BusinessNo businessNo;

    public SampleDomainEvent() {
    }

    public SampleDomainEvent(String id, long quantity, Date date, BigDecimal price, BusinessNo businessNo) {
        this.id = id;
        this.quantity = quantity;
        this.date = date;
        this.price = price;
        this.businessNo = businessNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SampleDomainEvent that = (SampleDomainEvent) o;

        if (quantity != that.quantity) return false;
        if (!id.equals(that.id)) return false;
        if (!date.equals(that.date)) return false;
        if (!price.equals(that.price)) return false;
        return businessNo.equals(that.businessNo);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (int) (quantity ^ (quantity >>> 32));
        result = 31 * result + date.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + businessNo.hashCode();
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BusinessNo getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(BusinessNo businessNo) {
        this.businessNo = businessNo;
    }
}
