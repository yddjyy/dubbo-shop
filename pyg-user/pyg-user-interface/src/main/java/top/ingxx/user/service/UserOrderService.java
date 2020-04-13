package top.ingxx.user.service;

import top.ingxx.pojo.TbOrder;
import top.ingxx.pojo.TbOrderItem;
import top.ingxx.pojo.TbRefund;
import top.ingxx.pojoGroup.WaitPaymentOrder;
import top.ingxx.untils.entity.PageResult;

import java.util.List;

/**
 * 功能简述<br>
 * 〈〉
 *
 * @author Asus
 * @create 2019/4/18
 */
public interface UserOrderService {

    public PageResult findAllOrder(String username, int pageNum, int pageSize);

    public PageResult findOrderByStatus(String username, int pageNum, int pageSize,String status);

    public List<WaitPaymentOrder> findWaitPayment(String username);

    public int delOrderByOrderId(String orderId);

    public Boolean cancelOrder(String outTradeNo);

    public TbOrder findAddressByOrderId(String orderId);

    public WaitPaymentOrder findOrderDetailInfoByOrderId(String orderId);

    public WaitPaymentOrder findOrderDetailInfoByOrderItemId(String orderItemid);


    public TbRefund findRefundOrderByOrderItemId(String orderItemId);

    public Boolean addRefundOrder(TbRefund tbRefund,String userid);

    public boolean cancelRefundOrder(String id);

    public TbOrderItem findOneItemByOrderItemId(Long orderItemId);

    public Boolean updateStatusByOrderId(Long orderId,int status);

}
