package top.ingxx.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import top.ingxx.mapper.TbSeckillTimeMapper;
import top.ingxx.pojo.TbSeckillTime;
import top.ingxx.pojo.TbSeckillTimeExample;
import top.ingxx.seckill.service.SeckillTimeService;
import top.ingxx.untils.entity.PageResult;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillTimeServiceImpl implements SeckillTimeService {

	@Autowired
	private TbSeckillTimeMapper seckillTimeMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillTime> findAll() {
		return seckillTimeMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillTime> page=   (Page<TbSeckillTime>) seckillTimeMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillTime tbSeckillTime) {
		seckillTimeMapper.insert(tbSeckillTime);
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillTime tbSeckillTime){
		seckillTimeMapper.updateByPrimaryKey(tbSeckillTime);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillTime findOne(int id){
		return seckillTimeMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(int[] ids) {
		for(int id:ids){
			seckillTimeMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillTime contentCategory, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillTimeExample example=new TbSeckillTimeExample();
		TbSeckillTimeExample.Criteria criteria = example.createCriteria();
		
//		if(contentCategory!=null){
//						if(contentCategory.getName()!=null && contentCategory.getName().length()>0){
//				criteria.andNameLike("%"+contentCategory.getName()+"%");
//			}
//
//		}
		
		Page<TbSeckillTime> page= (Page<TbSeckillTime>)seckillTimeMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbSeckillTime> findStartTimeOfDate(String startDate) {
		TbSeckillTimeExample tbSeckillTimeExample = new TbSeckillTimeExample();
		TbSeckillTimeExample.Criteria criteria = tbSeckillTimeExample.createCriteria();
		criteria.andStartDataEqualTo(startDate);
		List<TbSeckillTime> tbSeckillTimes = seckillTimeMapper.selectByExample(tbSeckillTimeExample);
		if(tbSeckillTimes.size()==0){
			return null;
		}
		return tbSeckillTimes;
	}

	@Override
	public List<TbSeckillTime> findStartTimeByNoStart(String startDate,String nowTime) {
		TbSeckillTimeExample tbSeckillTimeExample = new TbSeckillTimeExample();
		TbSeckillTimeExample.Criteria criteria = tbSeckillTimeExample.createCriteria();
		criteria.andStartDataEqualTo(startDate);
		criteria.andEndTimeGreaterThan(nowTime);
		List<TbSeckillTime> tbSeckillTimes = seckillTimeMapper.selectByExample(tbSeckillTimeExample);
		return tbSeckillTimes;
	}
}
