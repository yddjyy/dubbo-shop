package top.ingxx.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import top.ingxx.mapper.TbItemMapper;
import top.ingxx.mapper.TbOrderItemMapper;
import top.ingxx.mapper.TbOrderMapper;
import top.ingxx.mapper.TbSellerMapper;
import top.ingxx.pojo.*;
import top.ingxx.pojoGroup.Orders;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.user.service.UserOrderService;

import java.util.ArrayList;
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
        List<Orders> list = new ArrayList<>();
        TbOrderExample tbOrderExample = new TbOrderExample();
        TbOrderExample.Criteria criteria = tbOrderExample.createCriteria();
        criteria.andUserIdEqualTo(username);
        criteria.andStatusEqualTo(status);
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

}
