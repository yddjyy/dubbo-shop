package top.ingxx.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import top.ingxx.manager.service.GoodsService;
import top.ingxx.mapper.*;
import top.ingxx.pojo.*;
import top.ingxx.pojo.TbGoodsExample.Criteria;
import top.ingxx.pojoGroup.Goods;
import top.ingxx.untils.entity.PageResult;

import java.util.*;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service

public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    @Transactional
    public void add(Goods goods) {
        goods.getGoods().setAuditStatus("0"); //状态为未审核
        goodsMapper.insert(goods.getGoods()); //插入商品基本信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId()); //设置商品id（SPU）
        goodsDescMapper.insert(goods.getGoodsDesc()); //插入商品扩展表
        saveItemList(goods); //插入sku
    }

    //插入sku列表
    private void saveItemList(Goods goods){
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            for (TbItem item : goods.getItemList()) {
                //构建标题
                String title = goods.getGoods().getGoodsName(); //SPU名称
                Map<String, Object> map = JSON.parseObject(item.getSpec());
                for (String key : map.keySet()) {
                    title += " " + map.get(key);
                }
                item.setTitle(title);
                setItemValue(item,goods);
                itemMapper.insert(item);
            }
        }else {
            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());
            item.setPrice(goods.getGoods().getPrice());
            item.setNum(99999);
            item.setStatus("1");
            item.setIsDefault("1");
            item.setSpec("{}");//规格
            setItemValue(item,goods);
            itemMapper.insert(item);
        }
    }

    private void setItemValue(TbItem item,Goods goods){
        //商品分类
        item.setCategoryid(goods.getGoods().getCategory3Id()); //三级分类
        //创建日期
        item.setCreateTime(new Date());
        //更新日期
        item.setUpdateTime(new Date());
        //商品ID
        item.setGoodsId(goods.getGoods().getId());
        //商家id
        item.setSellerId(goods.getGoods().getSellerId());
        //分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //商家名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getNickName());
        //图片
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imageList.size() > 0) {
            String url = imageList.get(0).get("url").toString();
            String[] split = url.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\\"", "").split(",");
            Random random = new Random();
            int index = random.nextInt(split.length);//生成随机数
            item.setImage(split[index].toString());
        }
    }

    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        //更新基本表数据
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        //更新扩展表
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        //更新sku表
        //删除原有的sku
        TbItemExample example = new TbItemExample();
        example.createCriteria().andGoodsIdEqualTo(goods.getGoods().getId());
        itemMapper.deleteByExample(example);
        //新增新sku
        saveItemList(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        //查询商品表
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
        //商品扩展表
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);

        //读取sku
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> list = itemMapper.selectByExample(example);
        goods.setItemList(list);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsDelete("1"); //逻辑删除
            goodsMapper.updateByPrimaryKey(goods);
        }
    }
//
//    public void resubmit(Long[] ids){
//        for (Long id : ids) {
//            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
//            goods.setIsDelete("1"); //逻辑删除
//            goodsMapper.updateByPrimaryKey(goods);
//        }
//    }

    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteIsNull();//如果没有逻辑删除
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        for(Long id :ids){
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);
        }
    }
     @Override
    public List<TbItem> findItemListByGoodsIdListAndStatus(Long []goodsIds,String status){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(status);
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        return itemMapper.selectByExample(example);
    }



}
