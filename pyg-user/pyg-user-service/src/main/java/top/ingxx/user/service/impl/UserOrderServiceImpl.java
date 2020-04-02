package top.ingxx.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import top.ingxx.mapper.*;
import top.ingxx.pojo.*;
import top.ingxx.pojoGroup.OrderItems;
import top.ingxx.pojoGroup.Orders;
import top.ingxx.pojoGroup.WaitPaymentOrder;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.until.IdWorker;
import top.ingxx.user.service.UserOrderService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能简述<br>
 *
 * @author Asus
 * @create 2019/4/18
 */
@Service
public class UserOrderServiceImpl implements UserOrderService {
    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Autowired
    private TbSellerMapper tbSellerMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbPayLogMapper tbPayLogMapper;

    @Autowired
    private TbRefundMapper tbRefundMapper;

    @Autowired
    private IdWorker idWorker;


    @Override
    public PageResult findAllOrder(String username, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        //定义返回集合
        List<Orders> list = new ArrayList<>();
        TbOrderExample tbOrderExample = new TbOrderExample();
        TbOrderExample.Criteria criteria = tbOrderExample.createCriteria();
        criteria.andUserIdEqualTo(username);
        //进行分页
        Page<TbOrder> page =(Page<TbOrder>) tbOrderMapper.selectByExample(tbOrderExample);
        List<TbOrder> listordes = page.getResult();
        //通过订单表订单号获取对应的orderitem
        for(int i=0;i<listordes.size();i++){
            Orders orders = new Orders();
            TbOrder tbOrder = new TbOrder();
            tbOrder=listordes.get(i);
            orders.setTbOrder(tbOrder);
            TbOrderItemExample tbOrderItemExample = new TbOrderItemExample();
            TbOrderItemExample.Criteria criteria1 = tbOrderItemExample.createCriteria();
            criteria1.andOrderIdEqualTo(tbOrder.getOrderId());
            TbOrderItem tbOrderItem = tbOrderItemMapper.selectByExample(tbOrderItemExample).get(0);
            orders.setTbOrderItem(tbOrderItem);
            //根据sellerid获取商家名称并存放于该orders对象中
            orders.setNickName(tbSellerMapper.selectByPrimaryKey(tbOrderItem.getSellerId()).getNickName());
            //根据itemid获取当前商品规格并存放于该orders对象中
            orders.setGoodsSpec(tbItemMapper.selectByPrimaryKey(tbOrderItem.getItemId()).getSpec());
            list.add(orders);
        }
        //返回当前用户的订单信息
        return new PageResult(page.getTotal(), list);
    }

    @Override
    public PageResult findOrderByStatus(String username, int pageNum, int pageSize, String status) {
        PageHelper.startPage(pageNum, pageSize);
        //定义返回集合
        List<WaitPaymentOrder> waitPaymentOrderList = new ArrayList<>();
        TbOrderExample tbOrderExample = new TbOrderExample();
        TbOrderExample.Criteria criteria = tbOrderExample.createCriteria();
        criteria.andUserIdEqualTo(username);
        criteria.andStatusEqualTo(status);
        //进行分页
        PageInfo<TbOrder> tbOrderPageInfo = new PageInfo<>(tbOrderMapper.selectByExample(tbOrderExample));
        List<TbOrder> list = tbOrderPageInfo.getList();
        List<TbOrder> tbOrderList = tbOrderPageInfo.getList();
        //通过订单号进行返回信息的组装
        for(TbOrder tborder : tbOrderList){
            WaitPaymentOrder waitPaymentOrder = new WaitPaymentOrder();
            waitPaymentOrder.setCreateTime(tborder.getCreateTime());
            waitPaymentOrder.setPostFee(tborder.getPostFee());
            waitPaymentOrder.setTotalFee(tborder.getPayment());
            waitPaymentOrder.setStatus(tborder.getStatus());
            waitPaymentOrder.setOrderId(tborder.getOrderId().toString());
            waitPaymentOrder.setOutTradeNo(findOutTradeNoByOrderId(tborder.getOrderId(),username));
            //根据订单id查找订单项
            TbOrderItemExample tbOrderItemExample = new TbOrderItemExample();
            TbOrderItemExample.Criteria criteria1 = tbOrderItemExample.createCriteria();
            criteria1.andOrderIdEqualTo(tborder.getOrderId());
            List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.selectByExample(tbOrderItemExample);//获得订单项
            //根据订单项查找对应商品的规格
            ArrayList<OrderItems> orderItems = new ArrayList<>();
            for(TbOrderItem tbOrderItem :tbOrderItemList){
                OrderItems orderItem = new OrderItems();
                BeanUtils.copyProperties(tbOrderItem, orderItem);
                orderItem.setId(tbOrderItem.getId().toString());
                orderItem.setOrderId(tbOrderItem.getOrderId().toString());
                TbItem tbItem = tbItemMapper.selectByPrimaryKey(tbOrderItem.getItemId());
                orderItem.setSpec(tbItem.getSpec());
                orderItems.add(orderItem);
            }
            waitPaymentOrder.setOrderItems(orderItems);//将一次订单中相同商家的订单放到一个订单项中
            waitPaymentOrderList.add(waitPaymentOrder);
        }
        //返回当前用户的订单信息
        return new PageResult(tbOrderPageInfo.getTotal(), waitPaymentOrderList);
    }

