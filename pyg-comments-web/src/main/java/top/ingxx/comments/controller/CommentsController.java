package top.ingxx.comments.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.comments.service.CommentsService;
import top.ingxx.entity.PageResult;
import top.ingxx.pojo.TbComments;
import top.ingxx.untils.entity.PygResult;

import java.util.List;

@RestController
@RequestMapping("/comments")
@CrossOrigin
public class CommentsController {

     @Reference
     CommentsService commentsService;

     @RequestMapping("/findAll")
     public PageResult findAll(int currentPage, int pageSize){

         return  commentsService.findAll(currentPage,pageSize);
     }
     @RequestMapping("/add")
     public PygResult insertone(@RequestBody TbComments tbComments){
         //tbComments.setPublishtime(new Date());
         tbComments.setVisits(0);
         tbComments.setThumbup(0);
         //tbComments.setComment(0);
         tbComments.setIsparent(0);
//         测试
//         tbComments.setOrderid("12134567897");
//         tbComments.setSpuid("12345645465545");
//         tbComments.setUserid("12345");
//         tbComments.setNickname("战三马子");
//         tbComments.setParentid("0");
//         tbComments.setIsparent(true);
//         List list = new ArrayList();
//         list.add("图片一");
//         list.add("图片2");
//         tbComments.setImages(list);
//         tbComments.setContentsGoods("评价商品");
//         tbComments.setContentService("评价服务");
//         tbComments.setStarDesc(5);
//         tbComments.setStarService(4);
//         tbComments.setStarLogi(2);
//         tbComments.setType("初次评论");
         commentsService.insertOneTbComments(tbComments);
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
