package top.ingxx.seckill.service;

import top.ingxx.pojo.TbSeckillTime;
import top.ingxx.untils.entity.PageResult;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillTimeService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillTime> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSeckillTime contentCategory);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeckillTime contentCategory);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillTime findOne(int id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(int[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillTime contentCategory, int pageNum, int pageSize);

	/**
	 * 根据开始日期查询开始时间
	 */
	public List<TbSeckillTime> findStartTimeOfDate(String startDate);


	/*
		根据当前时间查取正在秒杀和未秒杀的事件段
	 */
	public List<TbSeckillTime> findStartTimeByNoStart(String startDate,String nowTime);
}
