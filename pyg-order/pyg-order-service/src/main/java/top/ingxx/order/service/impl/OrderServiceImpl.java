package top.ingxx.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import top.ingxx.mapper.TbItemMapper;
import top.ingxx.mapper.TbOrderItemMapper;
import top.ingxx.mapper.TbOrderMapper;
import top.ingxx.mapper.TbPayLogMapper;
import top.ingxx.order.service.OrderService;
import top.ingxx.pojo.*;
import top.ingxx.pojoGroup.Cart;
import top.ingxx.pojoGroup.CartItem;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.until.IdWorker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	
	@Autowired
	private TbPayLogMapper payLogMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private IdWorker idWorker;
	
	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private TbItemMapper tbItemMapper;
	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		
		//1.从redis中提取欲购买的订单车列表
		List<Cart> orderCartList= (List<Cart>) redisTemplate.boundHashOps("orderList").get(order.getUserId());
		if(orderCartList.size()<=0){
			return;
		}
		System.out.println("订单列表："+orderCartList.size());

		List<String> orderIdList=new ArrayList();//订单ID集合
		double total_money=0;//总金额
		//2.循环购物车列表添加订单
		for(Cart cart :orderCartList) {
			TbOrder tbOrder = new TbOrder();
			long orderId = idWorker.nextId();    //获取ID
			tbOrder.setOrderId(orderId);
			tbOrder.setPaymentType(order.getPaymentType());//支付类型
			tbOrder.setStatus("1");//未付款 
			tbOrder.setCreateTime(new Date());//下单时间
			tbOrder.setUpdateTime(new Date());//更新时间
			tbOrder.setUserId(order.getUserId());//当前用户
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());//收货人地址
			tbOrder.setReceiverMobile(order.getReceiverMobile());//收货人电话
			tbOrder.setReceiver(order.getReceiver());//收货人
			tbOrder.setSourceType(order.getSourceType());//订单来源
			tbOrder.setSellerId(cart.getSellerId());//商家ID

			double money = 0;//合计数
			//循环购物车中每条明细记录
			for (CartItem cartItem : cart.getCartItemList()) {
				TbOrderItem orderItem = new TbOrderItem();
				orderItem.setId(idWorker.nextId());//主键
				orderItem.setOrderId(orderId);//订单编号
				orderItem.setGoodsId(cartItem.getGoodsId());
				orderItem.setItemId(cartItem.getItemId());
				orderItem.setNum(cartItem.getNum());
				orderItem.setPicPath(cartItem.getPicPath());
				orderItem.setPrice(cartItem.getPrice());
				orderItem.setSellerId(cartItem.getSellerId());
				orderItem.setTitle(cartItem.getTitle());
				orderItem.setTotalFee(cartItem.getTotalFee());
				orderItemMapper.insert(orderItem);
				money += orderItem.getTotalFee().doubleValue();
			}

			tbOrder.setPayment(new BigDecimal(money));//合计


			orderMapper.insert(tbOrder);

			orderIdList.add(orderId + "");
			total_money += money;
		}
		//添加支付日志
		if("1".equals(order.getPaymentType())){
			TbPayLog payLog=new TbPayLog();
			
			payLog.setOutTradeNo(idWorker.nextId()+"");//支付订单号
			payLog.setCreateTime(new Date());
			payLog.setUserId(order.getUserId());//用户ID
			payLog.setOrderList(orderIdList.toString().replace("[", "").replace("]", ""));//订单ID串
			payLog.setTotalFee( (long)( total_money ) );//金额（元）
			payLog.setTradeState("0");//交易状态
			payLog.setPayType("1");//微信
			payLogMapper.insert(payLog);
			List<TbPayLog> payLogList = (List<TbPayLog>)redisTemplate.boundHashOps("payLog").get(order.getUserId());
			if(payLogList==null){
				payLogList=new ArrayList<TbPayLog>();
			}
			payLogList.add(payLog);
			redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLogList);//放入缓存
		}
		//从购物车中清除预购买的商品
		List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps("cartList").get(order.getUserId());

		redisTemplate.boundHashOps("cartList").put(order.getUserId(),deleteGoodsFromCartList(cartList,orderCartList) );//放入缓存
		//3.清除redis中的购物车
		redisTemplate.boundHashOps("orderList").delete(order.getUserId());
	}

	public List<Cart> deleteGoodsFromCartList(List<Cart>  cartList,List<Cart> orderCartList){
		for(Cart orderCart : orderCartList){
			for(CartItem orderCartItem : orderCart.getCartItemList()){
				for(Cart cart:cartList){
					if(cart.getSellerName().equals(orderCart.getSellerName())){
						for(int index=0;index<cart.getCartItemList().size();index++){
							if(orderCartItem.getItemId().equals(cart.getCartItemList().get(index))){
								cart.getCartItemList().remove(index);
							}
						}
					}
				}
			}
		}

		return null;
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		TbOrderExample.Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public TbPayLog searchPayLogFromRedis(String userId) {
		List<TbPayLog> payLogList = (List<TbPayLog>)redisTemplate.boundHashOps("payLog").get(userId);
		if(payLogList.size()==0){
			return null;
		}
		return payLogList.get(payLogList.size()-1);
	}

	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		//1.修改支付日志的状态及相关字段
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		payLog.setPayTime(new Date());//支付时间
		payLog.setTradeState("1");//交易成功
		payLog.setTransactionId(transaction_id);//微信的交易流水号
		
		payLogMapper.updateByPrimaryKey(payLog);//修改
		//2.修改订单表的状态
		String orderList = payLog.getOrderList();// 订单ID 串
		String[] orderIds = orderList.split(",");
		
		for(String orderId:orderIds){
			TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
			order.setStatus("2");//已付款状态
			order.setPaymentTime(new Date());//支付时间
			orderMapper.updateByPrimaryKey(order);
			//根据orderid修改对应商品的库存
			updateGoodsStockByOrderId(orderId);
		}
		
		//3.清除缓存中的payLog
		List<TbPayLog> payLogList =(List<TbPayLog>)redisTemplate.boundHashOps("payLog").get(payLog.getUserId());
		payLogList.remove(payLogList.size()-1);//移除最新的一项
		redisTemplate.boundHashOps("orderList").delete(payLog.getUserId());//移除选中列表
		redisTemplate.boundHashOps("payLog").put(payLog.getUserId(),payLogList);
	}
	//根据orderid修改对应商品的库存
	public void updateGoodsStockByOrderId(String orderId){
		//获取订单项
		TbOrderItemExample tbOrderItemExample = new TbOrderItemExample();
		TbOrderItemExample.Criteria criteria = tbOrderItemExample.createCriteria();
		criteria.andOrderIdEqualTo(Long.parseLong(orderId));
		List<TbOrderItem> tbOrderItems = orderItemMapper.selectByExample(tbOrderItemExample);
		//根据itemid获取对应的sku商品
		for(TbOrderItem tbOrderItem :tbOrderItems){
			TbItem tbItem = tbItemMapper.selectByPrimaryKey(tbOrderItem.getItemId());
			tbItem.setNum(tbItem.getNum()-tbOrderItem.getNum());
			tbItemMapper.updateByPrimaryKey(tbItem);
		}
	}
}
