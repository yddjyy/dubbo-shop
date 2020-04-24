package top.ingxx.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.comments.service.CommentsService;
import top.ingxx.entity.PageResult;
import top.ingxx.pojo.TbComments;
import top.ingxx.pojo.TbOrderItem;
import top.ingxx.pojo.TbUser;
import top.ingxx.untils.entity.PygResult;
import top.ingxx.user.service.UserOrderService;
import top.ingxx.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentsController {
    private static Logger logger = Logger.getLogger(CommentsController.class.getName());
    public static String PRODUCT_RATING_PREFIX = "PRODUCT_RATING_PREFIX";

     @Reference(timeout = 5000)
     CommentsService commentsService;

     @Reference
     UserOrderService userOrderService;

     @Reference
     UserService userService;

     @RequestMapping("/findAll")
     public PageResult findAll(int currentPage, int pageSize){

         return  commentsService.findAll(currentPage,pageSize);
     }
     @RequestMapping("/add")
     public PygResult insertone(@RequestBody TbComments tbComments){
         tbComments.setPublishtime((int) (System.currentTimeMillis()/1000));//评论时间
         tbComments.setVisits(0);//访问量
         tbComments.setThumbup(0);//评论的点赞数
         //获取订单信息
         TbOrderItem orderItem = userOrderService.findOneItemByOrderItemId(Long.parseLong(tbComments.getOrderitemid()+""));
         tbComments.setGoodsname(orderItem.getTitle());
         tbComments.setOrderid(orderItem.getOrderId()+"");
         tbComments.setSpuid(orderItem.getGoodsId()+"");
         tbComments.setSellerid(orderItem.getSellerId());
         tbComments.setItemId(orderItem.getItemId().intValue());
         TbUser userByUserName = userService.findUserByUserName(tbComments.getNickname());
         tbComments.setNickname(userByUserName.getNickName());
         tbComments.setUserid(userByUserName.getId().intValue());
        commentsService.insertOneTbComments(tbComments);
        userOrderService.updateStatusByOrderId(orderItem.getOrderId(),8);
         //TODO 当前用户未登录时应该怎么存
         System.out.print("=========点击了查看 埋点=========");
         String username = SecurityContextHolder.getContext().getAuthentication().getName();
         TbUser userInfo = userService.findUserByUserName(username);
         if(userInfo!=null){
             logger.info(PRODUCT_RATING_PREFIX + ":" + userInfo.getId() +"|"+ orderItem.getGoodsId() +"|"+ tbComments.getRating() +"|"+ System.currentTimeMillis()/1000);
         }
        return new PygResult(true,"评论成功--"+tbComments);
     }
     @RequestMapping("/queryTbCommentsBySpuId")
    public PageResult queryTbCommentsBySpuId(String spuid, int currentPage, int PageSize){
         PageResult pageResult =  commentsService.queryTbCommentsBySpuId(spuid,currentPage, PageSize);
         List<TbComments> list = pageResult.getRows();
         //遍历集合获取子评论
         for(TbComments tbComments : list){
             //得到子评论
           PageResult pageResult1 =   commentsService.queryTbCommentsByParentsId(tbComments.get_id(),1,10);
           //将子评论放到父评论的list集合中
             tbComments.setList(pageResult1.getRows());
         }
         return pageResult;
    }
    @RequestMapping("/queryTbCommentsByParentsId")
    public PageResult queryTbCommentsByParentsId(String parentsid, int currentPage, int pageSize){
         return  commentsService.queryTbCommentsByParentsId(parentsid,currentPage,pageSize);
    }
    @RequestMapping("/deleteOneTbComments")
    public PygResult deleteOneTbComments(String id){

         commentsService.deleteOneTbComments(id);
         return new PygResult(true,"删除成功");
    }

    @RequestMapping("/findByName")
    public PageResult findByName(String nickname, int currentPage, int pageSize){
         return commentsService.findByName(nickname,currentPage,pageSize);
    }
   @RequestMapping("/find")
    public TbComments find(String id){
         return commentsService.find(id);
    }
}
