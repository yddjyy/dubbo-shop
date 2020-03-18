package top.ingxx.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.manager.service.GoodsService;
import top.ingxx.manager.service.ItemService;
import top.ingxx.pojo.TbGoods;
import top.ingxx.pojo.TbItem;
import top.ingxx.pojoGroup.Goods;
import top.ingxx.search.service.ItemSearchService;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.entity.PygResult;

import java.util.ArrayList;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Reference
	private ItemService itemService;

	@Reference
	private ItemSearchService itemSearchService;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public PygResult add(@RequestBody Goods goods){
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.getGoods().setSellerId(sellerId);
        try {
			goodsService.add(goods);
			return new PygResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public PygResult update(@RequestBody Goods goods){
	    //判断操作的商品和数据库中商家是否一样  判断操作商品和当前登录用户是否一样
        Goods one = goodsService.findOne(goods.getGoods().getId());
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!one.getGoods().getSellerId().equals(sellerId) || !goods.getGoods().getSellerId().equals(sellerId)){
            return new PygResult(false,"非法操作");
        }
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
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids goodsid
	 * @return
	 */
	@RequestMapping("/delete")
	@Transactional
	public PygResult delete(Long [] ids){
		try {
			goodsService.delete(ids);
			//从solr中清除
			ArrayList<Long> list = new ArrayList<>();
			for(Long id:ids){
				list.add(id);
				//设置sku状态为 删除状态（3）
				List<TbItem> itemByGoodsId = itemService.findItemByGoodsId(id);
				for(TbItem tbItem : itemByGoodsId){
					tbItem.setStatus("3");
					itemService.update(tbItem);
				}
			}
			itemSearchService.deleteByGoodsIds(list);

			return new PygResult(true, "删除成功"); 
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return new PygResult(false, "删除失败");
		}
	}
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
        //获取商家ID
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
	    goods.setSellerId(sellerId);
		return goodsService.findPage(goods, page, rows);		
	}

	/**
	 * 更新sku商品上下架
	 * @param add skuid
	 * @param remove skuid
	 * @return
	 */
	@RequestMapping("/updateSku")
	@Transactional
	public PygResult updateSku(Long[] add,Long[] remove){

		//更改sku的状态，是否上架
		try {
			if(add.length!=0){
				//上架
				itemService.updateStatus(add, "1");
				//添加sku
				ArrayList<TbItem> list = new ArrayList<>();
				for(Long id: add){
					list.add(itemService.findOne(id));
				}
				//加入缓存
				itemSearchService.importList(list);
			}
			if(remove.length!=0){
				//下架
				itemService.updateStatus(remove, "2");
				ArrayList<Long> RemoveListIds = new ArrayList<>();
				for(Long id: remove){
					RemoveListIds.add(id);
				}
				itemSearchService.deleteByItemIds(RemoveListIds);
			}

			return new PygResult(true,"成功");
		}catch (Exception e){
			return new PygResult(false,"失败");
		}

	}
	
}
