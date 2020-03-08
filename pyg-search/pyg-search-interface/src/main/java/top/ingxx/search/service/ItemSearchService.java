package top.ingxx.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    public Map search(Map searchMap);

    /**
     * 增量更新
     * @param list
     */
    public void importList(List list);

    /**
     * 批量删除
     * @param goodsIds
     */
    public void deleteByGoodsIds(List goodsIds);
}
