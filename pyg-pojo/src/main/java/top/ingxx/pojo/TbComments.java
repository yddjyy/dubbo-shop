package top.ingxx.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
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
     * 商品id
     */
    @Field
    private String spuid;
    /**
     * 评论商品
     */
    @Field
    private String contentsGoods;
    /**
     * 评价服务
     */
    @Field
    private String contentService;
    /**
     * 描述相符
     */
    @Field
    private int starDesc;
    /**
     * 商家服务
     */
    @Field
    private int starService;
    /*
     物流服务
     */
    @Field
    private int starLogi;
    /**
     * 评论时间
     */
    @Field
    private Date publishtime;
    /**
     * 评论用户id
     */
    @Field
    private String userid;
    /**
     * 评论用户昵称
     */
    @Field
    private String nickname;
    /**
     * 评论的浏览量
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
    private Integer comment;
    /**
     * 该评论是否可以被回复
     */
    @Field
    private Boolean iscomment;
    /**
     * 该评论的上一级id
     */
    @Field
    private String parentid;
    /**
     * 是否是顶级评论
     */
    @Field
    private Boolean isparent;
    /**
     * 评论的类型:
     *    用于标志是否为商家回评:0表示不是 1表示商家回评 2 追加评论
     */
    @Field
    private String type;

    /**
     * List<TbComments>存储子评论</TbComments>
     * @return
     */
    private List<TbComments> list ;

    public TbComments() {
    }

    public TbComments(String _id, String orderid, String spuid, String contentsGoods, String contentService, int starDesc, int starService, int starLogi, Date publishtime, String userid, String nickname, Integer visits, Integer thumbup, List<String> images, Integer comment, Boolean iscomment, String parentid, Boolean isparent, String type) {
        this._id = _id;
        this.orderid = orderid;
        this.spuid = spuid;
        this.contentsGoods = contentsGoods;
        this.contentService = contentService;
        this.starDesc = starDesc;
        this.starService = starService;
        this.starLogi = starLogi;
        this.publishtime = publishtime;
        this.userid = userid;
        this.nickname = nickname;
        this.visits = visits;
        this.thumbup = thumbup;
        this.images = images;
        this.comment = comment;
        this.iscomment = iscomment;
        this.parentid = parentid;
        this.isparent = isparent;
        this.type = type;
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

    public String getSpuid() {
        return spuid;
    }

    public void setSpuid(String spuid) {
        this.spuid = spuid;
    }

    public String getContentsGoods() {
        return contentsGoods;
    }

    public void setContentsGoods(String contentsGoods) {
        this.contentsGoods = contentsGoods;
    }

    public String getContentService() {
        return contentService;
    }

    public void setContentService(String contentService) {
        this.contentService = contentService;
    }

    public int getStarDesc() {
        return starDesc;
    }

    public void setStarDesc(int starDesc) {
        this.starDesc = starDesc;
    }

    public int getStarService() {
        return starService;
    }

    public void setStarService(int starService) {
        this.starService = starService;
    }

    public int getStarLogi() {
        return starLogi;
    }

    public void setStarLogi(int starLogi) {
        this.starLogi = starLogi;
    }

    public Date getPublishtime() {
        return publishtime;
    }

    public void setPublishtime(Date publishtime) {
        this.publishtime = publishtime;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public Integer getComment() {
        return comment;
    }

    public void setComment(Integer comment) {
        this.comment = comment;
    }

    public Boolean getIscomment() {
        return iscomment;
    }

    public void setIscomment(Boolean iscomment) {
        this.iscomment = iscomment;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public Boolean getIsparent() {
        return isparent;
    }

    public void setIsparent(Boolean isparent) {
        this.isparent = isparent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TbComments> getList() {
        return list;
    }

    public void setList(List<TbComments> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "TbComments{" +
                "_id='" + _id + '\'' +
                ", orderid='" + orderid + '\'' +
                ", spuid='" + spuid + '\'' +
                ", contentsGoods='" + contentsGoods + '\'' +
                ", contentService='" + contentService + '\'' +
                ", starDesc=" + starDesc +
                ", starService=" + starService +
                ", starLogi=" + starLogi +
                ", publishtime=" + publishtime +
                ", userid='" + userid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", visits=" + visits +
                ", thumbup=" + thumbup +
                ", images=" + images +
                ", comment=" + comment +
                ", iscomment=" + iscomment +
                ", parentid='" + parentid + '\'' +
                ", isparent=" + isparent +
                ", type='" + type + '\'' +
                ", list=" + list +
                '}';
    }
}
