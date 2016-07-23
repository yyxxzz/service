package com.yoho.gateway.model.product;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 凑单商品VO
 * @author caoyan
 *
 */
public class TogetherProductRspVo implements Serializable{
	
	private static final long serialVersionUID = 8467587291317523119L;

    @JSONField(name="page")
    private Integer page;
	
	@JSONField(name="page_total")
    private Integer pageTotal;
	
    @JSONField(name="total")
    private Integer total;
    
    @JSONField(name="goods")
    private List<TogetherProductVo> goods;

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageTotal() {
		return pageTotal;
	}

	public void setPageTotal(Integer pageTotal) {
		this.pageTotal = pageTotal;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List<TogetherProductVo> getGoods() {
		return goods;
	}

	public void setGoods(List<TogetherProductVo> goods) {
		this.goods = goods;
	}

    
    
}
