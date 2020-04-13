package top.ingxx.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.comments.service.CommentsService;
import top.ingxx.entity.PageResult;
import top.ingxx.pojo.TbComments;
import top.ingxx.untils.entity.PygResult;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentsController {

     @Reference(timeout = 5000)
     CommentsService commentsService;

     @RequestMapping("/findAll")
     public PageResult findAll(int currentPage, int pageSize){

         return  commentsService.findAll(currentPage,pageSize);
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

    @RequestMapping("/findBysellerid")
    public PageResult findByName( int currentPage, int pageSize){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
         return commentsService.findByName(name,currentPage,pageSize);
    }
   @RequestMapping("/find")
    public TbComments find(String id){
         return commentsService.find(id);
    }
    @RequestMapping("/update")
    public PygResult updateByid(String id,String content){
        System.out.println(content+"================================");
        TbComments tbComments = new TbComments();
        tbComments.setParentid(id);
        tbComments.setCommentscontent(content);
        tbComments.setIsparent(1);
        commentsService.insertOneTbComments(tbComments);
        commentsService.updateByid(id,1);
         return new PygResult(true,"成功");
    }
}
