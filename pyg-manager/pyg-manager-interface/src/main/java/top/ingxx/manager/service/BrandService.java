package top.ingxx.manager.service;

import top.ingxx.pojo.TbBrand;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.entity.PygResult;

import java.util.List;
import java.util.Map;

public interface BrandService {
    /**
     * 查询所有品牌数据
     */
    public List<TbBrand> findAll();

    /**
     * 分页查询
     * @param pageNum 当前页面
     * @param rows 页面大小
     * @return
     */
    public PageResult findPage(Integer pageNum,Integer rows);

    /**
     * 添加品牌
     * @param brand  品牌对象
     * @return
     */
    public void add(TbBrand brand);

    /**
     * 根据ID查询实体
     * @param id ID
     * @return
     */
    public TbBrand findOne(Long id);

    /**
     * 修改实体
     * @param tbBrand 实体
     * @return
     */
    public void update(TbBrand tbBrand);

    /**
     * 删除实体
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 条件分页
     * @param brand 实体
     * @param pageNum 页码
     * @param rows 页面大小
     * @return
     */
    public PageResult findPage(TbBrand brand,Integer pageNum,Integer rows);

    /**
     * 获取品牌下拉列表
     * @return
     */
    public List<Map> selectOptionList();
}
