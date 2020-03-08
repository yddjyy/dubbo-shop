package top.ingxx.user.service;

import top.ingxx.untils.entity.PageResult;

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
}
