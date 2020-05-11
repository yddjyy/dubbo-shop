package top.ingxx.seckill.service;

import top.ingxx.entity.PageResult;
import top.ingxx.pojo.TbSeckillGoods;
import top.ingxx.pojo.TbSeckillTime;
import top.ingxx.untils.entity.PygResult;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillGoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSeckillGoods seckillGoods);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeckillGoods seckillGoods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillGoods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize);
	
	/**
	 * 返回正在参与秒杀的商品
	 * @return
	 */
	public List<TbSeckillGoods> findList(TbSeckillTime tbSeckillTime);
	
	
	/**
	 * 根据ID获取实体(从缓存中读取)
	 * @param id
	 * @return
	 */
	public TbSeckillGoods findOneFromRedis(String startDate,String startTime,Long id);
	/**
	 * 当秒杀时间结束时从缓存中移除该商品
	 */
	public PygResult removeGoodsFromRedis(Long goodsid);
}
