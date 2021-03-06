package top.ingxx.page.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import top.ingxx.mapper.*;
import top.ingxx.page.service.ItemPageService;
import top.ingxx.pojo.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {


    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public Map getPage(Long goodsId) {

        //创建数据模型
        Map dataModel = new HashMap<>();
        //1.商品主表数据
        TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
        dataModel.put("goods", goods);
        //2.商品扩展表数据
        TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        dataModel.put("goodsDesc", goodsDesc);
        //3.读取商品分类

        String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
        String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
        String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
        dataModel.put("itemCat1", itemCat1);
        dataModel.put("itemCat2", itemCat2);
        dataModel.put("itemCat3", itemCat3);

        //4.读取SKU列表
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);//SPU ID
        criteria.andStatusEqualTo("1");//状态有效
        example.setOrderByClause("is_default");//按是否默认字段进行降序排序，目的是返回的结果第一条为默认SKU

        List<TbItem> itemList = itemMapper.selectByExample(example);
        dataModel.put("itemList", itemList);

        return dataModel;

    }
    @Autowired
    private TbUserMapper tbUserMapper;
    @Override
    public TbUser getUserInfo(String username) {
        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> tbUsers = tbUserMapper.selectByExample(tbUserExample);
        if(tbUsers.size()>0)
            return tbUsers.get(0);
        return null;
    }
}
