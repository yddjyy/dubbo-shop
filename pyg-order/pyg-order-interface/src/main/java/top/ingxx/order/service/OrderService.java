package top.ingxx.order.service;

import top.ingxx.pojo.TbOrder;
import top.ingxx.pojo.TbOrderItem;
import top.ingxx.pojo.TbPayLog;
import top.ingxx.pojo.TbRefund;
import top.ingxx.pojoGroup.RefundOrder;
import top.ingxx.pojoGroup.RefundOrderShop;
import top.ingxx.untils.entity.PageResult;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbOrder order);
	
	
	/**
	 * 修改
	 */
	public void update(TbOrder order);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbOrder findOne(Long id);
	
	
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
	public PageResult findPage(TbOrder order, int pageNum, int pageSize);
	
	/**
	 * 根据用户ID获取支付日志
	 * @param userId
	 * @return
	 */
	public TbPayLog searchPayLogFromRedis(String userId);
	
	
	/**
	 * 支付成功修改状态
	 * @param out_trade_no
	 * @param transaction_id
	 */
	public void updateOrderStatus(String out_trade_no, String transaction_id);
	//查找今日订单
	public PageResult findAllByPage(String sellerId,int currentPage,int pageSize);

	//通过用户名获取用户退款订单项
	public List<RefundOrder> findAllRefundOrderByUsername(String username);

	//发货
	public int sendGoods(String orderId,String shippingCode);
	//查看订单商品信息
	public List<TbOrderItem> viewGoods(String orderId);

	//获取退款订单
	public List<RefundOrderShop> findRefundOrderBySellerId(String sellerId);


	//查看退款单信息
	public TbRefund viewRefundOrder(String refundId);

	//拒绝退款
	public int returnRefund(String refundId,String reply);

	//同意退款
	public int refundOk(String refundId);

	//查找历史记录
	public PageResult findHistoryByPage(String sellerId,int currentPage,int pageSize);
}
