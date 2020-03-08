package top.ingxx.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.manager.service.ItemService;
import top.ingxx.pojo.TbItem;

import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Reference
    private ItemService itemService;

    /**
     * 通过商品id查找
     * @param id
     * @return
     */
    @RequestMapping("/findItemByGoodsId")
    public List<TbItem> findItemByGoodsId(Long id) {
        return itemService.findItemByGoodsId(id);
    }
}
