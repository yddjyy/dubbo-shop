package top.ingxx.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import top.ingxx.cart.service.CartService;
import top.ingxx.pojoGroup.Cart;
import top.ingxx.untils.entity.PygResult;
import top.ingxx.untils.until.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference(timeout=60000)
	private CartService cartService;

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;

	@RequestMapping("/findCartList")
	public List<Cart> findCartList() {
		//当前登录人账号
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
		if (cartListString == null || cartListString.equals("")) {
			cartListString = "[]";
		}
		List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);

		//更新购物车中商品信息
		List<Cart> cartList_cookieNew = cartService.updateCartGoodsInfo(cartList_cookie);

		if (username.equals("anonymousUser")) {//如果未登录
			//从cookie中提取购物车
			return cartList_cookieNew;

		} else {//如果已登录
			//获取redis购物车
			List<Cart> cartList_redis = cartService.findCartListFromRedis(username);

			List<Cart> cartsList_redisNew  = cartService.updateCartGoodsInfo(cartList_redis);//更新redis中的购物车
			if (cartList_cookie.size() > 0) {//判断当本地购物车中存在数据
				//得到合并后的购物车
				List<Cart> cartList = cartService.mergeCartList(cartList_cookieNew, cartsList_redisNew);
				//将合并后的购物车存入redis
				cartService.saveCartListToRedis(username, cartList);
				//本地购物车清除
				CookieUtil.deleteCookie(request, response, "cartList");
				return cartList;
			}
			return cartList_redis;
		}

	}

	@RequestMapping(value = "/addGoodsToCartList",method = RequestMethod.GET)
	@CrossOrigin
	public PygResult addGoodsToCartList(Long itemId, Integer num) {
		//当前登录人账号
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			//提取购物车
			List<Cart> cartList = findCartList();
			//调用服务方法操作购物车
			cartList = cartService.addGoodsToCartList(cartList, itemId, num);
			if (name.equals("anonymousUser")) {//如果未登录
				//将新的购物车存入cookie
				String cartListString = JSON.toJSONString(cartList);
				CookieUtil.setCookie(request, response, "cartList", cartListString, 3600 * 24, "UTF-8");
			} else {//如果登录
				cartService.saveCartListToRedis(name, cartList);
			}
			return new PygResult(true, "存入购物车成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "存入购物车失败");
		}


	}
	//点击立刻购买后创建订单，存入缓存
	@RequestMapping("/createOrderRedis")
	public PygResult createOrderRedis(@RequestBody Long[] selectIds){
		if(selectIds.length==0){
			return new PygResult(true, "没有选中任何商品");
		}
		//当前登录人账号
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		cartService.createOrderListAndSavetoRedis(username ,selectIds);

		return  new PygResult(true, "已提出订单");
	}

	//从redis中获取最新的订单项
	@RequestMapping("/findOrderList")
	public List findOrderList(){
		//当前登录人账号
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return cartService.findOrderList(username);//获取最新的
	}
}