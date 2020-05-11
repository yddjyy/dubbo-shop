package top.ingxx.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TbSeckillGoods implements Serializable {
    private Long id;

    /**
    * spu ID
    */
    private Long goodsId;

    /**
    * sku ID
    */
    private Long itemId;

    /**
    * 标题
    */
    private String title;

    /**
    * 商品图片
    */
    private String smallPic;

    /**
    * 原价格
    */
    private BigDecimal price;

    /**
    * 秒杀价格
    */
    private BigDecimal costPrice;

    /**
    * 商家ID
    */
    private String sellerId;

    /**
    * 添加日期
    */
    private Date createTime;

    /**
    * 审核日期
    */
    private Date checkTime;

    /**
    * 审核状态 0 未审核 1 审核通过 3 删除
    */
    private String status;

    /**
    * 开始日期
    */
    private String startDate;

    /**
    * 结束日期
    */
    private String endDate;

    /**
    * 秒杀商品数
    */
    private Integer num;

    /**
    * 剩余库存数
    */
    private Integer stockCount;

    /**
    * 描述
    */
    private String introduction;

    /**
    * 结束时间
    */
    private String endTime;

    /**
    * 开始时间
    */
    private String startTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSmallPic() {
        return smallPic;
    }

    public void setSmallPic(String smallPic) {
        this.smallPic = smallPic;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}