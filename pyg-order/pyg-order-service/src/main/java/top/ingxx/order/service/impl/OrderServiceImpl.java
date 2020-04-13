package top.ingxx.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import top.ingxx.mapper.*;
import top.ingxx.order.service.OrderService;
import top.ingxx.pojo.*;
import top.ingxx.pojoGroup.*;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.until.IdWorker;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
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

	@Autowired
	private TbOrderMapper tbOrderMapper;

	@Autowired
	private TbRefundMapper tbRefundMapper;
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
			payLog.setTotalFee(new BigDecimal(total_money));//金额（元）
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

		List<Cart> cartList_new = deleteGoodsFromCartList(cartList, orderCartList);

		redisTemplate.boundHashOps("cartList").put(order.getUserId(), cartList_new);//放入缓存
		//3.清除redis中的购物车
		redisTemplate.boundHashOps("orderList").delete(order.getUserId());
	}

	public List<Cart> deleteGoodsFromCartList(List<Cart>  cartList,List<Cart> orderCartList){
		for(Cart orderCart : orderCartList){//遍历预购买的订单
			for(CartItem orderCartItem : orderCart.getCartItemList()){//遍历预购买订单的商品详情
				for(int cartIndex=0;cartIndex<cartList.size();cartIndex++){//遍历购物车中的商家
					if(cartList.get(cartIndex).getSellerName().equals(orderCart.getSellerName())){//查找当前商品在购物车中对应的商家，通过该条件可以实现一定的过滤，优化Suunto
						for(int index=0;index<cartList.get(cartIndex).getCartItemList().size();index++){//在查找的商家中查找对应的商品
							if(orderCartItem.getItemId().equals(cartList.get(cartIndex).getCartItemList().get(index).getItemId())){//从购物车中删除对应的商品信息
								//从对应的商家中清除对应的商品
								cartList.get(cartIndex).getCartItemList().remove(index);
								//如果购物车该商家对应的商品为空，则从购物车中清除该商家
								if(cartList.get(cartIndex).getCartItemList().size()<=0){
									cartList.remove(cartIndex);//从购物出中清除该商家的信息
								}
							}
						}
					}
				}
			}
		}

		return cartList;
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
//		List<TbPayLog> payLogList =(List<TbPayLog>)redisTemplate.boundHashOps("payLog").get(payLog.getUserId());
//		payLogList.remove(payLogList.size()-1);//移除最新的一项
		redisTemplate.boundHashOps("orderList").delete(payLog.getUserId());//移除选中列表
		redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
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
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	//查找今天的订单
	@Override
	public PageResult findAllByPage(String sellerId, int currentPage, int pageSize) {
		PageHelper.startPage(currentPage, pageSize);
		TbOrderExample tbOrderExample = new TbOrderExample();
		TbOrderExample.Criteria criteria = tbOrderExample.createCriteria();
		criteria.andSellerIdEqualTo(sellerId);
		criteria.andStatusEqualTo("2");
		PageInfo<TbOrder> tbOrderPageInfo = new PageInfo<>(tbOrderMapper.selectByExample(tbOrderExample));
		return new PageResult(tbOrderPageInfo.getTotal(), tbOrderPageInfo.getList());
	}


	//用户端获取退款订单

	@Override
	public List<RefundOrder> findAllRefundOrderByUsername(String username) {
		TbRefundExample tbRefundExample = new TbRefundExample();
		tbRefundExample.setOrderByClause("start_time");
		TbRefundExample.Criteria criteria = tbRefundExample.createCriteria();
		criteria.andUserIdEqualTo(username);
		List<TbRefund> tbRefunds = tbRefundMapper.selectByExample(tbRefundExample);
		List<RefundOrder> refundOrderList = new ArrayList<RefundOrder>();
		for(TbRefund tbRefund:tbRefunds){
			RefundOrder refundOrder = new RefundOrder();
			refundOrder.setTbRefund(tbRefund);
			TbOrderItem tbOrderItem = orderItemMapper.selectByPrimaryKey(Long.parseLong(tbRefund.getOrderItemId()));
			refundOrder.setTbOrderItem(tbOrderItem);
			refundOrderList.add(refundOrder);
		}
		return refundOrderList;
	}

	@Override
	public int sendGoods(String orderId, String shippingCode) {
		TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
		tbOrder.setStatus("4");
		tbOrder.setShippingCode(shippingCode);
		int i = orderMapper.updateByPrimaryKey(tbOrder);
		return i;
	}

	@Override
	public List<TbOrderItem> viewGoods(String orderId) {
		TbOrderItemExample tbOrderItemExample = new TbOrderItemExample();
		TbOrderItemExample.Criteria criteria = tbOrderItemExample.createCriteria();
		criteria.andOrderIdEqualTo(Long.parseLong(orderId));
		List<TbOrderItem> tbOrderItems = orderItemMapper.selectByExample(tbOrderItemExample);
		return tbOrderItems;
	}

	//获取未处理的退款订单
	@Override
	public List<RefundOrderShop> findRefundOrderBySellerId(String sellerId) {
		TbRefundExample tbRefundExample = new TbRefundExample();
		TbRefundExample.Criteria criteria = tbRefundExample.createCriteria();
		criteria.andSellerIdEqualTo(sellerId);
		criteria.andStatusEqualTo("0");
		List<TbRefund> tbRefunds = tbRefundMapper.selectByExample(tbRefundExample);
		ArrayList<RefundOrderShop> refundOrderShops = new ArrayList<>();
		for(TbRefund tbRefund:tbRefunds){
			RefundOrderShop refundOrderShop = new RefundOrderShop();
			refundOrderShop.setOutTradeNo(tbRefund.getOutTradeNo());
			refundOrderShop.setId(tbRefund.getId());
			String orderItemId = tbRefund.getOrderItemId();
			TbOrderItem tbOrderItem = orderItemMapper.selectByPrimaryKey(Long.parseLong(orderItemId));
			Long orderId = tbOrderItem.getOrderId();
			TbOrder tbOrder = tbOrderMapper.selectByPrimaryKey(orderId);
			refundOrderShop.setTbOrder(tbOrder);
			refundOrderShops.add(refundOrderShop);
		}
		System.out.println(refundOrderShops.size()+"========================");
		return refundOrderShops;
	}


	//查看退款订单的退款原因及类型
	@Override
	public TbRefund viewRefundOrder(String refundId) {
		return tbRefundMapper.selectByPrimaryKey(refundId);
	}

	//拒接
	@Override
	public int returnRefund(String refundId, String reply) {
		System.out.println(refundId+"=======================");
		TbRefund tbRefund = tbRefundMapper.selectByPrimaryKey(refundId);
		tbRefund.setStatus("3");
		tbRefund.setReply(reply);
		return tbRefundMapper.updateByPrimaryKey(tbRefund);
	}

	@Override
	public int refundOk(String refundId) {
		TbRefund tbRefund = tbRefundMapper.selectByPrimaryKey(refundId);
		tbRefund.setStatus("2");
		return tbRefundMapper.updateByPrimaryKey(tbRefund);
	}

	@Override
	public PageResult findHistoryByPage(String sellerId ,int currentPage, int pageSize) {
		PageHelper.startPage(currentPage,pageSize);
		TbOrderExample tbOrderExample = new TbOrderExample();
		TbOrderExample.Criteria criteria = tbOrderExample.createCriteria();
		criteria.andStatusNotEqualTo("1");
		criteria.andStatusNotEqualTo("9");
		List<TbOrder> tbOrders = tbOrderMapper.selectByExample(tbOrderExample);
		System.out.println(tbOrders.size()+"=========================");
		ArrayList<TbOrdersGroup> tbOrdersGroupArrayList = new ArrayList<TbOrdersGroup>();
		for(TbOrder tbOrder :tbOrders){
			TbOrdersGroup tbOrdersGroup = new TbOrdersGroup();
			BeanUtils.copyProperties(tbOrder,tbOrdersGroup);
			tbOrdersGroup.setOrderId(tbOrder.getOrderId().toString());
			tbOrdersGroupArrayList.add(tbOrdersGroup);
		}
		PageInfo<TbOrdersGroup> tbOrderPageInfo = new PageInfo<TbOrdersGroup>(tbOrdersGroupArrayList);
		System.out.println(tbOrderPageInfo.getTotal()+"========================="+tbOrderPageInfo.getPages());
		return new PageResult(tbOrderPageInfo.getTotal(),tbOrderPageInfo.getList());
	}
}
