package com.yoho.gateway.model.product;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 商品尺寸信息VO
 * @author xieyong
 *
 */
public class GoodsSizeVo {
	
	/**
	 * GoodsSize的主键ID
	 */
	@JSONField(name="size_id")
    private Integer id;
    
	/**
     * 精确到尺码的skuId
     */
	@JSONField(name="product_sku")
    private Integer goodsSizeSkuId;
    
    /**
     * 对应尺码的库存数
     */
    @JSONField(name="storage_number")
    private Integer goodsSizeStorageNum;
    
    @JSONField(name="size_name")
	private String sizeName;
    
    @JSONField(name="order_by")
    private Integer orderBy;

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGoodsSizeSkuId() {
		return goodsSizeSkuId;
	}

	public void setGoodsSizeSkuId(Integer goodsSizeSkuId) {
		this.goodsSizeSkuId = goodsSizeSkuId;
	}

	public Integer getGoodsSizeStorageNum() {
		return goodsSizeStorageNum;
	}

	public void setGoodsSizeStorageNum(Integer goodsSizeStorageNum) {
		this.goodsSizeStorageNum = goodsSizeStorageNum;
	}

	public String getSizeName() {
		return sizeName;
	}

	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}

	public Integer getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}
}