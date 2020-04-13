package top.ingxx.page.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.comments.service.CommentsService;
import top.ingxx.entity.PageResult;
import top.ingxx.page.service.ItemPageService;

import java.util.Map;

@RestController
@RequestMapping("/page")
public class PageController {

    @Reference(timeout = 600000)
    private ItemPageService itemPageService;

    @Reference
    private CommentsService commentsService;

    @RequestMapping("/getPage")
    public Map getPage(Long goodsId) {

        return itemPageService.getPage(goodsId);

    }
    @RequestMapping("/getCommentsInfo")
    public PageResult getCommentsInfo(String spuid, int currentPage, int pageSize) {

        return commentsService.queryTbCommentsBySpuId(spuid,currentPage,pageSize);

    }

}
