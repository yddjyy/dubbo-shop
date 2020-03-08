package top.ingxx.manager.controller;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import top.ingxx.pojo.TbSeller;
import top.ingxx.manager.service.SellerService;

import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.entity.PygResult;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seller")
public class SellerController {
  	Logger logger = Logger.getLogger(this.getClass());
	@Reference
	private SellerService sellerService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeller> findAll(){			
		return sellerService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return sellerService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param seller
	 * @return
	 */
	@RequestMapping("/add")
	public PygResult add(@RequestBody TbSeller seller){
		try {
			sellerService.add(seller);
			return new PygResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param seller
	 * @return
	 */
	@RequestMapping("/update")
	public PygResult update(@RequestBody TbSeller seller){
		try {
			sellerService.update(seller);
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
	public TbSeller findOne(String id) throws UnsupportedEncodingException {
		id = new String(id.getBytes("ISO-8859-1"),"UTF-8");
		logger.info(id);
		return sellerService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public PygResult delete(String [] ids){
		try {
			sellerService.delete(ids);
			return new PygResult(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param seller
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbSeller seller, int page, int rows  ){
		return sellerService.findPage(seller, page, rows);		
	}

    /**
     * 修改商家审核状态
     * @param map
     * @return
     */
	@RequestMapping("updateStatus")
	public PygResult updateStatus(@RequestBody Map<String,String>map){
        try {
            sellerService.updateStatus(map.get("sellerId"),map.get("status"));
            return new PygResult(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new PygResult(false,"修改失败");
        }
    }
}
