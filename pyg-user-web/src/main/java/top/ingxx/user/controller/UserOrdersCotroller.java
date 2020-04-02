package top.ingxx.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.pojo.TbOrder;
import top.ingxx.pojo.TbRefund;
import top.ingxx.pojoGroup.WaitPaymentOrder;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.entity.PygResult;
import top.ingxx.user.service.UserOrderService;

import java.util.List;

/**
 * 功能简述<br>
 * 用户订单管理
 *
 * @author Asus
 * @create 2019/4/18
 */
@RestController
@RequestMapping("/userOrders")
public class UserOrdersCotroller {

    @Reference
    private UserOrderService userOrderService;

    @RequestMapping("/showOrders")
    public PageResult showOrders(@RequestParam String username,int pageNum,int pageSize){
        System.out.println(username+"订单信息：");
       return  userOrderService.findAllOrder(username,pageNum,pageSize);
    }

    @RequestMapping("/showOrdersByStatus")
    public PageResult showOrdersByStatus( String username,int pageNum,int pageSize,String status){
        return  userOrderService.findOrderByStatus(username,pageNum,pageSize,status);
    }

    /**
     * 查找未支付的订单
     * @return
     */
    @RequestMapping("/waitPayment")
    public List<WaitPaymentOrder> waitPayment(){
        //获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

       return  userOrderService.findWaitPayment(username);
    }

    /**
     * 删除订单
     * @param orderId
     * @return
     */
    @RequestMapping("/delOrderByOrderId")
    public PygResult delOrderByOrderId(String orderId){
          int flag=  userOrderService.delOrderByOrderId(orderId);
          if(flag==1){
              return new PygResult(true,"删除成功");
          }else{
              return new PygResult(false,"操作失败");
          }
    }

    @RequestMapping("/cancelOrder")
    public PygResult cancelOrder(String outTradeNo){
        Boolean flag = userOrderService.cancelOrder(outTradeNo);
        if(flag){
            return new PygResult(true,"取消订单");
        }else{
            return new PygResult(false,"操作失败");
        }
    }

    /**
     * 通过订单id获取订单地址
     * @param orderId
     * @return
     */
    @RequestMapping("/findAddressByOrderId")
    public TbOrder findAddressByOrderId(String orderId){
        return userOrderService.findAddressByOrderId(orderId);
    }

    /**
     * 通过订单id获取订单信息信息
     * @param orderId
     * @return
     */
    @RequestMapping("/findOrderDetailInfoByOrderId")
    public WaitPaymentOrder findOrderDetailInfoByOrderId(String orderId){
        return userOrderService.findOrderDetailInfoByOrderId(orderId);
    }

    /**
     * 通过orderitemid查询订单信息
     * @param orderItemId
     * @return
     */
    @RequestMapping("/findOrderDetailInfoByOrderItemId")
    public WaitPaymentOrder findOrderDetailInfoByOrderItemId(String orderItemId){
        return userOrderService.findOrderDetailInfoByOrderItemId(orderItemId);
    }


    /**
     * 退款相关
     */
    @RequestMapping("/findRefundOrderByOrderItemId")
    public TbRefund findRefundOrderByOrderItemId(String orderItemId){
        return userOrderService.findRefundOrderByOrderItemId(orderItemId);
    }

    @RequestMapping("/addRefundOrder")
    public PygResult addRefundOrder(@RequestBody TbRefund tbRefund){
        Boolean aBoolean = userOrderService.addRefundOrder(tbRefund);
        if(aBoolean){
            return  new PygResult(true,"添加成功");
        }
        return new PygResult(false,"添加失败");
    }

    //撤回退款申请
    @RequestMapping("/cancelRefundOrder")
    public PygResult cancelRefundOrder(String id){
        boolean b = userOrderService.cancelRefundOrder(id);
        if(b){
          return   new PygResult(true,"撤回成功");
        }
        return new PygResult(false,"撤回失败失败");
    }
}
