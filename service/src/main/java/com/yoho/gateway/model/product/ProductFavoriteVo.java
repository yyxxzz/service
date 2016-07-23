package com.yoho.gateway.model.product;

/**
 * 收藏商品的Vo对象
 * @author mali
 *
 */
public class ProductFavoriteVo {
	private String product_name;
	private int attribute;
	private int product_id;
	private int product_skn;
	private String image;
	private String sales_price;
	private String market_price;
	private int status;
	private float price_down;
	private int storage;
	private int goodsId;
	private String cnAlphabet;
	/**
	 * 商品的二级分类Id
	 */
	private Integer category_id;

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public int getAttribute() {
		return attribute;
	}

	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public int getProduct_skn() {
		return product_skn;
	}

	public void setProduct_skn(int product_skn) {
		this.product_skn = product_skn;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getSales_price() {
		return sales_price;
	}

	public void setSales_price(String sales_price) {
		this.sales_price = sales_price;
	}

	public String getMarket_price() {
		return market_price;
	}

	public void setMarket_price(String market_price) {
		this.market_price = market_price;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public float getPrice_down() {
		return price_down;
	}

	public void setPrice_down(float price_down) {
		this.price_down = price_down;
	}

	public int getStorage() {
		return storage;
	}

	public void setStorage(int storage) {
		this.storage = storage;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public String getCnAlphabet() {
		return cnAlphabet;
	}

	public void setCnAlphabet(String cnAlphabet) {
		this.cnAlphabet = cnAlphabet;
	}

	public Integer getCategory_id() {
		return category_id;
	}

	public void setCategory_id(Integer category_id) {
		this.category_id = category_id;
	}

	@Override
	public String toString() {
		return "ProductFavoriteVo [product_name=" + product_name
				+ ", attribute=" + attribute + ", product_id=" + product_id
				+ ", product_skn=" + product_skn + ", image=" + image
				+ ", sales_price=" + sales_price + ", market_price="
				+ market_price + ", status=" + status + ", price_down="
				+ price_down + ", storage=" + storage + ", goodsId=" + goodsId
				+ ", cnAlphabet=" + cnAlphabet + ", category_id=" + category_id
				+ "]";
	}
}
