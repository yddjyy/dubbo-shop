package top.ingxx.page.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.comments.service.CommentsService;
import top.ingxx.entity.PageResult;
import top.ingxx.page.service.ItemPageService;
import top.ingxx.pojo.TbUser;

import java.util.Map;

@RestController
@RequestMapping("/page")
public class PageController {
    private static Logger logger = Logger.getLogger(PageController.class.getName());
    public static String PRODUCT_RATING_PREFIX = "PRODUCT_RATING_PREFIX";
    @Reference(timeout = 600000)
    private ItemPageService itemPageService;

    @Reference
    private CommentsService commentsService;

    @RequestMapping("/getPage")
    public Map getPage(Long goodsId) {
        //TODO 当前用户未登录时应该怎么存
        System.out.print("=========点击了查看 埋点=========");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbUser userInfo = itemPageService.getUserInfo(username);
        if(userInfo!=null){
            logger.info(PRODUCT_RATING_PREFIX + ":" + userInfo.getId() +"|"+ goodsId +"|"+ "1.0" +"|"+ System.currentTimeMillis()/1000);
        }
        return itemPageService.getPage(goodsId);
    }
    @RequestMapping("/getCommentsInfo")
    public PageResult getCommentsInfo(String spuid, int currentPage, int pageSize) {

        return commentsService.queryTbCommentsBySpuId(spuid,currentPage,pageSize);

    }

}
