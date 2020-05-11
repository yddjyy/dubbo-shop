package top.ingxx.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.manager.service.SeckillTimeService;
import top.ingxx.pojo.TbSeckillTime;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.entity.PygResult;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seckilltime")
public class SeckillTimeController {

	@Reference
	private SeckillTimeService seckillTimeService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeckillTime> findAll(){
		return seckillTimeService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return seckillTimeService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param
	 * @return
	 */
	@RequestMapping("/add")
	public PygResult add(@RequestBody TbSeckillTime tbSeckillTime){
		try {
			System.out.println(tbSeckillTime.getEndData()+":"+tbSeckillTime.getInfo()+"========================");
			seckillTimeService.add(tbSeckillTime);
			return new PygResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param contentCategory
	 * @return
	 */
	@RequestMapping("/update")
	public PygResult update(@RequestBody TbSeckillTime contentCategory){
		try {
			seckillTimeService.update(contentCategory);
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
	public TbSeckillTime findOne(int id){
		return seckillTimeService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public PygResult delete(int [] ids){
		try {
			seckillTimeService.delete(ids);
			return new PygResult(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param contentCategory
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbSeckillTime contentCategory, int page, int rows  ){
		return seckillTimeService.findPage(contentCategory, page, rows);		
	}
	
}
