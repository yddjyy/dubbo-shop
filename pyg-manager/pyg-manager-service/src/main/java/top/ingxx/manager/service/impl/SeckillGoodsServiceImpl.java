package top.ingxx.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import top.ingxx.manager.service.SeckillGoodsService;
import top.ingxx.mapper.TbItemMapper;
import top.ingxx.mapper.TbSeckillGoodsMapper;
import top.ingxx.pojo.TbItem;
import top.ingxx.pojo.TbItemExample;
import top.ingxx.pojo.TbSeckillGoods;
import top.ingxx.pojo.TbSeckillGoodsExample;
import top.ingxx.untils.entity.PageResult;

import java.util.*;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;

	@Autowired
	private TbItemMapper tbItemMapper;
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
		//通过秒杀商品中的goodsid获取商品item信息
		TbItemExample tbItemExample = new TbItemExample();
		TbItemExample.Criteria criteria = tbItemExample.createCriteria();
		criteria.andGoodsIdEqualTo(seckillGoods.getGoodsId());
		List<TbItem> tbItems = tbItemMapper.selectByExample(tbItemExample);
		if(tbItems.size()!=0){
			TbItem tbItem = tbItems.get(0);
			seckillGoods.setCreateTime(new Date());
			seckillGoods.setItemId(tbItem.getId());
			seckillGoods.setPrice(tbItem.getPrice());
			seckillGoods.setTitle(tbItem.getTitle());
			seckillGoods.setSmallPic(tbItem.getImage());
			seckillGoods.setSellerId(tbItem.getSellerId());
			seckillGoods.setStatus("0");
			seckillGoods.setStockCount(seckillGoods.getNum());
			seckillGoodsMapper.insert(seckillGoods);
			tbItem.setNum(tbItem.getNum()-seckillGoods.getNum());
			tbItemMapper.updateByPrimaryKey(tbItem);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillGoods seckillGoods){
		seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public void updateStatus(Long[] ids, String status) {
		System.out.println("通过审核=========================="+status);
		for(Long id :ids){
			if("1".equals(status)){//如审核通过
				System.out.println("通过审核==========================");
				TbSeckillGoods tbSeckillGoods = seckillGoodsMapper.selectByPrimaryKey(id);
				tbSeckillGoods.setStatus(status);
				Map seckillGoodsMap = (Map) redisTemplate.boundHashOps("seckillGoods").get(tbSeckillGoods.getStartDate());
				if(seckillGoodsMap==null || seckillGoodsMap.size()==0){//不存在该天的秒杀目录
					HashMap<String,TbSeckillGoods> seckillGoodsList = new HashMap<>();
					seckillGoodsList.put(tbSeckillGoods.getId()+"",tbSeckillGoods);
					HashMap<String, Map> seckillGoodsDayMap = new HashMap<>();
					seckillGoodsDayMap.put(tbSeckillGoods.getStartTime(),seckillGoodsList);//格式： 7:00 ，list<TbSeckillGood>
					redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getStartDate(),seckillGoodsDayMap);
				}else{//存在当天的秒杀目录
					Map<String,TbSeckillGoods> seckillTime = (Map) seckillGoodsMap.get(tbSeckillGoods.getStartTime());
					if(seckillTime==null||seckillTime.size()==0)//不存在某一刻的秒杀目录
					{
						HashMap<String,TbSeckillGoods> seckillGoodsList = new HashMap<>();
						seckillGoodsList.put(tbSeckillGoods.getId()+"",tbSeckillGoods);
						seckillGoodsMap.put(tbSeckillGoods.getStartTime(),seckillGoodsList);
						redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getStartDate(),seckillGoodsMap);
					}else{//存在某刻秒杀
						int flag=0;
						if(seckillTime.get(tbSeckillGoods.getId()+"")!=null)//存在当前商品
						{
							TbSeckillGoods seckillGoods = seckillTime.get(tbSeckillGoods.getId()+"");
							seckillGoods.setNum(seckillGoods.getNum()+tbSeckillGoods.getNum());
							seckillGoods.setStockCount(seckillGoods.getStockCount()+tbSeckillGoods.getStockCount());
							seckillTime.put(tbSeckillGoods.getId()+"",seckillGoods);
						}
						else{//不存在同一商品。
							seckillTime.put(tbSeckillGoods.getId()+"",tbSeckillGoods);
						}
						redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getStartDate(),seckillTime);
					}
				}
				seckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods);
			}else{
				TbSeckillGoods tbSeckillGoods = seckillGoodsMapper.selectByPrimaryKey(id);
				tbSeckillGoods.setStatus(status);
				seckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods);
				if(status.equals("3")){//下架该秒杀商品
					Map seckillGoodsMap = (Map) redisTemplate.boundHashOps("seckillGoods").get(tbSeckillGoods.getStartDate());
					Map<String,TbSeckillGoods>  seckillTime= (Map<String,TbSeckillGoods>) seckillGoodsMap.get(tbSeckillGoods.getStartTime());
					seckillTime.remove(tbSeckillGoods.getId()+"");//移除id
					seckillGoodsMap.put(tbSeckillGoods.getStartTime(),seckillTime);
					redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getStartDate(),seckillGoodsMap);
				}

			}
		}
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
		TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
		
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

    @Override
    public void addSeckillGoodsList(List<TbSeckillGoods> seckillGoodsList) {
        for(TbSeckillGoods seckillGoods:seckillGoodsList){
            seckillGoods.setStatus("0");
            seckillGoodsMapper.insert(seckillGoods);
        }
    }

}
