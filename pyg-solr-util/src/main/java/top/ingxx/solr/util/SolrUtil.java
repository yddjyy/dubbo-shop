package top.ingxx.solr.util;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;
import top.ingxx.mapper.TbItemMapper;
import top.ingxx.pojo.TbItem;
import top.ingxx.pojo.TbItemExample;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemDate(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过
        List<TbItem> itemList = tbItemMapper.selectByExample(example);
        for (TbItem item : itemList){
            System.out.println(item.getId()+"   "+item.getTitle());
            Map map = JSON.parseObject(item.getSpec(), Map.class); //配置动态域
            item.setSpecMap(map);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil util = (SolrUtil)context.getBean("solrUtil");
        util.importItemDate();

    }
}