    /***
     * 根据订单id查找流水号
     * @param OrderId
     * @param username
     * @return
     */
    public String findOutTradeNoByOrderId(Long OrderId,String username){
        //获取支付历史中为支付的订单
        TbPayLogExample tbPayLogExample = new TbPayLogExample();
        TbPayLogExample.Criteria criteria = tbPayLogExample.createCriteria();
        criteria.andUserIdEqualTo(username);
        criteria.andTradeStateEqualTo("1");
        List<TbPayLog> payLogList = tbPayLogMapper.selectByExample(tbPayLogExample);
        if(payLogList.size()<=0){
            return null;
        }
        for(TbPayLog tbPayLog :payLogList){
            String[] split = tbPayLog.getOrderList().split(",");
            for(String id :split){
                if(id.trim().equals(OrderId.toString())){
                    return tbPayLog.getOutTradeNo();
                }
            }
        }
        return null;
    }
    /**
     * 查找待付款的订单
     * @param username
     * @return
     */
    @Override
    public List<WaitPaymentOrder> findWaitPayment(String username) {
        //获取支付历史中为支付的订单
        TbPayLogExample tbPayLogExample = new TbPayLogExample();
        TbPayLogExample.Criteria criteria = tbPayLogExample.createCriteria();
        criteria.andUserIdEqualTo(username);
        criteria.andTradeStateEqualTo("0");
        List<TbPayLog> payLogList = tbPayLogMapper.selectByExample(tbPayLogExample);
        if(payLogList.size()<=0){
            return null;
        }
        ArrayList<WaitPaymentOrder> waitPaymentOrderList = new ArrayList<>();
        //封装返回类集合
        for(TbPayLog tbPayLog : payLogList){
            WaitPaymentOrder waitPaymentOrder = new WaitPaymentOrder();
            waitPaymentOrder.setOutTradeNo(tbPayLog.getOutTradeNo());
            waitPaymentOrder.setCreateTime(tbPayLog.getCreateTime());
            waitPaymentOrder.setTotalFee(tbPayLog.getTotalFee());
            String[] split = tbPayLog.getOrderList().split(",");//获取订单号
            if(split.length<=0){
                return null;
            }
            ArrayList<OrderItems> orderItems = new ArrayList<>();
            for(String orderId : split){//遍历订单号
                Long id = Long.parseLong(orderId.trim());
                TbOrderItemExample tbOrderItemExample = new TbOrderItemExample();
                TbOrderItemExample.Criteria criteria1 = tbOrderItemExample.createCriteria();
                criteria1.andOrderIdEqualTo(id);
                List<TbOrderItem> tbOrderItems = tbOrderItemMapper.selectByExample(tbOrderItemExample);
                for(TbOrderItem tbOrderItem :tbOrderItems){
                    OrderItems orderItem = new OrderItems();
                    BeanUtils.copyProperties(tbOrderItem, orderItem);
                    orderItem.setId(tbOrderItem.getId().toString());
                    orderItem.setOrderId(tbOrderItem.getOrderId().toString());
                    TbItem tbItem = tbItemMapper.selectByPrimaryKey(tbOrderItem.getItemId());
                    orderItem.setSpec(tbItem.getSpec());
                    orderItems.add(orderItem);
                }
            }
            waitPaymentOrder.setOrderItems(orderItems);
            waitPaymentOrderList.add(waitPaymentOrder);
        }
        return waitPaymentOrderList;
    }

    /**
     * 通过订单id删除订单
     * @param orderId
     */
    @Override
    public int delOrderByOrderId(String orderId) {
        TbOrder tbOrder = tbOrderMapper.selectByPrimaryKey(Long.parseLong(orderId));
        if(tbOrder==null){
            return 0;
        }
        tbOrder.setStatus("9");
       int flag= tbOrderMapper.updateByPrimaryKey(tbOrder);
       return flag;
    }

    @Override
    @Transactional
    public Boolean cancelOrder(String outTradeNo) {
        TbPayLog tbPayLog = tbPayLogMapper.selectByPrimaryKey(outTradeNo);
        tbPayLog.setTradeState("2");//设置订单状态为取消
        String orderIdList = tbPayLog.getOrderList();
        int payLogFlag = tbPayLogMapper.updateByPrimaryKey(tbPayLog);
        //获取对应的订单编号
        String[] split = orderIdList.split(",");
        for(String id: split){
            TbOrder tbOrder = tbOrderMapper.selectByPrimaryKey(Long.parseLong(id.trim()));
            tbOrder.setStatus("9");
            int tbOrderFlag = tbOrderMapper.updateByPrimaryKey(tbOrder);
        }
        return true;
    }

