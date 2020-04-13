package top.ingxx.pojoGroup;

import top.ingxx.pojo.TbOrderItem;
import top.ingxx.pojo.TbRefund;

import java.io.Serializable;

public class RefundOrder implements Serializable {

    private TbOrderItem tbOrderItem;
    private TbRefund tbRefund;

    public TbOrderItem getTbOrderItem() {
        return tbOrderItem;
    }

    public void setTbOrderItem(TbOrderItem tbOrderItem) {
        this.tbOrderItem = tbOrderItem;
    }

    public TbRefund getTbRefund() {
        return tbRefund;
    }

    public void setTbRefund(TbRefund tbRefund) {
        this.tbRefund = tbRefund;
    }
}
