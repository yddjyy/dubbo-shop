package top.ingxx.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.order.service.OrderService;
import top.ingxx.pay.service.AliPayService;
import top.ingxx.pojo.TbOrderItem;
import top.ingxx.pojo.TbRefund;
import top.ingxx.pojoGroup.RefundOrderShop;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.entity.PygResult;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private  OrderService orderService;

    @Reference(timeout = 50000)
    private AliPayService aliPayService;

    @RequestMapping("/findAllByPage")
    public PageResult findAllByPage(int currentPage, int pageSize){ ;
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return  orderService.findAllByPage(name,currentPage,pageSize);
    }

    @RequestMapping("/sendGoods")
    public PygResult sendGoods(String orderId, String shippingCode){
        int i = orderService.sendGoods(orderId, shippingCode);
        if(i==1){
            return new PygResult(true,"成功");
        }
        return new PygResult(false,"失败");
    }

    @RequestMapping("/viewGoods")
    public List<TbOrderItem> viewGoods(String orderId){
        return  orderService.viewGoods(orderId);
    }

    @RequestMapping("/findRefundOrderBySellerId")
    public List<RefundOrderShop> findRefundOrderBySellerId(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
         return orderService.findRefundOrderBySellerId(name);
    }

    //查看退款单信息
    @RequestMapping("/viewRefundOrder")
    public TbRefund viewRefundOrder(String refundId){
        return orderService.viewRefundOrder(refundId);
    }
    //拒绝退款
    @RequestMapping("/returnRefund")
    public PygResult returnRefund(String refundId,String reply){
        int i = orderService.returnRefund(refundId, reply);
        if(i==1){
            return new PygResult(true,"操作成功");
        }
        return new PygResult(false,"操作失败");
    }
    //同意退款
    @RequestMapping("/refundOk")
    @Transactional
    public PygResult refundOk(String refundId,String outTradeNo,String refundAmount){

        Map map = aliPayService.refundPay(outTradeNo, refundAmount);
        if((Boolean)map.get("flag")){
            int flag = orderService.refundOk(refundId);
            if(flag==1){
                return new PygResult(true,"操作成功");
            }
            return new PygResult(false,"操作失败");
        }else{
            return new PygResult(false,"操作失败");
        }

    }

    @RequestMapping("/findHistoryByPage")
    public PageResult findHistoryByPage(int currentPage ,int pageSize){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
       return orderService.findHistoryByPage(name,currentPage,pageSize);
    }
}
