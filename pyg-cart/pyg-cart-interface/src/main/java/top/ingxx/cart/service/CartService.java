package top.ingxx.cart.service;

import top.ingxx.pojoGroup.Cart;

import java.util.List;

/**
 * 购物车服务接口
 * @author Administrator
 *
 */
public interface CartService {

	/**
	 * 添加商品到购物车
	 * @param
	 * @param itemId
	 * @param num
	 * @return
	 */
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);
	
	/**
	 * 从redis中提取购物车列表
	 * @param username
	 * @return
	 */
	public List<Cart> findCartListFromRedis(String username);
	
	/**
	 * 将购物车列表存入redis
	 * @param username
	 * @param cartList
	 */
	public void saveCartListToRedis(String username,List<Cart> cartList);
	
	/**
	 * 合并购物车
	 * @param cartList1
	 * @param cartList2
	 * @return
	 */
	public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);

	/**
	 * 更新购物车中商品信息
	 * @param cartList
	 * @return
	 */
	public List<Cart> updateCartGoodsInfo(List<Cart> cartList);

	/**
	 * 从购车列表中删除选中的商品存入orderList中
	 * @param username
	 * @param selectIds
	 * @return
	 */
	public Boolean createOrderListAndSavetoRedis(String username,Long[] selectIds);

	/**
	 * 从redis中获取订单列表(1,表示获取最新的，2表示获取全部
	 * @param username
	 * @return
	 */
	public List findOrderList(String username);
}
