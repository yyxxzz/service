package com.yoho.gateway.model.browse;

import com.yoho.service.model.BaseBO;

public class ProductVO extends BaseBO {
	private static final long serialVersionUID = -4011940568952152987L;

	// 商品名称
	private String product_name;
	// 商品ID
	private int product_id;
	// 商品SKN
	private int product_skn;
	// 图片
	private String image;
	// 销售价格
	private double sales_price;
	// 市场价格
	private double market_price;
	// 上架/下架
	private int status;
	// 库存
	private int storage;
	// 分类ID
	private int category_id;
	// 浏览时间
	private String time;

	public ProductVO() {
	}

	public ProductVO(String product_name, int product_id, int product_skn, String image, double sales_price, double market_price, int status, int storage, int category_id, String time) {
		this.product_name = product_name;
		this.product_id = product_id;
		this.product_skn = product_skn;
		this.image = image;
		this.sales_price = sales_price;
		this.market_price = market_price;
		this.status = status;
		this.storage = storage;
		this.category_id = category_id;
		this.time = time;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
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

	public double getSales_price() {
		return sales_price;
	}

	public void setSales_price(double sales_price) {
		this.sales_price = sales_price;
	}

	public double getMarket_price() {
		return market_price;
	}

	public void setMarket_price(double market_price) {
		this.market_price = market_price;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStorage() {
		return storage;
	}

	public void setStorage(int storage) {
		this.storage = storage;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
