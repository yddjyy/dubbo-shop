package top.ingxx.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Document(collection = "comments")
public class TbComments implements Serializable {

    @Id
    @Field(value = "_id")
    private String _id;
    /**
     * 订单id
     */
    @Field
    private String orderid;
    /**
     * 订单订单商品详情id
     */
    @Field
    private String orderitemid;
    /**
     * 商品id
     */
    @Field
    private String spuid;
    /**
     * 评论
     */
    @Field
    private String commentscontent;
    /**
     * 商品名称
     */
    @Field
    private String goodsname;
    /**
     *商品规格
     */
    @Field
    private String goodsspec;
    /**
     * 评论时间
     */
    @Field
    private Integer publishtime;
    /**
     * 评论用户id
     */
    @Field
    private Integer userid;

    /**
     * 商品itemid
     *
     */
    @Field
    private int itemId;
    /**
     * 商家id
     */
    @Field
    private String sellerid;
    /**
     * 评论用户昵称
     */
    @Field
    private String nickname;
    /**
     * 是否已经评论
     */
    @Field
    private Integer visits;
    /**
     * 评论的点赞数
     */
    @Field
    private Integer thumbup;
    /**
     * 评论中的图片
     */
    @Field
    private List<String> images;
    /**
     * 评论的回复数
     */
    @Field
    private Integer commentnum;
    /**
     * 该评论的上一级id
     */
    @Field
    private String parentid;
    /**
     * 是否是顶级评论
     */
    @Field
    private int isparent;
    /**
     * 评论的类型:
     *    用于标志是否为商家回评:0表示不是是 1表示商家回评 2 追加评论
     */
    @Field
    private Double rating;

    /**
     * List<TbComments>存储子评论</TbComments>
     * @return
     */
    private List<TbComments> list ;

    public TbComments() {
    }

    public TbComments(String _id, String orderid, String orderitemid, String spuid, String commentscontent, String goodsname, String goodsspec, Integer publishtime, Integer userid, int itemId, String sellerid, String nickname, Integer visits, Integer thumbup, List<String> images, Integer commentnum, String parentid, int isparent, Double rating, List<TbComments> list) {
        this._id = _id;
        this.orderid = orderid;
        this.orderitemid = orderitemid;
        this.spuid = spuid;
        this.commentscontent = commentscontent;
        this.goodsname = goodsname;
        this.goodsspec = goodsspec;
        this.publishtime = publishtime;
        this.userid = userid;
        this.itemId = itemId;
        this.sellerid = sellerid;
        this.nickname = nickname;
        this.visits = visits;
        this.thumbup = thumbup;
        this.images = images;
        this.commentnum = commentnum;
        this.parentid = parentid;
        this.isparent = isparent;
        this.rating = rating;
        this.list = list;
    }

    @Override
    public String toString() {
        return "TbComments{" +
                "_id='" + _id + '\'' +
                ", orderid='" + orderid + '\'' +
                ", orderitemid='" + orderitemid + '\'' +
                ", spuid='" + spuid + '\'' +
                ", commentscontent='" + commentscontent + '\'' +
                ", goodsname='" + goodsname + '\'' +
                ", goodsspec='" + goodsspec + '\'' +
                ", publishtime=" + publishtime +
                ", userid=" + userid +
                ", itemId=" + itemId +
                ", sellerid='" + sellerid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", visits=" + visits +
                ", thumbup=" + thumbup +
                ", images=" + images +
                ", commentnum=" + commentnum +
                ", parentid='" + parentid + '\'' +
                ", isparent=" + isparent +
                ", rating=" + rating +
                ", list=" + list +
                '}';
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOrderitemid() {
        return orderitemid;
    }

    public void setOrderitemid(String orderitemid) {
        this.orderitemid = orderitemid;
    }

    public String getSpuid() {
        return spuid;
    }

    public void setSpuid(String spuid) {
        this.spuid = spuid;
    }

    public String getCommentscontent() {
        return commentscontent;
    }

    public void setCommentscontent(String commentscontent) {
        this.commentscontent = commentscontent;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public String getGoodsspec() {
        return goodsspec;
    }

    public void setGoodsspec(String goodsspec) {
        this.goodsspec = goodsspec;
    }

    public Integer getPublishtime() {
        return publishtime;
    }

    public void setPublishtime(Integer publishtime) {
        this.publishtime = publishtime;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getSellerid() {
        return sellerid;
    }

    public void setSellerid(String sellerid) {
        this.sellerid = sellerid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getVisits() {
        return visits;
    }

    public void setVisits(Integer visits) {
        this.visits = visits;
    }

    public Integer getThumbup() {
        return thumbup;
    }

    public void setThumbup(Integer thumbup) {
        this.thumbup = thumbup;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Integer getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(Integer commentnum) {
        this.commentnum = commentnum;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public int getIsparent() {
        return isparent;
    }

    public void setIsparent(int isparent) {
        this.isparent = isparent;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<TbComments> getList() {
        return list;
    }

    public void setList(List<TbComments> list) {
        this.list = list;
    }
}
