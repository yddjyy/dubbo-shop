package top.ingxx.manager.service;

import java.util.List;

import top.ingxx.pojo.TbGoods;

import top.ingxx.pojo.TbItem;
import top.ingxx.pojoGroup.Goods;
import top.ingxx.untils.entity.PageResult;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface GoodsService {

    /**
     * 返回全部列表
     *
     * @return
     */
    public List<TbGoods> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    public void add(Goods goods);


    /**
     * 修改
     */
    public void update(Goods goods);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    public Goods findOne(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 分页
     *
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize);


    /**
     * 修改审核状态
     * @param ids 需要审核的id
     * @param status 状态
     */
    public void updateStatus(Long[] ids,String status);


    /**
     * 根据spu 查询sku
     * @param goodsIds
     * @param status
     * @return
     */
    public List<TbItem> findItemListByGoodsIdListAndStatus(Long []goodsIds, String status);
}
