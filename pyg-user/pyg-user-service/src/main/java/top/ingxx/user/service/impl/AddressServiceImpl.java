package top.ingxx.user.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import top.ingxx.mapper.TbAddressMapper;
import top.ingxx.pojo.TbAddress;
import top.ingxx.pojo.TbAddressExample;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.user.service.AddressService;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private TbAddressMapper addressMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbAddress> findAll() {
		return addressMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbAddress> page=   (Page<TbAddress>) addressMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbAddress address) {
		addressMapper.insert(address);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbAddress address){
		addressMapper.updateByPrimaryKey(address);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbAddress findOne(Long id){
		return addressMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			addressMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbAddress address, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbAddressExample example=new TbAddressExample();
		TbAddressExample.Criteria criteria = example.createCriteria();
		
		if(address!=null){			
						if(address.getUserId()!=null && address.getUserId().length()>0){
				criteria.andUserIdLike("%"+address.getUserId()+"%");
			}
			if(address.getProvinceId()!=null && address.getProvinceId().length()>0){
				criteria.andProvinceIdLike("%"+address.getProvinceId()+"%");
			}
			if(address.getCityId()!=null && address.getCityId().length()>0){
				criteria.andCityIdLike("%"+address.getCityId()+"%");
			}
			if(address.getTownId()!=null && address.getTownId().length()>0){
				criteria.andTownIdLike("%"+address.getTownId()+"%");
			}
			if(address.getMobile()!=null && address.getMobile().length()>0){
				criteria.andMobileLike("%"+address.getMobile()+"%");
			}
			if(address.getAddress()!=null && address.getAddress().length()>0){
				criteria.andAddressLike("%"+address.getAddress()+"%");
			}
			if(address.getContact()!=null && address.getContact().length()>0){
				criteria.andContactLike("%"+address.getContact()+"%");
			}
			if(address.getIsDefault()!=null && address.getIsDefault().length()>0){
				criteria.andIsDefaultLike("%"+address.getIsDefault()+"%");
			}
			if(address.getNotes()!=null && address.getNotes().length()>0){
				criteria.andNotesLike("%"+address.getNotes()+"%");
			}
			if(address.getAlias()!=null && address.getAlias().length()>0){
				criteria.andAliasLike("%"+address.getAlias()+"%");
			}
	
		}
		
		Page<TbAddress> page= (Page<TbAddress>)addressMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbAddress> findListByUserId(String userId) {
		
		TbAddressExample example=new TbAddressExample();
		TbAddressExample.Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		return addressMapper.selectByExample(example);
	}

	//更改默认地址
	@Transactional
	@Override
	public Boolean updateDefaultAddress(String preAddressId, String nowAddressId) {
		try {
			//原来设置了默认值
			if(!preAddressId.equals("0")){
				//获取原来的默认地址
				TbAddress preTbAddress = findOne(Long.parseLong(preAddressId));
				preTbAddress.setIsDefault("0");//更改原来的默认地址为非默认
				update(preTbAddress);
			}
			//获取要设置的默认地址
			TbAddress nowTbAddress = findOne(Long.parseLong(nowAddressId));
			nowTbAddress.setIsDefault("1");//新的默认地址
			update(nowTbAddress);
			return true;
		}catch (Exception e){
			 throw new RuntimeException();
		}
	}
}
