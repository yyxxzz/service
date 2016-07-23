package com.yoho.gateway.model.product;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 商品展现的VO
 * @author xieyong
 *
 */
public class ProductVo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8467587291317523119L;
	@JSONField(name="vip_silver")
	private Double vipSilver;
	
	@JSONField(name="vip_gold")
    private Double vipGold;
	
	@JSONField(name="vip_platinum")
    private Double vipPlatinum;

	@JSONField(name="product_id")
	private Integer productId;
	
	@JSONField(name="product_skn")
    private Integer productSkn;
	
	@JSONField(name="product_name")
    private String productName;
    
    @JSONField(name="cn_alphabet")
    private String cnAlphabet;

    private String phrase;
    
    @JSONField(name="sales_phrase")
    private String salesPhrase;

    @JSONField(name="is_advance")
    private String isAdvance;

    @JSONField(name="attribute")
    private Integer attribute;
    
    @JSONField(name="expect_arrival_time")
    private String expectArrivalTime;
    
    @JSONField(name="format_market_price")
    private String formatMarketPrice;
    
    @JSONField(name="market_price")
    private Integer marketPrice;
    
    @JSONField(name="format_sales_price")
    private String formatSalesPrice;
    
    @JSONField(name="sales_price")
    private Integer salesPrice;
    
    @JSONField(name="is_outlets")
    private String isOutlets;
    
    @JSONField(name="is_collect")
    private String isCollect;
    
	@JSONField(name="product_url")
    private String productUrl;
    
    @JSONField(name="storage_sum")
    private Integer storageSum;
    
    @JSONField(name="brand_info")
    private BrandVo brand;
    
    @JSONField(name="tags")
    private List<String> tags;
    
    @JSONField(name="vip")
    private List<VipPriceVo> vip;
    
    @JSONField(name="vip_price")
    private String vipPrice;
    
    /**
     * 是否限购
     */
    @JSONField(name="isLimitBuy")
    private boolean isLimitBuy;
   
	/**
	 * 限购商品的code,如果该商品是限量商品，需要此code来做跳转
	 */
	@JSONField(name="limitProductCode")
	private String limitProductCode;

	/**
	 * 限购商品指定sku,如果该商品是限量商品，需要此sku做默认尺码选中
	 */
	@JSONField(name="limitProductSku")
	private String limitProductSku;

	/**
	 * 展示状态
	 * 1.开售前 立即分享获得限购码(如果已经抢光显示限购码已经被抢光,获取限购码成功之后按钮变成即将开售，如果有限购码就直接显示即将开售)
	 * 2.开售后 如果售罄所有按钮均不展示，如果限购码被抢光显示立即购买不可点，如果有限购码，直接显示立即购买
	 *
	 * 1.立即分享限购码
	 * 2.已经抢光
	 * 3.已经售罄
	 * 4.立即购买
	 * 5.限购码已抢光
	 * 6.即将开售
	 */
	@JSONField(name="showStatus")
	private int showStatus;

	/**
	 * 销售状态
	 * 0.开售前
	 * 1.开售后
	 */
    @JSONField(name="saleStatus")
	private int saleStatus;

	/**
	 * 返有货币
	 */
	@JSONField(name="yohoCoinNum")
	private String yohoCoinNum;

	public Double getVipSilver() {
		return vipSilver;
	}
	public void setVipSilver(Double vipSilver) {
		this.vipSilver = vipSilver;
	}
	public Double getVipGold() {
		return vipGold;
	}
	public void setVipGold(Double vipGold) {
		this.vipGold = vipGold;
	}
	public Double getVipPlatinum() {
		return vipPlatinum;
	}
	public void setVipPlatinum(Double vipPlatinum) {
		this.vipPlatinum = vipPlatinum;
	}
	@JSONField(name="promotion")
	private List<PromotionVo> promotionVoList;

	/**
	 * skc(颜色)
	 */
	@JSONField(name="goods_list")
	private List<GoodsVo> goodsList;
	/**
	 * 分类信息
	 */
	@JSONField(name="category_info")
	private List<CategoryVo> categoryVoList;

	@JSONField(name="info")
	private CommentAndConsultVo commentAndConsultVo;

	@JSONField(name="shop")
	private List<ShopsVo> shopVoList;

	public CommentAndConsultVo getCommentAndConsultVo() {
		return commentAndConsultVo;
	}
	public void setCommentAndConsultVo(CommentAndConsultVo commentAndConsultVo) {
		this.commentAndConsultVo = commentAndConsultVo;
	}

	public boolean isLimitBuy() {
		return isLimitBuy;
	}

	public void setLimitBuy(boolean isLimitBuy) {
		this.isLimitBuy = isLimitBuy;
	}

	public String getLimitProductCode() {
		return limitProductCode;
	}

	public String getLimitProductSku() {
		return limitProductSku;
	}

	public void setLimitProductSku(String limitProductSku) {
		this.limitProductSku = limitProductSku;
	}

	public void setLimitProductCode(String limitProductCode) {
		this.limitProductCode = limitProductCode;
	}

	public int getShowStatus() {
		return showStatus;
	}

	public void setShowStatus(int showStatus) {
		this.showStatus = showStatus;
	}

	public int getSaleStatus() {
		return saleStatus;
	}

	public void setSaleStatus(int saleStatus) {
		this.saleStatus = saleStatus;
	}

	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getProductSkn() {
		return productSkn;
	}
	public void setProductSkn(Integer productSkn) {
		this.productSkn = productSkn;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getCnAlphabet() {
		return cnAlphabet;
	}
	public void setCnAlphabet(String cnAlphabet) {
		this.cnAlphabet = cnAlphabet;
	}
	public String getPhrase() {
		return phrase;
	}
	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}
	public String getSalesPhrase() {
		return salesPhrase;
	}
	public void setSalesPhrase(String salesPhrase) {
		this.salesPhrase = salesPhrase;
	}
	public String getIsAdvance() {
		return isAdvance;
	}
	public void setIsAdvance(String isAdvance) {
		this.isAdvance = isAdvance;
	}
	public Integer getAttribute() {
		return attribute;
	}
	public void setAttribute(Integer attribute) {
		this.attribute = attribute;
	}
	public String getExpectArrivalTime() {
		return expectArrivalTime;
	}
	public void setExpectArrivalTime(String expectArrivalTime) {
		this.expectArrivalTime = expectArrivalTime;
	}
	public String getFormatMarketPrice() {
		return formatMarketPrice;
	}
	public void setFormatMarketPrice(String formatMarketPrice) {
		this.formatMarketPrice = formatMarketPrice;
	}
	public Integer getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(Integer marketPrice) {
		this.marketPrice = marketPrice;
	}
	public String getFormatSalesPrice() {
		return formatSalesPrice;
	}
	public void setFormatSalesPrice(String formatSalesPrice) {
		this.formatSalesPrice = formatSalesPrice;
	}
	public Integer getSalesPrice() {
		return salesPrice;
	}

	public void setSalesPrice(Integer salesPrice) {
		this.salesPrice = salesPrice;
	}

	public String getIsCollect() {
		return isCollect;
	}

	public void setIsCollect(String isCollect) {
		this.isCollect = isCollect;
	}

	public String getIsOutlets() {
		return isOutlets;
	}
	public void setIsOutlets(String isOutlets) {
		this.isOutlets = isOutlets;
	}
	public String getProductUrl() {
		return productUrl;
	}
	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}
	public Integer getStorageSum() {
		return storageSum;
	}
	public void setStorageSum(Integer storageSum) {
		this.storageSum = storageSum;
	}
	public BrandVo getBrand() {
		return brand;
	}
	public void setBrand(BrandVo brand) {
		this.brand = brand;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public List<VipPriceVo> getVip() {
		return vip;
	}
	public void setVip(List<VipPriceVo> vip) {
		this.vip = vip;
	}
	public String getVipPrice() {
		return vipPrice;
	}
	public void setVipPrice(String vipPrice) {
		this.vipPrice = vipPrice;
	}
	public List<PromotionVo> getPromotionVoList() {
		return promotionVoList;
	}
	public void setPromotionVoList(List<PromotionVo> promotionVoList) {
		this.promotionVoList = promotionVoList;
	}
	public List<GoodsVo> getGoodsList() {
		return goodsList;
	}
	public void setGoodsList(List<GoodsVo> goodsList) {
		this.goodsList = goodsList;
	}
	public List<CategoryVo> getCategoryVoList() {
		return categoryVoList;
	}
	public void setCategoryVoList(List<CategoryVo> categoryVoList) {
		this.categoryVoList = categoryVoList;
	}
	public String getYohoCoinNum() {
		return yohoCoinNum;
	}
	public void setYohoCoinNum(String yohoCoinNum) {
		this.yohoCoinNum = yohoCoinNum;
	}
}
