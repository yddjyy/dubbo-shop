package top.ingxx.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import top.ingxx.entity.PageResult;
import top.ingxx.mapper.TbSeckillGoodsMapper;
import top.ingxx.pojo.TbSeckillGoods;
import top.ingxx.pojo.TbSeckillGoodsExample;
import top.ingxx.pojo.TbSeckillGoodsExample.Criteria;
import top.ingxx.pojo.TbSeckillTime;
import top.ingxx.seckill.service.SeckillGoodsService;
import top.ingxx.untils.entity.PygResult;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillGoods> findAll() {
		return seckillGoodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillGoods> page=   (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillGoods seckillGoods) {
		seckillGoodsMapper.insert(seckillGoods);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillGoods seckillGoods){
		seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillGoods findOne(Long id){
		return seckillGoodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillGoodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillGoodsExample example=new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillGoods!=null){			
						if(seckillGoods.getTitle()!=null && seckillGoods.getTitle().length()>0){
				criteria.andTitleLike("%"+seckillGoods.getTitle()+"%");
			}
			if(seckillGoods.getSmallPic()!=null && seckillGoods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+seckillGoods.getSmallPic()+"%");
			}
			if(seckillGoods.getSellerId()!=null && seckillGoods.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillGoods.getSellerId()+"%");
			}
			if(seckillGoods.getStatus()!=null && seckillGoods.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillGoods.getStatus()+"%");
			}
			if(seckillGoods.getIntroduction()!=null && seckillGoods.getIntroduction().length()>0){
				criteria.andIntroductionLike("%"+seckillGoods.getIntroduction()+"%");
			}
	
		}
		
		Page<TbSeckillGoods> page= (Page<TbSeckillGoods>)seckillGoodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
		
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public List<TbSeckillGoods> findList(TbSeckillTime tbSeckillTime) {
		
//		Map dayMap = (Map) redisTemplate.boundHashOps("seckillGoods").get(seckillGoods.getStartDate());
//
//		Map timeMap = (Map) dayMap.get(seckillGoods.getStartTime());
		TbSeckillGoodsExample example=new TbSeckillGoodsExample();
			Criteria criteria = example.createCriteria();
			criteria.andStatusEqualTo("1");// 审核通过的商品
			criteria.andStockCountGreaterThan(0);//库存数大于0
			criteria.andStartDateEqualTo(tbSeckillTime.getStartData());
		    criteria.andStartTimeEqualTo(tbSeckillTime.getStartTime());
		List<TbSeckillGoods> tbSeckillGoods = seckillGoodsMapper.selectByExample(example);
//		if(seckillGoodsList==null || seckillGoodsList.size()==0){
//			TbSeckillGoodsExample example=new TbSeckillGoodsExample();
//			Criteria criteria = example.createCriteria();
//			criteria.andStatusEqualTo("1");// 审核通过的商品
//			criteria.andStockCountGreaterThan(0);//库存数大于0
//			criteria.andStartTimeLessThanOrEqualTo(new Date()+"");//开始日期小于等于当前日期
//			criteria.andEndTimeGreaterThanOrEqualTo(new Date()+"");//截止日期大于等于当前日期
//			seckillGoodsList = seckillGoodsMapper.selectByExample(example);
//			//将列表数据装入缓存
//			for(TbSeckillGoods seckillGoods:seckillGoodsList){
//				redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
//			}
//			System.out.println("从数据库中读取数据装入缓存");
//		}else{
//			System.out.println("从缓存中读取数据");
//
//		}
//		return seckillGoodsList;


		return tbSeckillGoods;
		
	}

	@Override
	public TbSeckillGoods findOneFromRedis(String startDate,String startTime,Long id) {
		Map<String ,Map> dayMap = (Map) redisTemplate.boundHashOps("seckillGoods").get(startDate);
		System.out.println(dayMap.size()+"================");
		Map<String,TbSeckillGoods> map = dayMap.get(startTime);
		System.out.println(map.size()+"---------------------------");
		for (String key : map.keySet()) {
			System.out.println(key);
		}
		for (TbSeckillGoods value : map.values()) {
			System.out.println(value.getStatus());
		}
		TbSeckillGoods goods = map.get(id);
		System.out.println(goods==null);
		System.out.println(goods.getTitle()+"----===----");
		return map.get(id);
	}

	@Override
	public PygResult removeGoodsFromRedis(Long goodsId) {
		redisTemplate.boundHashOps("seckillGoods").delete(goodsId);
		return null;
	}

}
