package top.ingxx.page.service;

import java.util.Map;

public interface ItemPageService {

	/**
	 * 生成商品详细页
	 * @param goodsId
	 * @return
	 */
	public Map getPage(Long goodsId);
	

}
