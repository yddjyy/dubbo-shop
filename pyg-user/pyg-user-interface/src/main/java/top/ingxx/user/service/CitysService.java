package top.ingxx.user.service;

import top.ingxx.pojo.TbCities;
import top.ingxx.untils.entity.PageResult;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface CitysService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbCities> findAll();


	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	*/
	public void add(TbCities tbCities);


	/**
	 * 修改
	 */
	public void update(TbCities tbCities);


	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbCities findOne(Long id);


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
	public PageResult findPage(TbCities tbCities, int pageNum, int pageSize);
	
	public List<TbCities> findListByProvinceidId(String provinceid);
	
}
