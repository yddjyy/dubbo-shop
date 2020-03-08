package top.ingxx.manager.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import top.ingxx.pojo.TbGoods;
import top.ingxx.manager.service.GoodsService;

import top.ingxx.pojo.TbItem;
import top.ingxx.pojoGroup.Goods;
import top.ingxx.search.service.ItemSearchService;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.entity.PygResult;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Reference(timeout = 10000)
    private ItemSearchService itemSearchService;
    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

//	/**
//	 * 增加
//	 * @param goods
//	 * @return
//	 */
//	@RequestMapping("/add")
//	public PygResult add(@RequestBody TbGoods goods){
//		try {
//			goodsService.add(goods);
//			return new PygResult(true, "增加成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new PygResult(false, "增加失败");
//		}
//	}

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public PygResult update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
            return new PygResult(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public PygResult delete(Long[] ids) {
        try {
            goodsService.delete(ids);
            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
            return new PygResult(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param goods
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        return goodsService.findPage(goods, page, rows);
    }

    /**
     * 商品审核
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("/updateStatus")
    public PygResult updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            if("1".equals(status)){//如审核通过
                List<TbItem> itemList = goodsService.findItemListByGoodsIdListAndStatus(ids, status);
                itemSearchService.importList(itemList);
            }
            return new PygResult(true,"审核成功");
        }catch (Exception e){
            e.printStackTrace();
            return new PygResult(false,"审核失败");
        }
    }

}
