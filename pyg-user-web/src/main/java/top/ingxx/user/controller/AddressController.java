package top.ingxx.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.pojo.TbAddress;
import top.ingxx.pojo.TbAreas;
import top.ingxx.pojo.TbCities;
import top.ingxx.pojo.TbProvinces;
import top.ingxx.pojoGroup.Address;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.entity.PygResult;
import top.ingxx.user.service.AddressService;
import top.ingxx.user.service.AreasService;
import top.ingxx.user.service.CitysService;
import top.ingxx.user.service.ProvincesService;

import java.util.ArrayList;
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
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			address.setUserId(username);
			address.setIsDefault("0");
			address.setCreateDate(new Date());
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
	public List<Address> findListByLoginUser(){
		//获取登陆用户
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		List<TbAddress> AddressList = addressService.findListByUserId(username);
		ArrayList<Address> addresses = new ArrayList<>();
		for(TbAddress tbAddress :AddressList){
			Address address = new Address();
			address.setAddress(tbAddress);
			TbProvinces tbProvinces = provincesService.findProvinceidId(tbAddress.getProvinceId());
			address.setProvince(tbProvinces.getProvince());
			TbCities tbCities = citysService.findCityByCityId(Long.parseLong(tbAddress.getCityId()));
			address.setCity(tbCities.getCity());
			TbAreas tbAreas = areasService.findAreasByAreasId(Long.parseLong(tbAddress.getTownId()));
			address.setTown(tbAreas.getArea());
			addresses.add(address);
		}
		return addresses;
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

	/**
	 *
	 * @param preAddressId 原来默认的地址id
	 * @param nowAddressId 新的默认地址的id
	 * @return
	 */
	@RequestMapping("/updateDefaultAddress")
	public PygResult updateDefaultAddress(String preAddressId,String nowAddressId){

		Boolean flag = addressService.updateDefaultAddress(preAddressId, nowAddressId);
		if(flag){
			return new PygResult(true,"修改成功");
		}
		return new PygResult(false,"修改失败");
	}

	/**
	 * 根据Addresid删除地址信息
	 * @param addressId
	 * @return
	 */
	@RequestMapping("/deleteAddressById")
	public PygResult deleteAddressById(Long addressId){
		Long[] ids= new Long[]{addressId};
		addressService.delete(ids);
		return new PygResult(true,"删除成功");
	}

	/**
	 * 根据id查找对应的省市县(区）
	 * @param //id 省1 市2 县3
	 * @param //flag
	 * @return
	 */
//	@RequestMapping("/findAddressInfoById")
//	public String findAddressInfoById(Long id,int flag){
//		if(id==null){
//			return null;
//		}
//		if(flag==1){
//			TbProvinces province = provincesService.findProvinceidId(id.toString());
//			if(province==null){
//				return null;
//			}
//			System.out.println(province.getProvince()+"=============================");
//			return province.getProvince();
//		}
//		if(flag==2){
//			TbCities tbCities = citysService.findCityByCityId(id);
//			System.out.println(tbCities.getCity()+"===================================");
//			if(tbCities==null){
//				return null;
//			}
//			return tbCities.getCity();
//		}
//		if(flag==3){
//			TbAreas tbAreas = areasService.findAreasByAreasId(id);
//			System.out.println(tbAreas.getArea()+"==================================================");
//			if(tbAreas==null){
//				return null;
//			}
//			return tbAreas.getArea();
//		}
//		return null;
//	}

	//查询所有省份
	@RequestMapping("/selectProvinceList")
	public List<TbProvinces> selectProvinceList(){
		return  provincesService.findAll();
	}

	//通过省份查找市
	@RequestMapping("/findByProvinceId")
	public List<TbCities> findByProvinceId(Long provinceId){
		return citysService.findListByProvinceidId(provinceId.toString());
	}

	/**
	 * 通过市查找县区
	 * @param cityId
	 * @return
	 */
	@RequestMapping("/findByCityId")
	public List<TbAreas> findByCityId(Long cityId){
		return areasService.findListByCitiesId(cityId.toString());
	}
}
