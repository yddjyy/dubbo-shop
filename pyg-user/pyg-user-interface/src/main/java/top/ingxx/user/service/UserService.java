package top.ingxx.user.service;
import top.ingxx.pojo.TbUser;
import top.ingxx.untils.entity.PageResult;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbUser> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbUser user);
	
	
	/**
	 * 修改
	 */
	public void update(TbUser user);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbUser findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbUser user, int pageNum,int pageSize);


	/**
	 * 通过用户名查找当前用户的信息
	 * @param username
	 * @return
	 */
	public TbUser findUserByUserName(String username);

	/**
	 * 通过用户名和加密后的密码查找用户信息
	 * @param username
	 * @param pwd
	 * @return
	 */
	public TbUser findUserByUserNameAndPwd(String username,String pwd);
}
