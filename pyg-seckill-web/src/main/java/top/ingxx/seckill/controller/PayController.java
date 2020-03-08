package top.ingxx.seckill.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.pay.service.AliPayService;
import top.ingxx.pojo.TbSeckillOrder;
import top.ingxx.seckill.service.SeckillOrderService;
import top.ingxx.untils.entity.PygResult;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    AliPayService aliPayService;

    @Reference
    SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    public Map createNative(){
        //1. 获取当前登录用户
        String username  = SecurityContextHolder.getContext().getAuthentication().getName();
        //2 . 获取支付日志（从缓存中redis)
        //TbPayLog tbPayLog = orderService.searchPayLogFromRedis(userid);
        TbSeckillOrder order = seckillOrderService.searchOrderFromRedisByUserId(username);
        if(order!=null){
           return aliPayService.createNative(order.getId()+"",order.getMoney().toString());
       }else{
           return new HashMap();
       }
    }

    @RequestMapping("/queryPayStatus")
    public PygResult queryPayStatus(String out_trade_no){
        //1. 获取当前登录用户
        String username  = SecurityContextHolder.getContext().getAuthentication().getName();
        PygResult pygResult = null;
        //作为标志位，当i>100时提示二维码超时，进行超时处理，方式循环无法退出，造成死循环。
        int i = 0;
        //循环进行订单信息的查询，i
        while (true){
          Map map =  aliPayService.queryPayStatus(out_trade_no);
          if(map== null){
              //发生异常时
             pygResult =  new PygResult(false,"支付发生错误");
             break;
          }
          //支付成功
          if(map.get("trade_status").equals("TRADE_SUCCESS")){
              pygResult = new PygResult(true,"支付成功");
              //保存秒杀订单到数据库
              seckillOrderService.saveOrderFromRedisToDb(username,Long.valueOf(out_trade_no),String.valueOf(map.get("trade_no")));
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
                //关闭支付宝支付
                 Map result = aliPayService.closePay(out_trade_no);
                 if(result!=null && result.get("sub_msg").equals("交易已被支付")){
                     pygResult = new PygResult(true,"支付成功");
                     //保存秒杀订单到数据库
                     seckillOrderService.saveOrderFromRedisToDb(username,Long.valueOf(out_trade_no),String.valueOf(map.get("trade_no")));
                 }else{
                     //删除订单
                     seckillOrderService.deleteOrderFromRedis(username,Long.valueOf(out_trade_no));
                 }
                break;
            }
        }
        return pygResult;
    }
}
