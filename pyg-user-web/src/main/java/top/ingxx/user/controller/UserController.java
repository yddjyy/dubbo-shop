package top.ingxx.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;
import top.ingxx.pojo.TbUser;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.entity.PygResult;
import top.ingxx.user.service.UserService;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {


	@Reference
	private UserService userService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return userService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public PygResult add(@RequestBody TbUser user, String smscode){

		try {
			MessageDigest md5=MessageDigest.getInstance("MD5");
			BASE64Encoder base64en = new BASE64Encoder();
			String psw=base64en.encode(md5.digest(user.getPassword().getBytes("utf-8")));
			user.setPassword(psw);
			user.setCreated(new Date());
			user.setUpdated(new Date());
			userService.add(user);
			return new PygResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public PygResult update(@RequestBody TbUser user){
		try {
			user.setUpdated(new Date());
			userService.update(user);
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
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public PygResult delete(Long [] ids){
		try {
			userService.delete(ids);
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
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}

	/**
	 * 通过用户名查找用户信息
	 * @param username
	 * @return
	 */
	@RequestMapping("/findUserByUserName")
	public Map findUserByUserName(String username){
		Map map = new HashMap();
		//如果接受的用户名为空
		if(username.trim().equals("")||username.equals(" ")){
			map.put("flag", false);
		}
		TbUser userInfo = userService.findUserByUserName(username);
		//如果没有查找到相关的用户信息
		if(userInfo==null){
			map.put("flag", false);
		}else{
			map.put("flag", true);
		}
		return map;
	}

	@RequestMapping("/changePwd")
	@Transactional
	public PygResult changePwd( String oldPwd, String newPwd){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		//定义一个字节数组

		try {
			byte[] secretBytes = null;
			MessageDigest md=MessageDigest.getInstance("MD5");
			md.update(oldPwd.getBytes());
			//获得加密后的数据
			secretBytes = md.digest();
			//将加密后的数据转换为16进制数字
			String psw = new BigInteger(1, secretBytes).toString(16);
			// 如果生成数字未满32位，需要前面补0
			for (int i = 0; i < 32 - psw.length(); i++) {
				psw = "0" + psw;
			}
			TbUser user = userService.findUserByUserNameAndPwd(username, psw);
			if(user==null){
				return new PygResult(false,"原密码错误");
			}else{
				byte[] secretByte = null;
				MessageDigest md5=MessageDigest.getInstance("MD5");
				md5.update(newPwd.getBytes());
				//获得加密后的数据
				secretByte = md5.digest();
				//将加密后的数据转换为16进制数字
				String newPassword = new BigInteger(1, secretByte).toString(16);
				// 如果生成数字未满32位，需要前面补0
				for (int i = 0; i < 32 - newPassword.length(); i++) {
					newPassword = "0" + newPassword;
				}
				user.setPassword(newPassword);
				user.setUpdated(new Date());
				userService.update(user);
				return new PygResult(true,"密码修改成功");
			}
		}catch (Exception e){
			throw new RuntimeException();
		}
	}

}
