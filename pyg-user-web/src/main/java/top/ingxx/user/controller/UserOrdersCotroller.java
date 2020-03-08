package top.ingxx.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.user.service.UserOrderService;

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
    public PageResult showOrdersByStatus(@RequestParam String username,int pageNum,int pageSize,String status){
        System.out.println(username+"订单信息：");
        return  userOrderService.findOrderByStatus(username,pageNum,pageSize,status);
    }
}
