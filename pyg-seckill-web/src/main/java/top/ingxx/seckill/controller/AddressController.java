package top.ingxx.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.pojo.TbAddress;
import top.ingxx.pojo.TbAreas;
import top.ingxx.pojo.TbCities;
import top.ingxx.pojo.TbProvinces;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.entity.PygResult;
import top.ingxx.user.service.AddressService;
import top.ingxx.user.service.AreasService;
import top.ingxx.user.service.CitysService;
import top.ingxx.user.service.ProvincesService;

import java.util.Date;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/address")
public class AddressController {

	@Reference
	private AddressService addressService;

	@Reference
	private ProvincesService provincesService;

	@Reference
	private CitysService citysService;
	@Reference
	private AreasService areasService;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbAddress> findAll(){
		return addressService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return addressService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param address
	 * @return
	 */
	@RequestMapping("/add")
	public PygResult add(@RequestBody TbAddress address){
		try {
			address.setIsDefault("0");//设置非默认
			address.setCreateDate(new Date());//设置添加时间
			address.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());//设置用于id
			addressService.add(address);

			return new PygResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param address
	 * @return
	 */
	@RequestMapping("/update")
	public PygResult update(@RequestBody TbAddress address){
		try {
			addressService.update(address);
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
	public TbAddress findOne(Long id){
		return addressService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public PygResult delete(Long [] ids){
		try {
			addressService.delete(ids);
			return new PygResult(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbAddress address, int page, int rows  ){
		return addressService.findPage(address, page, rows);		
	}
	
	@RequestMapping("/findListByLoginUser")
	public List<TbAddress> findListByLoginUser(){
		//获取登陆用户
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return addressService.findListByUserId(username);		
	}

	//获取所有省的列表
	@RequestMapping("/findAllProvince")
	public List<TbProvinces> findAllProvince(){
		return provincesService.findAll();
	}

	//根据省的id查找城市
	@RequestMapping("/findListByProvinceidId")
	public List<TbCities> findListByProvinceidId(String provinceid){
       return citysService.findListByProvinceidId(provinceid);
	}
	@RequestMapping("/findAllCity")
	public List<TbCities> findAllCity(){
		return citysService.findAll();
	}
	//根据城市id查询区
	@RequestMapping("/findListByCitiesId")
	public List<TbAreas> findListByCitiesId(String citesid){
		return areasService.findListByCitiesId(citesid);
	}
	@RequestMapping("/findAllTown")
	public List<TbAreas> findAllTown(){
		return areasService.findAll();
	}
}
