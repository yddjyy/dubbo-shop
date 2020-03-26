package top.ingxx.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import top.ingxx.cart.service.CartService;
import top.ingxx.mapper.TbItemMapper;
import top.ingxx.pojo.TbItem;
import top.ingxx.pojoGroup.Cart;
import top.ingxx.pojoGroup.CartItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		
		//1.根据skuID查询商品明细SKU的对象
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if(item==null){
			throw new RuntimeException("商品不存在");
		}
		if(!item.getStatus().equals("1")){

			throw new RuntimeException("商品状态不合法");
		}		
		//2.根据SKU对象得到商家ID
		String sellerId = item.getSellerId();//商家ID
		
		//3.根据商家ID在购物车列表中查询购物车对象
		Cart cart = searchCartBySellerId(cartList,sellerId);
		
		if(cart==null){//4.如果购物车列表中不存在该商家的购物车
			
			//4.1 创建一个新的购物车对象
			cart=new Cart();
			cart.setSellerId(sellerId);//商家ID
			cart.setSellerName(item.getSeller());//商家名称			
			List<CartItem> cartItemList=new ArrayList();//创建购物车明细列表
			CartItem cartItem = createCartItem(item,num);
			cartItemList.add(cartItem);
			cart.setCartItemList(cartItemList);
			
			//4.2将新的购物车对象添加到购物车列表中
			cartList.add(cart);
			
		}else{//5.如果购物车列表中存在该商家的购物车
			// 判断该商品是否在该购物车的明细列表中存在
			CartItem cartItem = searchOrderItemByItemId(cart.getCartItemList(),itemId);
			if(cartItem==null){
				//5.1  如果不存在  ，创建新的购物车明细对象，并添加到该购物车的明细列表中
				cartItem=createCartItem(item,num);
				cart.getCartItemList().add(cartItem);
				
			}else{
				//5.2 如果存在，在原有的数量上添加数量 ,并且更新金额	
				cartItem.setNum(cartItem.getNum()+num);//更改数量
				//金额
				cartItem.setTotalFee( new BigDecimal(cartItem.getPrice().doubleValue()*cartItem.getNum() ) );
				//当明细的数量小于等于0，移除此明细
				if(cartItem.getNum()<=0){
					cart.getCartItemList().remove(cartItem);
				}
				//当购物车的明细数量为0，在购物车列表中移除此购物车
				if(cart.getCartItemList().size()==0){
					cartList.remove(cart);
				}				
			}
			
		}
		
		return cartList;
	}
	
	/**
	 * 根据商家ID在购物车列表中查询购物车对象
	 * @param cartList
	 * @param sellerId
	 * @return
	 */
	private Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
		for(Cart cart:cartList){
			if(cart.getSellerId().equals(sellerId)){
				return cart;
			}
		}
		return null;		
	}
	
	/**
	 * 根据skuID在购物车明细列表中查询购物车明细对象
	 * @param orderItemList
	 * @param itemId
	 * @return
	 */
	public CartItem searchOrderItemByItemId(List<CartItem> orderItemList,Long itemId){
		for(CartItem cartItem:orderItemList){
			if(cartItem.getItemId().longValue()==itemId.longValue()){
				return cartItem;
			}			
		}
		return null;
	}
	
	/**
	 * 创建购物车明细对象
	 * @param item
	 * @param num
	 * @return
	 */
	private CartItem createCartItem(TbItem item,Integer num){
		//创建新的购物车明细对象
		CartItem cartItem=new CartItem();
		cartItem.setGoodsId(item.getGoodsId());
		cartItem.setItemId(item.getId());
		cartItem.setNum(num);
		cartItem.setPicPath(item.getImage());
		cartItem.setPrice(item.getPrice());
		cartItem.setSellerId(item.getSellerId());
		cartItem.setTitle(item.getTitle());
		cartItem.setTotalFee(  new BigDecimal(item.getPrice().doubleValue()*num) );
		if(item.getStatus().equals("2")||item.getStatus().equals("3")){
			//商品失效
			cartItem.setStatus("0");
		}else if(item.getNum()<cartItem.getNum()){
			//库存不足
			cartItem.setStatus("1");
		}else{
			//商品正常
			cartItem.setStatus("3");
		}
		cartItem.setSpec(item.getSpec());
		return cartItem;
	}
	
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public List<Cart> findCartListFromRedis(String username) {
		System.out.println("从redis中提取购物车"+username);
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
		if(cartList==null){
			cartList=new ArrayList();
		}		
		return cartList;
	}

	@Override
	public void saveCartListToRedis(String username, List<Cart> cartList) {
		System.out.println("向redis中存入购物车"+username);
		redisTemplate.boundHashOps("cartList").put(username, cartList);
		
	}

	@Override
	public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
		// cartList1.addAll(cartList2);  不能简单合并 		
		for(Cart cart:cartList2){
			for( CartItem cartItem :cart.getCartItemList() ){
				cartList1=addGoodsToCartList(cartList1,cartItem.getItemId(),cartItem.getNum());
			}
		}
		return cartList1;		
	}

	@Override
	public List<Cart> updateCartGoodsInfo(List<Cart> cartList) {
		for(Cart cart:cartList){
			for(CartItem cartItem:cart.getCartItemList()){
				TbItem item = itemMapper.selectByPrimaryKey(cartItem.getItemId());
				//判断商品状态
				if(item.getStatus().equals("2")||item.getStatus().equals("3")){
					//商品失效
					cartItem.setStatus("0");
				}else if(item.getNum()<cartItem.getNum()){
					//库存不足
					cartItem.setStatus("1");
				}else{
					//商品正常
					cartItem.setStatus("2");
				}
				cartItem.setSpec(item.getSpec());
			}
		}
		return cartList;
	}

	@Override
	public Boolean createOrderListAndSavetoRedis(String username,Long[] selectIds) {
		//从redis中获取购物车列表
		List<Cart> cartList_redis = findCartListFromRedis(username);

		//查找要付款的商品，存入orderList中并存入缓存
		List<Cart> orderList = new ArrayList<>();
		for(Long id:selectIds){
			for(Cart cart:cartList_redis){
				for(int i=0;i<cart.getCartItemList().size();i++){
					if(cart.getCartItemList().get(i).getItemId().equals(id)){
						orderList=addGoodsToCartList(orderList,cart.getCartItemList().get(i).getItemId(),cart.getCartItemList().get(i).getNum());
					}
				}
			}
		}
		redisTemplate.boundHashOps("orderList").put(username, orderList);
		return true;
	}


	@Override
	public List findOrderList(String username) {
		List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps("orderList").get(username);

		return cartList;
	}
}
