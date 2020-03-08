package top.ingxx.pojoGroup;

import top.ingxx.pojo.TbOrder;
import top.ingxx.pojo.TbOrderItem;

import java.io.Serializable;

/**
 * 功能简述<br>
 *
 * @author Asus
 * @create 2019/4/18
 */
public class Orders implements Serializable {

    private TbOrder tbOrder;
    private TbOrderItem tbOrderItem;
    private String nickName;
    private String goodsSpec;
    public String getGoodsSpec() {
        return goodsSpec;
    }

    public void setGoodsSpec(String goodsSpec) {
        this.goodsSpec = goodsSpec;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public TbOrder getTbOrder() {
        return tbOrder;
    }

    public void setTbOrder(TbOrder tbOrder) {
        this.tbOrder = tbOrder;
    }

    public TbOrderItem getTbOrderItem() {
        return tbOrderItem;
    }

    public void setTbOrderItem(TbOrderItem tbOrderItem) {
        this.tbOrderItem = tbOrderItem;
    }
}
