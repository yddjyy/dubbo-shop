package top.ingxx.cart.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.order.service.OrderService;
import top.ingxx.pay.service.AliPayService;
import top.ingxx.pojo.TbPayLog;
import top.ingxx.untils.entity.PygResult;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    AliPayService aliPayService;

    @Reference
    OrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative(){
        //1. 获取当前登录用户
        String userid  = SecurityContextHolder.getContext().getAuthentication().getName();
        //2 . 获取支付日志（从缓存中redis)
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(userid);
       if(tbPayLog!=null){
           return aliPayService.createNative(tbPayLog.getOutTradeNo()+"",tbPayLog.getTotalFee().toString());
       }else{
           return new HashMap();
       }
    }

    @RequestMapping("/queryPayStatus")
    public PygResult queryPayStatus(String out_trade_no){
        PygResult pygResult = null;//作为标志位，当i>100时提示二维码超时，进行超时处理，方式循环无法退出，造成死循环。
        int i = 0; //循环进行订单信息的查询，i
        while (true){
          Map map =  aliPayService.queryPayStatus(out_trade_no);
          if(map== null){ //发生异常时
             pygResult =  new PygResult(false,"支付发生错误");
             break;
          }
          //支付成功
          if(map.get("trade_status").equals("TRADE_SUCCESS")){
              pygResult = new PygResult(true,"支付成功");
              //修改支付状态信息
              orderService.updateOrderStatus(out_trade_no,(String)map.get("trade_no"));
              break;
          }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
            //如果5分钟左右未支付将结束支付
            if(i>=100){
                pygResult  = new PygResult(false,"二维码超时");
                break;
            }
        }
        return pygResult;
    }
}
