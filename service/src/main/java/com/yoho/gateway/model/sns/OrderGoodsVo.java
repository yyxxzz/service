package com.yoho.gateway.model.sns;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * OrdersGoods
 * 订单商品类
 *
 * @author zhangyonghui
 * @date 2015/11/3
 */
public class OrderGoodsVo implements Serializable {


    private Integer productId;

    private Integer goodsId;

    private String goods_type;

    private BigDecimal goods_price;

    private BigDecimal goods_amount;

    private Integer buy_number;

    private String size_name;

    private String color_name;

    private String goods_image;

    private String pruduct_name;
    
    private String product_url;

    private BrandVo brand;


    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(String goods_type) {
        this.goods_type = goods_type;
    }

    public BigDecimal getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(BigDecimal goods_price) {
        this.goods_price = goods_price;
    }

    public BigDecimal getGoods_amount() {
        return goods_amount;
    }

    public void setGoods_amount(BigDecimal goods_amount) {
        this.goods_amount = goods_amount;
    }

    public Integer getBuy_number() {
        return buy_number;
    }

    public void setBuy_number(Integer buy_number) {
        this.buy_number = buy_number;
    }

    public String getSize_name() {
        return size_name;
    }

    public void setSize_name(String size_name) {
        this.size_name = size_name;
    }

    public String getColor_name() {
        return color_name;
    }

    public void setColor_name(String color_name) {
        this.color_name = color_name;
    }

    public String getGoods_image() {
        return goods_image;
    }

    public void setGoods_image(String goods_image) {
        this.goods_image = goods_image;
    }

    public BrandVo getBrand() {
        return brand;
    }

    public void setBrand(BrandVo brand) {
        this.brand = brand;
    }

	public String getProduct_url() {
		return product_url;
	}

	public void setProduct_url(String product_url) {
		this.product_url = product_url;
	}

	public String getPruduct_name() {
		return pruduct_name;
	}

	public void setPruduct_name(String pruduct_name) {
		this.pruduct_name = pruduct_name;
	}
}