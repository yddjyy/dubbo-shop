package top.ingxx.pojoGroup;

import top.ingxx.pojo.TbOrder;

import java.io.Serializable;

public class RefundOrderShop implements Serializable {

    private String outTradeNo;

    private String id;

    private TbOrder tbOrder;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TbOrder getTbOrder() {
        return tbOrder;
    }

    public void setTbOrder(TbOrder tbOrder) {
        this.tbOrder = tbOrder;
    }
}