    /**
     * 通过订单id获取订单地址
     * @param orderId
     * @return
     */
    @Override
    public TbOrder findAddressByOrderId(String orderId) {
        TbOrderExample tbOrderExample = new TbOrderExample();
        TbOrderExample.Criteria criteria = tbOrderExample.createCriteria();
        criteria.andOrderIdEqualTo(Long.parseLong(orderId.trim()));
        List<TbOrder> tbOrders = tbOrderMapper.selectByExample(tbOrderExample);
        if(tbOrders.size()==0){
            return null;
        }
        return tbOrders.get(0);
    }

    @Override
    public WaitPaymentOrder findOrderDetailInfoByOrderId(String orderId) {
        TbOrderItemExample tbOrderItemExample = new TbOrderItemExample();
        TbOrderItemExample.Criteria criteria = tbOrderItemExample.createCriteria();
        criteria.andOrderIdEqualTo(Long.parseLong(orderId.trim()));
        List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.selectByExample(tbOrderItemExample);

        WaitPaymentOrder waitPaymentOrder = new WaitPaymentOrder();

        TbOrder tbOrder = tbOrderMapper.selectByPrimaryKey(Long.parseLong(orderId.trim()));
        waitPaymentOrder.setOrderId(tbOrder.getOrderId().toString());
        waitPaymentOrder.setCreateTime(tbOrder.getPaymentTime());
        waitPaymentOrder.setTotalFee(tbOrder.getPayment());
        waitPaymentOrder.setPostFee(tbOrder.getPostFee());
        ArrayList<OrderItems> orderItemsList = new ArrayList<>();
        for(TbOrderItem tbOrderItem :tbOrderItemList){
            OrderItems orderItems = new OrderItems();

            BeanUtils.copyProperties(tbOrderItem, orderItems);
            orderItems.setId(tbOrderItem.getId().toString());
            orderItems.setOrderId(tbOrderItem.getOrderId().toString());
            orderItems.setSpec(tbItemMapper.selectByPrimaryKey(tbOrderItem.getItemId()).getSpec());
            orderItemsList.add(orderItems);
        }
        waitPaymentOrder.setOrderItems(orderItemsList);
        return waitPaymentOrder;
    }

    @Override
    public WaitPaymentOrder findOrderDetailInfoByOrderItemId(String orderItemid) {
        WaitPaymentOrder waitPaymentOrder = new WaitPaymentOrder();
        TbOrderItem tbOrderItem = tbOrderItemMapper.selectByPrimaryKey(Long.parseLong(orderItemid.trim()));
        OrderItems orderItems = new OrderItems();
        BeanUtils.copyProperties(tbOrderItem,orderItems);
        orderItems.setId(tbOrderItem.getId().toString());
        orderItems.setOrderId(tbOrderItem.getOrderId().toString());
        orderItems.setSpec(tbItemMapper.selectByPrimaryKey(tbOrderItem.getItemId()).getSpec());
        ArrayList<OrderItems> orderItems1 = new ArrayList<>();
        orderItems1.add(orderItems);
        waitPaymentOrder.setOrderItems(orderItems1);
        waitPaymentOrder.setCreateTime(tbOrderMapper.selectByPrimaryKey(tbOrderItem.getOrderId()).getPaymentTime());
        return waitPaymentOrder;
    }

    @Override
    public TbRefund findRefundOrderByOrderItemId(String orderItemId) {
        TbRefundExample tbRefundExample = new TbRefundExample();
        TbRefundExample.Criteria criteria = tbRefundExample.createCriteria();
        criteria.andOrderItemIdEqualTo(orderItemId);
        criteria.andStatusNotEqualTo("4");
        criteria.andStatusNotEqualTo("3");
        List<TbRefund> tbRefunds = tbRefundMapper.selectByExample(tbRefundExample);
        if (tbRefunds.size() > 0) {
            return tbRefunds.get(0);
        }
        return null;
    }

    @Override
    public Boolean addRefundOrder(TbRefund tbRefund) {
        tbRefund.setId(String.valueOf(idWorker.nextId()));
        tbRefund.setStatus("0");
        tbRefund.setStartTime(new Date());
        int insert = tbRefundMapper.insert(tbRefund);
        if(insert==1){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean cancelRefundOrder(String id) {
        TbRefund tbRefund = tbRefundMapper.selectByPrimaryKey(id);
        tbRefund.setStatus("4");
        int i = tbRefundMapper.updateByPrimaryKey(tbRefund);
        if(i==1){
            return true;
        }else {
                return false;
            }
    }
}
