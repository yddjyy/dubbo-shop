package top.ingxx.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import top.ingxx.pojo.TbItem;
import top.ingxx.search.service.ItemSearchService;


@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map search(Map searchMap) {
//        Map map = new HashMap();
//        Query query = new SimpleQuery("*:*");
//        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
//        query.addCriteria(criteria);
//        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
//        map.put("rows",page.getContent());

//        Map map = new HashMap();
//        HighlightQuery query = new SimpleHighlightQuery();
//        //关键字查询
//        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
//        query.addCriteria(criteria);
//
//        //构建高亮选项对象
//        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//高亮域
//        highlightOptions.setSimplePrefix("<em style='color:red'>"); //前缀
//        highlightOptions.setSimplePostfix("</em>");
//        //给查询对象设置高亮选项
//        query.setHighlightOptions(highlightOptions);
//        //返回高亮页对象
//        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
//
//        //高亮入口集合(每条记录高亮入口)
//        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
//        for (HighlightEntry<TbItem> entry : entryList) {
//            //获取高亮列表(高亮域的个数)
//            List<Highlight> highlights = entry.getHighlights();
////            for (Highlight h:highlights){
////                List<String> list = h.getSnipplets();//一个域可能存储多值
////            }
//            if(highlights.size()>0 &&  highlights.get(0).getSnipplets().size()>0 ){
//                TbItem item = entry.getEntity();
//                item.setTitle(highlights.get(0).getSnipplets().get(0));
//            }
//
//        }
//        map.put("rows", page.getContent());
//        return map;
        Map map = new HashMap();
        String keywords = (String) searchMap.get("keywords"); //去掉空格
        searchMap.put("keywords", keywords.replace(" ", ""));
        //查询列表
        map.putAll(searchList(searchMap));
        //查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);

        String category = (String) searchMap.get("category");
        if (!"".equals(category)) {
            Map brandAndSpecList = searchBrandAndSpecList(category);
            map.putAll(brandAndSpecList);
        } else {
            if (categoryList.size() > 0) {
                //查询品牌和规格列表
                Map brandAndSpecList = searchBrandAndSpecList(categoryList.get(0));
                map.putAll(brandAndSpecList);
            }
        }
        return map;
    }


    /**
     * 查询列表
     *
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap) {
        Map map = new HashMap();
        HighlightQuery query = new SimpleHighlightQuery();
        //关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //分类过滤
        if (!"".equals(searchMap.get("category"))) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //品牌过滤
        if (!"".equals(searchMap.get("brand"))) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //规格过滤
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //按价格过滤
        if (!"".equals(searchMap.get("price"))) {
            String[] price = searchMap.get("price").toString().split("-");
            if (!price[0].equals("0")) { //如果最低价格不等于0
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria priceCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                filterQuery.addCriteria(priceCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals("*")) { //如果最高价格不等于*
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria priceCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                filterQuery.addCriteria(priceCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //分页
        Integer pageNum = (Integer) searchMap.get("pageNum");
        if (pageNum == null) {
            pageNum = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageNum = 20;
        }
        query.setOffset((pageNum - 1) * pageSize);//起始索引
        query.setRows(pageSize); //每页数量

        //按价格排序
        String sortValue = searchMap.get("sort").toString(); //升序asc 降序desc
        String sortField = searchMap.get("sortField").toString(); //排序字段
        Sort sort = null;
        if (sortValue != null && !sortValue.equals("")) {
            if (sortValue.equals("ASC")) {
                sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
            } else if (sortValue.equals("DESC")) {
                sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
            }
        }
        query.addSort(sort);

        //构建高亮选项对象
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>"); //前缀
        highlightOptions.setSimplePostfix("</em>");
        //给查询对象设置高亮选项
        query.setHighlightOptions(highlightOptions);
        //返回高亮页对象
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //高亮入口集合(每条记录高亮入口)
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
        for (HighlightEntry<TbItem> entry : entryList) {
            //获取高亮列表(高亮域的个数)
            List<Highlight> highlights = entry.getHighlights();
//            for (Highlight h:highlights){
//                List<String> list = h.getSnipplets();//一个域可能存储多值
//            }
            if (highlights.size() > 0 && highlights.get(0).getSnipplets().size() > 0) {
                TbItem item = entry.getEntity();
                item.setTitle(highlights.get(0).getSnipplets().get(0));
            }

        }
        map.put("rows", page.getContent());
        map.put("totalPages", page.getTotalPages());//总页数
        map.put("total", page.getTotalElements());//总条数
        return map;
    }

    /**
     * 查询分组信息(查询商品分类列表)
     *
     * @return
     */
    private List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList<>();
        Query query = new SimpleQuery("*:*");
        //设置查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项  group by
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //获取分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取分组结果
        GroupResult<TbItem> result = page.getGroupResult("item_category");
        //获取分组入口页
        Page<GroupEntry<TbItem>> groupEntries = result.getGroupEntries();
        //获取分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());
        }
        return list;
    }


    /**
     * 查询品牌和规格列表
     *
     * @param category 商品分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        //获取模板ID
        Long templteId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (templteId != null) {
            //获取品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templteId);
            map.put("brandList", brandList);
            //根据模板ID取得规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(templteId);
            map.put("specList", specList);
        }
        return map;
    }

    /**
     * 增量添加
     * @param list
     */
    public void importList(List list){
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    public void deleteByGoodsIds(List goodsIds){
        SolrDataQuery query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
