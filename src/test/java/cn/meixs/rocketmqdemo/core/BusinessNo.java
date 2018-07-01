package cn.meixs.rocketmqdemo.core;

public class BusinessNo {
    private String no;

    public BusinessNo(String no) {
        this.no = no;
    }

    public BusinessNo() {
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusinessNo that = (BusinessNo) o;

        return no.equals(that.no);
    }

    @Override
    public int hashCode() {
        return no.hashCode();
    }
}
